package net.zemoa.impresso.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestScope
import net.zemoa.impresso.print.data.InMemoryPrinterDiscoveryRepository
import net.zemoa.impresso.print.data.WifiNetworkAvailability
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoverySource
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDiscoveryCoordinatorTest {
    @Test
    fun `start returns before checking the network`() = runTest {
        var networkChecks = 0
        val coordinator = DefaultDiscoveryCoordinator(
            wifiNetworkAvailability = WifiNetworkAvailability {
                networkChecks += 1
                false
            },
            discoverySource = FakeSource(emptyFlow()),
            repository = InMemoryPrinterDiscoveryRepository(),
            scope = this,
        )

        coordinator.start()

        assertEquals(0, networkChecks)
        runCurrent()
        assertEquals(1, networkChecks)
    }

    @Test
    fun `starts once and publishes discovered printers`() = runTest {
        val source = FakeSource(flowOf(printer()))
        val coordinator = coordinator(source = source)

        coordinator.start()
        coordinator.start()
        advanceUntilIdle()

        assertEquals(1, source.discoveryCount)
        assertEquals(PrinterDiscoveryStatus.RESULTS_AVAILABLE, coordinator.state.value.status)
        assertEquals(listOf(printer()), coordinator.state.value.printers)
    }

    @Test
    fun `wifi failure is reported without starting network discovery`() = runTest {
        val source = FakeSource(flowOf(printer()))
        val coordinator = coordinator(source = source, wifiConnected = false)

        coordinator.start()
        advanceUntilIdle()

        assertEquals(0, source.discoveryCount)
        assertEquals(PrinterDiscoveryStatus.WIFI_FAILURE, coordinator.state.value.status)
    }

    @Test
    fun `retry clears old results before collecting a new run`() = runTest {
        val source = FakeSource(flowOf(printer(name = "First")), flowOf(printer(id = "printer-2", name = "Second")))
        val coordinator = coordinator(source = source)

        coordinator.start()
        advanceUntilIdle()
        coordinator.retry()
        advanceUntilIdle()

        assertEquals(2, source.discoveryCount)
        assertEquals(listOf(printer(id = "printer-2", name = "Second")), coordinator.state.value.printers)
    }

    @Test
    fun `retry clears displayed results immediately`() = runTest {
        val coordinator = coordinator(source = FakeSource(flowOf(printer()), emptyFlow()))

        coordinator.start()
        advanceUntilIdle()
        coordinator.retry()

        assertEquals(PrinterDiscoveryStatus.RUNNING, coordinator.state.value.status)
        assertEquals(emptyList<DiscoveredPrinter>(), coordinator.state.value.printers)
    }

    @Test
    fun `recheck marks an unavailable printer inactive`() = runTest {
        val source = FakeSource(flowOf(printer()), available = false)
        val coordinator = coordinator(source = source)

        coordinator.start()
        advanceUntilIdle()
        val isAvailable = coordinator.recheck(printer())

        assertFalse(isAvailable)
        assertEquals(PrinterAvailability.INACTIVE, coordinator.state.value.printers.single().availability)
    }

    @Test
    fun `completed run without printers becomes no results`() = runTest {
        val coordinator = coordinator(source = FakeSource(emptyFlow()))

        coordinator.start()
        advanceUntilIdle()

        assertEquals(PrinterDiscoveryStatus.NO_RESULTS, coordinator.state.value.status)
    }

    @Test
    fun `technical source failure becomes no results`() = runTest {
        val coordinator = coordinator(source = FakeSource(flow { error("NSD failure") }))

        coordinator.start()
        advanceUntilIdle()

        assertEquals(PrinterDiscoveryStatus.NO_RESULTS, coordinator.state.value.status)
    }

    private fun TestScope.coordinator(
        source: FakeSource,
        wifiConnected: Boolean = true,
    ) = DefaultDiscoveryCoordinator(
        wifiNetworkAvailability = WifiNetworkAvailability { wifiConnected },
        discoverySource = source,
        repository = InMemoryPrinterDiscoveryRepository(),
        scope = this,
    )

    private fun printer(
        id: String = "printer-1",
        name: String = "Epson XP-6000",
    ) = DiscoveredPrinter(
        id = id,
        name = name,
        model = "XP-6000",
        networkAddress = "192.168.1.30",
        port = 631,
        availability = PrinterAvailability.ACTIVE,
    )

    private class FakeSource(
        private vararg val runs: Flow<DiscoveredPrinter>,
        private val available: Boolean = true,
    ) : PrinterDiscoverySource {
        var discoveryCount = 0

        override fun discover(): Flow<DiscoveredPrinter> {
            val run = runs[discoveryCount.coerceAtMost(runs.lastIndex)]
            discoveryCount += 1
            return run
        }

        override suspend fun isAvailable(printer: DiscoveredPrinter): Boolean = available
    }
}
