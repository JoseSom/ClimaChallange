package com.example.climachallange.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.io.IOException

fun checkForInternet(context: Context): Boolean {
    // Registrar la actividad con el servicio connectivity manager
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    // Si la versión de Android es M o mayor se usa NetworkCaoabilities para verificar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // Devuelve un objeto de tipo Network correspondiente a la conectividad del dispositivo
        val network = connectivityManager.activeNetwork ?: return false
        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indica si la red usa transporte WiFi o tiene conectividad WiFi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // Inidca si la red tiene conectividad por datos móviles
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            else -> false
        }
    } else {
        // Si la versión de Android es menor a M
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}

val Any.TAG: String
    get() {
        return javaClass.simpleName
    }

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}



