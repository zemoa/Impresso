package net.zemoa.impresso.print.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoveryRepository

class InMemoryPrinterDiscoveryRepository : PrinterDiscoveryRepository {
    private val lock = Any()
    private val _printers = MutableStateFlow<List<DiscoveredPrinter>>(emptyList())
    override val printers: StateFlow<List<DiscoveredPrinter>> = _printers.asStateFlow()

    override fun clear() {
        synchronized(lock) {
            _printers.value = emptyList()
        }
    }

    override fun upsert(printer: DiscoveredPrinter) {
        synchronized(lock) {
            val current = _printers.value
            val index = current.indexOfFirst { it.id == printer.id }
            _printers.value = if (index < 0) {
                current + printer
            } else {
                current.toMutableList().apply { set(index, printer) }
            }
        }
    }

    override fun markInactive(printerId: String) {
        synchronized(lock) {
            _printers.value = _printers.value.map { printer ->
                if (printer.id == printerId) {
                    printer.copy(availability = PrinterAvailability.INACTIVE)
                } else {
                    printer
                }
            }
        }
    }
}
