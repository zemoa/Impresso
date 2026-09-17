package net.zemoa.impresso.print.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Checks whether the active Android network is a conventional Wi-Fi network. */
fun interface WifiNetworkAvailability {
    fun isWifiConnected(): Boolean
}

class AndroidWifiNetworkAvailability(context: Context) : WifiNetworkAvailability {
    private val connectivityManager = context.applicationContext.getSystemService(ConnectivityManager::class.java)

    override fun isWifiConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
