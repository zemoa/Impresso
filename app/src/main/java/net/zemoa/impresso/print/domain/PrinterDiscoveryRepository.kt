package net.zemoa.impresso.print.domain

import kotlinx.coroutines.flow.StateFlow

/** Keeps the transient, deduplicated list of printers for one application session. */
interface PrinterDiscoveryRepository {
    val printers: StateFlow<List<DiscoveredPrinter>>

    fun clear()

    fun upsert(printer: DiscoveredPrinter)

    fun markInactive(printerId: String)
}
