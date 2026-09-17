package net.zemoa.impresso.print.domain

/** A printer discovered on the local network during the current application session. */
data class DiscoveredPrinter(
    val id: String,
    val name: String?,
    val model: String?,
    val networkAddress: String?,
    val port: Int?,
    val availability: PrinterAvailability,
    val origin: PrinterDiscoveryOrigin = PrinterDiscoveryOrigin.LOCAL_WIFI,
)

enum class PrinterAvailability {
    ACTIVE,
    INACTIVE,
}

enum class PrinterDiscoveryOrigin {
    LOCAL_WIFI,
}

/** Builds a session identifier from the best identity information available from discovery. */
fun printerIdentifier(
    stableIdentifier: String?,
    networkAddress: String?,
    port: Int?,
    name: String?,
    model: String?,
): String {
    stableIdentifier?.trim()?.takeIf(String::isNotEmpty)?.let { return "stable:${it.lowercase()}" }
    networkAddress?.trim()?.takeIf(String::isNotEmpty)?.let { address ->
        return "endpoint:${address.lowercase()}:${port ?: 0}"
    }
    val description = listOfNotNull(name, model)
        .joinToString("|")
        .trim()
        .lowercase()
    require(description.isNotEmpty()) { "A discovered printer requires identifying information." }
    return "description:$description"
}
