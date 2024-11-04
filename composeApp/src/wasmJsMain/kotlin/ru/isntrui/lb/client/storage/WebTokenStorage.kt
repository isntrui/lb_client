package ru.isntrui.lb.client.storage

import kotlinx.browser.localStorage

actual object TokenStorage {
    private const val TOKEN_KEY = "auth_token"

    actual fun saveToken(token: String) {
        localStorage.setItem(TOKEN_KEY, token)
    }

    actual fun getToken(): String? {
        return localStorage.getItem(TOKEN_KEY)
    }

    actual fun clearToken() {
        localStorage.removeItem(TOKEN_KEY)
    }
}