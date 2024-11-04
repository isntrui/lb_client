package ru.isntrui.lb.client.storage

expect object TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}