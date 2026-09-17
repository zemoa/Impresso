package net.zemoa.impresso.print.data

import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryPrinterDiscoveryRepositoryTest {
    @Test
    fun `upsert keeps one entry and updates its data`() {
        val repository = InMemoryPrinterDiscoveryRepository()
        repository.upsert(printer(name = "Office"))

        repository.upsert(printer(name = "Office printer", availability = PrinterAvailability.INACTIVE))

        assertEquals(1, repository.printers.value.size)
        assertEquals("Office printer", repository.printers.value.single().name)
        assertEquals(PrinterAvailability.INACTIVE, repository.printers.value.single().availability)
    }

    @Test
    fun `inactive printers remain visible until a retry clears the session list`() {
        val repository = InMemoryPrinterDiscoveryRepository()
        repository.upsert(printer())

        repository.markInactive("printer-1")

        assertEquals(PrinterAvailability.INACTIVE, repository.printers.value.single().availability)
        repository.clear()
        assertEquals(emptyList<DiscoveredPrinter>(), repository.printers.value)
    }

    private fun printer(
        name: String = "Office",
        availability: PrinterAvailability = PrinterAvailability.ACTIVE,
    ) = DiscoveredPrinter(
        id = "printer-1",
        name = name,
        model = "XP-6000",
        networkAddress = "192.168.1.30",
        port = 631,
        availability = availability,
    )
}
