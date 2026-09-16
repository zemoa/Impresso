package net.zemoa.impresso.print.domain

import android.net.Uri

/** Retains and releases document-provider read grants for the active printing flow. */
interface UriPermissionManager {
    fun takeReadPermission(uri: Uri): Boolean

    fun releaseReadPermission(uri: Uri)
}
