package ru.isntrui.lb.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import ru.isntrui.lb.client.storage.TokenStorage

sealed class Net {
    companion object Client {
        private var httpClient = HttpClient {
            if (TokenStorage.getToken() != null) {
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(TokenStorage.getToken()!!, null)
                        }
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }

        fun recreate() {
            httpClient = HttpClient {
                if (TokenStorage.getToken() != null) {
                    install(Auth) {
                        bearer {
                            loadTokens {
                                BearerTokens(TokenStorage.getToken()!!, null)
                            }
                        }
                    }
                }
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                    })
                }
            }
        }

        fun client() : HttpClient {
            return httpClient
        }
    }
}