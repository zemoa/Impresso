package net.zemoa.impresso.print.data

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import net.zemoa.impresso.print.domain.DiscoveredPrinter
import net.zemoa.impresso.print.domain.PrinterAvailability
import net.zemoa.impresso.print.domain.PrinterDiscoverySource
import net.zemoa.impresso.print.domain.printerIdentifier

/** Discovers AirPrint-compatible Epson XP-6000 printers through local DNS-SD only. */
class NsdEpsonPrinterDiscoverySource(
    private val nsdManager: NsdManager,
    private val serviceTypes: List<String> = listOf(IPP_SERVICE_TYPE, IPPS_SERVICE_TYPE),
) : PrinterDiscoverySource {
    override fun discover(): Flow<DiscoveredPrinter> = callbackFlow {
        val listeners = mutableListOf<NsdManager.DiscoveryListener>()
        serviceTypes.forEach { serviceType ->
            val listener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) = Unit

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    nsdManager.resolveService(
                        serviceInfo,
                        object : NsdManager.ResolveListener {
                            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit

                            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                serviceInfo.toEpsonXp6000Printer()?.let(::trySend)
                            }
                        },
                    )
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo) = Unit

                override fun onDiscoveryStopped(serviceType: String) = Unit

                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) = Unit

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) = Unit
            }
            listeners += listener
            nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
        }

        awaitClose {
            listeners.forEach { listener ->
                runCatching { nsdManager.stopServiceDiscovery(listener) }
            }
        }
    }

    override suspend fun isAvailable(printer: DiscoveredPrinter): Boolean = withContext(Dispatchers.IO) {
        val address = printer.networkAddress ?: return@withContext false
        val port = printer.port ?: return@withContext false
        runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(address, port), AVAILABILITY_TIMEOUT_MILLIS)
            }
            true
        }.getOrDefault(false)
    }

    private fun NsdServiceInfo.toEpsonXp6000Printer(): DiscoveredPrinter? {
        val model = attribute("ty") ?: attribute("product")?.trim('(', ')')
        if (!listOfNotNull(serviceName, model).any { it.contains(XP_6000_MODEL, ignoreCase = true) }) {
            return null
        }
        val address = host?.hostAddress
        val name = serviceName.takeIf { it.isNotBlank() }
        val id = printerIdentifier(
            stableIdentifier = attribute("UUID"),
            networkAddress = address,
            port = port.takeIf { it > 0 },
            name = name,
            model = model,
        )
        return DiscoveredPrinter(
            id = id,
            name = name,
            model = model,
            networkAddress = address,
            port = port.takeIf { it > 0 },
            availability = PrinterAvailability.ACTIVE,
        )
    }

    private fun NsdServiceInfo.attribute(name: String): String? = attributes.entries
        .firstOrNull { it.key.equals(name, ignoreCase = true) }
        ?.value
        ?.toString(StandardCharsets.UTF_8)
        ?.takeIf { it.isNotBlank() }

    private companion object {
        const val IPP_SERVICE_TYPE = "_ipp._tcp."
        const val IPPS_SERVICE_TYPE = "_ipps._tcp."
        const val XP_6000_MODEL = "XP-6000"
        const val AVAILABILITY_TIMEOUT_MILLIS = 3_000
    }
}
