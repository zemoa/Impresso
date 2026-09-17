package net.zemoa.impresso.app

import android.content.Context
import android.net.nsd.NsdManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.zemoa.impresso.print.data.AndroidWifiNetworkAvailability
import net.zemoa.impresso.print.data.ContentResolverFileReferenceValidator
import net.zemoa.impresso.print.data.ContentResolverUriPermissionManager
import net.zemoa.impresso.print.data.InMemoryPrinterDiscoveryRepository
import net.zemoa.impresso.print.data.NsdEpsonPrinterDiscoverySource
import net.zemoa.impresso.print.domain.ValidatePrintableFileUseCase
import net.zemoa.impresso.print.presentation.discovery.PrinterDiscoveryViewModel
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileViewModel

class AppGraph(context: Context) {
    private val contentResolver = context.applicationContext.contentResolver
    private val uriPermissionManager = ContentResolverUriPermissionManager(contentResolver)
    private val validatePrintableFile = ValidatePrintableFileUseCase(
        ContentResolverFileReferenceValidator(contentResolver),
    )
    private val printerDiscoveryRepository = InMemoryPrinterDiscoveryRepository()
    private val printerDiscoverySource = NsdEpsonPrinterDiscoverySource(
        context.applicationContext.getSystemService(NsdManager::class.java),
    )

    val discoveryCoordinator: DiscoveryCoordinator = DefaultDiscoveryCoordinator(
        wifiNetworkAvailability = AndroidWifiNetworkAvailability(context),
        discoverySource = printerDiscoverySource,
        repository = printerDiscoveryRepository,
    )

    val selectPrintableFileViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(SelectPrintableFileViewModel::class.java))
            return SelectPrintableFileViewModel(
                validatePrintableFile = validatePrintableFile,
                uriPermissionManager = uriPermissionManager,
            ) as T
        }
    }

    val printerDiscoveryViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(PrinterDiscoveryViewModel::class.java))
            return PrinterDiscoveryViewModel(discoveryCoordinator) as T
        }
    }
}
