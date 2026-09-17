package net.zemoa.impresso.app

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import net.zemoa.impresso.print.data.WifiNetworkAvailability
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterDiscoveryRepository
import net.zemoa.impresso.print.domain.PrinterDiscoverySource
import net.zemoa.impresso.print.domain.PrinterDiscoveryState
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus

/** Owns one cancellable, local-only discovery run for the application session. */
interface DiscoveryCoordinator {
    val state: StateFlow<PrinterDiscoveryState>

    fun start()

    fun retry()

    suspend fun recheck(printer: DiscoveredPrinter): Boolean

    fun stop()
}

class DefaultDiscoveryCoordinator(
    private val wifiNetworkAvailability: WifiNetworkAvailability,
    private val discoverySource: PrinterDiscoverySource,
    private val repository: PrinterDiscoveryRepository,
    private val timeoutMillis: Long = DISCOVERY_TIMEOUT_MILLIS,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : DiscoveryCoordinator {
    private val _state = MutableStateFlow(PrinterDiscoveryState())
    override val state: StateFlow<PrinterDiscoveryState> = _state.asStateFlow()
    private var discoveryJob: Job? = null
    private var hasStarted = false

    override fun start() {
        if (hasStarted) {
            return
        }
        hasStarted = true
        launchDiscovery(clearResults = false)
    }

    override fun retry() {
        hasStarted = true
        launchDiscovery(clearResults = true)
    }

    override suspend fun recheck(printer: DiscoveredPrinter): Boolean {
        val isAvailable = try {
            discoverySource.isAvailable(printer)
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Throwable) {
            false
        }
        if (!isAvailable) {
            repository.markInactive(printer.id)
            updateState(_state.value.status)
        }
        return isAvailable
    }

    override fun stop() {
        discoveryJob?.cancel()
        scope.cancel()
    }

    private fun launchDiscovery(clearResults: Boolean) {
        discoveryJob?.cancel()
        if (clearResults) {
            repository.clear()
            updateState(PrinterDiscoveryStatus.RUNNING)
        }
        discoveryJob = scope.launch {
            if (!wifiNetworkAvailability.isWifiConnected()) {
                updateState(PrinterDiscoveryStatus.WIFI_FAILURE)
                return@launch
            }

            updateState(PrinterDiscoveryStatus.RUNNING)
            try {
                withTimeoutOrNull(timeoutMillis) {
                    discoverySource.discover().collect { printer ->
                        repository.upsert(printer)
                        updateState(PrinterDiscoveryStatus.RESULTS_AVAILABLE)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Throwable) {
                // Technical discovery failures are presented as an empty result set in the MVP.
            }
            if (repository.printers.value.isEmpty()) {
                updateState(PrinterDiscoveryStatus.NO_RESULTS)
            } else {
                updateState(PrinterDiscoveryStatus.RESULTS_AVAILABLE)
            }
        }
    }

    private fun updateState(status: PrinterDiscoveryStatus) {
        _state.value = PrinterDiscoveryState(status, repository.printers.value)
    }

    private companion object {
        const val DISCOVERY_TIMEOUT_MILLIS = 30_000L
    }
}
