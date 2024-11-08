package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.task.Task

suspend fun deleteTask(client: HttpClient, task: Task) {
    println(client.delete {
        url("task/${task.id}")
        contentType(ContentType.Application.Json)
    }.status)
}