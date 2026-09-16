package net.zemoa.impresso.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.zemoa.impresso.print.data.ContentResolverFileReferenceValidator
import net.zemoa.impresso.print.data.ContentResolverUriPermissionManager
import net.zemoa.impresso.print.domain.ValidatePrintableFileUseCase
import net.zemoa.impresso.print.presentation.selectfile.SelectPrintableFileViewModel

class AppGraph(context: Context) {
    private val contentResolver = context.applicationContext.contentResolver
    private val uriPermissionManager = ContentResolverUriPermissionManager(contentResolver)
    private val validatePrintableFile = ValidatePrintableFileUseCase(
        ContentResolverFileReferenceValidator(contentResolver),
    )

    val discoveryCoordinator: DiscoveryCoordinator = NoOpDiscoveryCoordinator()

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
}
