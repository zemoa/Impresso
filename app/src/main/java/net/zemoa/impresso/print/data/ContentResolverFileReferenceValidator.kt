package net.zemoa.impresso.print.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.zemoa.impresso.print.domain.FileReferenceValidator
import net.zemoa.impresso.print.domain.FileSelectionError
import net.zemoa.impresso.print.domain.FileValidationResult
import net.zemoa.impresso.print.domain.PrintableFile
import net.zemoa.impresso.print.domain.PrintableFileType

class ContentResolverFileReferenceValidator(
    private val contentResolver: ContentResolver,
) : FileReferenceValidator {
    override suspend fun validate(uri: Uri): FileValidationResult = withContext(Dispatchers.IO) {
        try {
            val displayName = readDisplayName(uri)
                ?: return@withContext FileValidationResult.Failure(FileSelectionError.UNAVAILABLE_METADATA)
            val declaredMimeType = contentResolver.getType(uri)
            val type = resolveType(declaredMimeType, displayName)
                ?: return@withContext FileValidationResult.Failure(FileSelectionError.UNSUPPORTED_TYPE)
            contentResolver.openInputStream(uri)?.use { }
                ?: return@withContext FileValidationResult.Failure(FileSelectionError.INACCESSIBLE_FILE)

            FileValidationResult.Success(
                PrintableFile(
                    uri = uri,
                    displayName = displayName,
                    type = type,
                    declaredMimeType = declaredMimeType,
                ),
            )
        } catch (_: IOException) {
            FileValidationResult.Failure(FileSelectionError.INACCESSIBLE_FILE)
        } catch (_: SecurityException) {
            FileValidationResult.Failure(FileSelectionError.INACCESSIBLE_FILE)
        }
    }

    private fun readDisplayName(uri: Uri): String? =
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) {
                return@use null
            }
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index < 0) {
                return@use null
            }
            cursor.getString(index)?.takeIf { it.isNotBlank() }
        }

    private fun resolveType(declaredMimeType: String?, displayName: String): PrintableFileType? =
        if (PrintableFileType.isUsableDeclaredMimeType(declaredMimeType)) {
            PrintableFileType.fromDeclaredMimeType(requireNotNull(declaredMimeType))
        } else {
            PrintableFileType.fromFileName(displayName)
        }
}
