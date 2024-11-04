package ru.isntrui.lb.client.api

import io.ktor.client.*
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.task.Task

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchTasks(client: HttpClient): List<Task> {
    return try {
        val response: HttpResponse = client.get("http://igw.isntrui.ru/api/task/my")

        if (response.status.value == 200) {
            val responseBody = response.bodyAsText()
            println(responseBody)
            json.decodeFromString<List<Task>>(responseBody)
        } else {
            println("Error fetching tasks: ${response.status}")
            emptyList()
        }
    } catch (e: SerializationException) {
        println("Serialization error: ${e.message}")
        emptyList()
    } catch (e: Exception) {
        println("Error fetching tasks: ${e.message}")
        emptyList()
    }
}