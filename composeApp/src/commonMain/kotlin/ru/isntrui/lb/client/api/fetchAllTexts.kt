package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.TextI

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllTexts(client: HttpClient): List<TextI> {
    val response: HttpResponse = client.get("text/getAll") {
        contentType(ContentType.Application.Json)
    }
    val responseBody = response.bodyAsText()
    println(responseBody)
    return json.decodeFromString<List<TextI>>(responseBody)
}