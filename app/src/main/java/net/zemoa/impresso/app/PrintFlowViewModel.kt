package net.zemoa.impresso.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.zemoa.impresso.print.domain.PrintableFile

/** Owns the typed transient handoff between steps of one standalone print flow. */
class PrintFlowViewModel : ViewModel() {
    private val _selectedFile = MutableStateFlow<PrintableFile?>(null)
    val selectedFile: StateFlow<PrintableFile?> = _selectedFile.asStateFlow()

    fun selectFile(file: PrintableFile) {
        _selectedFile.value = file
    }
}
