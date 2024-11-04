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

suspend fun fetchCurrentUser(client: HttpClient): User {
    val response: HttpResponse = client.get("http://igw.isntrui.ru/api/user/")
    val responseBody = response.bodyAsText()
    println(responseBody)
    return json.decodeFromString<User>(responseBody)
}