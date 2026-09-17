package net.zemoa.impresso.print.domain

data class PrinterDiscoveryState(
    val status: PrinterDiscoveryStatus = PrinterDiscoveryStatus.NOT_STARTED,
    val printers: List<DiscoveredPrinter> = emptyList(),
)

enum class PrinterDiscoveryStatus {
    NOT_STARTED,
    RUNNING,
    RESULTS_AVAILABLE,
    NO_RESULTS,
    WIFI_FAILURE,
}
