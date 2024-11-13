
package ru.isntrui.lb.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.storage.TokenStorage

actual fun createHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(TokenStorage.getToken() ?: "", null)
                }
            }
        }
        defaultRequest {
            url("http://igw.isntrui.ru/api/")
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