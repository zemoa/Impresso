package net.zemoa.impresso.print.presentation.discovery

import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterDiscoveryState
import net.zemoa.impresso.print.domain.PrinterDiscoveryStatus

data class PrinterDiscoveryUiState(
    val status: PrinterDiscoveryStatus = PrinterDiscoveryStatus.NOT_STARTED,
    val printers: List<DiscoveredPrinter> = emptyList(),
) {
    val isDiscovering: Boolean
        get() = status == PrinterDiscoveryStatus.RUNNING

    val canRetry: Boolean
        get() = status == PrinterDiscoveryStatus.NO_RESULTS || status == PrinterDiscoveryStatus.WIFI_FAILURE
}

fun PrinterDiscoveryState.toUiState() = PrinterDiscoveryUiState(status, printers)
