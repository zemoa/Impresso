package net.zemoa.impresso.print.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DiscoveredPrinterTest {
    @Test
    fun `stable identifier takes precedence over endpoint data`() {
        val identifier = printerIdentifier(
            stableIdentifier = "  UUID-123  ",
            networkAddress = "192.168.1.30",
            port = 631,
            name = "Epson",
            model = "XP-6000",
        )

        assertEquals("stable:uuid-123", identifier)
    }

    @Test
    fun `endpoint is used when no stable identifier is advertised`() {
        val identifier = printerIdentifier(
            stableIdentifier = null,
            networkAddress = "192.168.1.30",
            port = 631,
            name = "Epson",
            model = "XP-6000",
        )

        assertEquals("endpoint:192.168.1.30:631", identifier)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `identifier rejects a printer without identifying data`() {
        printerIdentifier(null, null, null, null, null)
    }
}
