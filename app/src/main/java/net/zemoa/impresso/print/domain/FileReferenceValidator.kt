package net.zemoa.impresso.print.domain

import android.net.Uri

/** Validates metadata and read access for an Android document-provider reference. */
interface FileReferenceValidator {
    suspend fun validate(uri: Uri): FileValidationResult
}
