package net.zemoa.impresso.print.presentation.discovery

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PrinterDiscoveryScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun runningDiscoveryShowsProgress() {
        setContent(PrinterDiscoveryUiState(status = PrinterDiscoveryStatus.RUNNING))

        composeRule.onNodeWithTag("printer-discovery-loading").assertIsDisplayed()
        composeRule.onNodeWithText("Searching for printers...").assertIsDisplayed()
    }

    @Test
    fun discoveredPrinterShowsItsIdentityAndAvailability() {
        setContent(
            PrinterDiscoveryUiState(
                status = PrinterDiscoveryStatus.RESULTS_AVAILABLE,
                printers = listOf(printer()),
            ),
        )

        composeRule.onNodeWithTag("printer-discovery-results").assertIsDisplayed()
        composeRule.onNodeWithText("Epson XP-6000").assertIsDisplayed()
        composeRule.onNodeWithText("Model: XP-6000").assertIsDisplayed()
        composeRule.onNodeWithText("Network address: 192.168.1.30").assertIsDisplayed()
        composeRule.onNodeWithText("Available").assertIsDisplayed()
    }

    @Test
    fun wifiFailureExplainsRecoveryAndRetries() {
        var retried = false
        setContent(
            PrinterDiscoveryUiState(status = PrinterDiscoveryStatus.WIFI_FAILURE),
            onRetry = { retried = true },
        )

        composeRule.onNodeWithTag("printer-discovery-wifi-failure").assertIsDisplayed()
        composeRule.onNodeWithTag("retry-printer-discovery").performClick()

        assertTrue(retried)
    }

    private fun setContent(
        state: PrinterDiscoveryUiState,
        onRetry: () -> Unit = {},
    ) {
        composeRule.setContent {
            MaterialTheme {
                PrinterDiscoveryContent(state, onRetry)
            }
        }
    }

    private fun printer() = DiscoveredPrinter(
        id = "printer-1",
        name = "Epson XP-6000",
        model = "XP-6000",
        networkAddress = "192.168.1.30",
        port = 631,
        availability = PrinterAvailability.ACTIVE,
    )
}
