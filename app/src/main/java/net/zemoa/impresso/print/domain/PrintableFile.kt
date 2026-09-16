package net.zemoa.impresso.print.domain

import android.net.Uri

/** A validated document reference retained only for the active printing flow. */
data class PrintableFile(
    val uri: Uri,
    val displayName: String,
    val type: PrintableFileType,
    val declaredMimeType: String?,
)
