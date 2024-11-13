package ru.isntrui.lb.client

import io.ktor.client.HttpClient

sealed class Net {
    companion object Client {
        private var httpClient = createHttpClient()
        fun recreate() {
            httpClient = createHttpClient()
        }

        fun client() : HttpClient {
            return httpClient
        }
    }
}