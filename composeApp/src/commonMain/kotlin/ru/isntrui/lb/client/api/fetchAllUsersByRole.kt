package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.models.task.UserTask

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchAllUsersByRole(client: HttpClient, role: Role): List<UserTask> {
    val response: HttpResponse = client.get("user/getAllByRole?role=$role")
    val responseBody = response.bodyAsText()
    return json.decodeFromString<List<UserTask>>(responseBody)
}