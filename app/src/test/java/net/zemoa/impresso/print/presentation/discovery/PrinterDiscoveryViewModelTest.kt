package net.zemoa.impresso.print.presentation.discovery

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.zemoa.impresso.app.DiscoveryCoordinator
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoveryState
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class PrinterDiscoveryViewModelTest {
    @Test
    fun `retry delegates to the application discovery coordinator`() {
        val coordinator = FakeCoordinator()
        val viewModel = PrinterDiscoveryViewModel(coordinator)

        viewModel.retry()

        assertEquals(1, coordinator.retryCount)
    }

    @Test
    fun `initial state projects discovered printers for the screen`() {
        val printer = DiscoveredPrinter(
            id = "printer-1",
            name = "Epson XP-6000",
            model = "XP-6000",
            networkAddress = "192.168.1.30",
            port = 631,
            availability = PrinterAvailability.ACTIVE,
        )
        val coordinator = FakeCoordinator(
            PrinterDiscoveryState(PrinterDiscoveryStatus.RESULTS_AVAILABLE, listOf(printer)),
        )

        val viewModel = PrinterDiscoveryViewModel(coordinator)

        assertEquals(PrinterDiscoveryStatus.RESULTS_AVAILABLE, viewModel.uiState.value.status)
        assertEquals(listOf(printer), viewModel.uiState.value.printers)
    }

    private class FakeCoordinator(state: PrinterDiscoveryState = PrinterDiscoveryState()) : DiscoveryCoordinator {
        override val state: StateFlow<PrinterDiscoveryState> = MutableStateFlow(state)
        var retryCount = 0

        override fun start() = Unit

        override fun retry() {
            retryCount += 1
        }

        override suspend fun recheck(printer: DiscoveredPrinter): Boolean = true

        override fun stop() = Unit
    }
}
