package ru.isntrui.lb.client.utils

import kotlinx.cinterop.*
import platform.SystemConfiguration.*

actual object NetworkUtils {
    @OptIn(ExperimentalForeignApi::class)
    actual fun isNetworkAvailable(): Boolean {
        val reachability = SCNetworkReachabilityCreateWithName(null, "google.com") ?: return false

        return memScoped {
            val flags = alloc<SCNetworkReachabilityFlagsVar>()
            val success = SCNetworkReachabilityGetFlags(reachability, flags.ptr)
            success && (flags.value and kSCNetworkReachabilityFlagsReachable) != 0u
        }
    }
}