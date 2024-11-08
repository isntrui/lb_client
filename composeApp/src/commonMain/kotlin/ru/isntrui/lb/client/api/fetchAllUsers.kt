package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.User

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllUsers(client: HttpClient): List<User> {
    val response: HttpResponse = client.get("user/all")
    val responseBody = response.bodyAsText()
    return json.decodeFromString<List<User>>(responseBody)
}