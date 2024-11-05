package ru.isntrui.lb.client.utils

import kotlinx.browser.window

actual object NetworkUtils {
    actual fun isNetworkAvailable(): Boolean {
        return window.navigator.onLine
    }
}