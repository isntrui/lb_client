package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.Invite

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllInvites(client: HttpClient): List<Invite> {
    val response: HttpResponse = client.get("invite/all")
    if (response.status.value != 200) {
        return emptyList()
    }
    try {
        val responseBody = response.bodyAsText()
        return json.decodeFromString<List<Invite>>(responseBody)
    } catch (e: Exception) {
        println("Error: $e")
        return emptyList()
    }
}