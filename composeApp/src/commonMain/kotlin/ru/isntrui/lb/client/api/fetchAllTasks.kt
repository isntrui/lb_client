package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.task.Task

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllTasks(client: HttpClient): List<Task> {
    val response: HttpResponse = client.get("task/all")
    val responseBody = response.bodyAsText()
    return json.decodeFromString<List<Task>>(responseBody)
}