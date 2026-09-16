package net.zemoa.impresso.print.domain

import java.util.Locale

enum class PrintableFileType {
    PDF,
    JPEG,
    PNG,
    GIF,
    WEBP,
    DOCX,
    ODT,
    ;

    companion object {
        private val typesByMime = mapOf(
            "application/pdf" to PDF,
            "image/jpeg" to JPEG,
            "image/png" to PNG,
            "image/gif" to GIF,
            "image/webp" to WEBP,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to DOCX,
            "application/vnd.oasis.opendocument.text" to ODT,
        )
        private val typesByExtension = mapOf(
            "pdf" to PDF,
            "jpg" to JPEG,
            "jpeg" to JPEG,
            "png" to PNG,
            "gif" to GIF,
            "webp" to WEBP,
            "docx" to DOCX,
            "odt" to ODT,
        )

        val supportedMimeTypes: Array<String> = typesByMime.keys.toTypedArray()

        fun isUsableDeclaredMimeType(mimeType: String?): Boolean {
            val normalizedMimeType = mimeType?.trim()?.lowercase(Locale.ROOT)
            return normalizedMimeType != null && normalizedMimeType !in setOf("", "*/*", "application/octet-stream")
        }

        fun fromDeclaredMimeType(mimeType: String): PrintableFileType? =
            typesByMime[mimeType.trim().lowercase(Locale.ROOT)]

        fun fromFileName(fileName: String): PrintableFileType? {
            val extension = fileName.substringAfterLast('.', missingDelimiterValue = "")
                .lowercase(Locale.ROOT)
            return typesByExtension[extension]
        }
    }
}
