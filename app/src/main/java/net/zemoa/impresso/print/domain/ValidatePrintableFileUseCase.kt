package net.zemoa.impresso.print.domain

import android.net.Uri

/** Provides a stable entry point for all supported Android file-selection entry points. */
class ValidatePrintableFileUseCase(
    private val validator: FileReferenceValidator,
) {
    suspend operator fun invoke(uri: Uri): FileValidationResult = validator.validate(uri)
}
