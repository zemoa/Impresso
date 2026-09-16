package net.zemoa.impresso.print.presentation.selectfile

import net.zemoa.impresso.print.domain.FileSelectionError
import net.zemoa.impresso.print.domain.PrintableFile

data class SelectPrintableFileUiState(
    val selectedFile: PrintableFile? = null,
    val isValidating: Boolean = false,
    val error: FileSelectionError? = null,
) {
    val canContinue: Boolean
        get() = selectedFile != null && !isValidating
}
