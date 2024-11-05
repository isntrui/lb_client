package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.Wave

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllActualWaves(client: HttpClient): List<Wave> {
    val response: HttpResponse = client.get("wave/allActual")
    val responseBody = response.bodyAsText()
    return json.decodeFromString<List<Wave>>(responseBody)
}