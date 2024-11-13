package ru.isntrui.lb.client.utils

import java.net.InetAddress

actual object NetworkUtils {
    actual fun isNetworkAvailable(): Boolean {
        return try {
            val address = InetAddress.getByName("google.com")
            !address.equals("")
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}