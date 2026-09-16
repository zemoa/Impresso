package net.zemoa.impresso.print.domain

/** The outcome of validating a document reference before it enters the print flow. */
sealed interface FileValidationResult {
    data class Success(val file: PrintableFile) : FileValidationResult

    data class Failure(val error: FileSelectionError) : FileValidationResult
}

enum class FileSelectionError {
    UNSUPPORTED_TYPE,
    INACCESSIBLE_FILE,
    UNAVAILABLE_METADATA,
    PICKER_UNAVAILABLE,
}
