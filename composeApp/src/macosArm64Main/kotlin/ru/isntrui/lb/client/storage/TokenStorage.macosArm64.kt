package ru.isntrui.lb.client.storage

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.posix.memcpy

actual object TokenStorage {
    private const val serviceName = "ru.isntrui.lb.client"
    private const val accountName = "token"

    @OptIn(ExperimentalForeignApi::class)
    actual fun saveToken(token: String) {
        val tokenData = token.encodeToByteArray().toNSData()
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to accountName
        )

        SecItemDelete(query as CFDictionaryRef)

        val attributes = query + mapOf(
            kSecValueData to tokenData
        )

        val status = SecItemAdd(attributes as CFDictionaryRef, null)
        if (status != errSecSuccess) {
            throw Error("Error saving token: $status")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getToken(): String? {
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to accountName,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )

        val result = memScoped {
            val dataRef = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, dataRef.ptr)
            if (status == errSecSuccess) {
                dataRef.value as NSData
            } else {
                null
            }
        }

        return result?.toByteArray()?.decodeToString()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun clearToken() {
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to accountName
        )

        val status = SecItemDelete(query as CFDictionaryRef)
        if (status != errSecSuccess && status != errSecItemNotFound) {
            throw Error("Error clearing token: $status")
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
}