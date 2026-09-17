package net.zemoa.impresso.print.domain

import kotlinx.coroutines.flow.Flow

/** Finds printers through one local-network protocol and can revalidate a discovered endpoint. */
interface PrinterDiscoverySource {
    fun discover(): Flow<DiscoveredPrinter>

    suspend fun isAvailable(printer: DiscoveredPrinter): Boolean
}
