package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.User

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchCurrentUser(client: HttpClient): User {
    val response: HttpResponse = client.get("user/") {
        contentType(ContentType.Application.Json)
    }
    val responseBody = response.bodyAsText()
    return json.decodeFromString<User>(responseBody)
}

suspend fun fetchCurrentUserResp(client: HttpClient) = client.get("user/")