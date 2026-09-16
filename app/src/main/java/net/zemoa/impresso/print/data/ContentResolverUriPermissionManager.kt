package net.zemoa.impresso.print.data

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import net.zemoa.impresso.print.domain.UriPermissionManager

class ContentResolverUriPermissionManager(
    private val contentResolver: ContentResolver,
) : UriPermissionManager {
    override fun takeReadPermission(uri: Uri): Boolean = try {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        true
    } catch (_: SecurityException) {
        false
    }

    override fun releaseReadPermission(uri: Uri) {
        try {
            contentResolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // The provider did not grant persistable access or access has already been released.
        }
    }
}
