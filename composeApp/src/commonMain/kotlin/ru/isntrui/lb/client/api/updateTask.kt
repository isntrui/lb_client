package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.task.Task

suspend fun updateTask(client: HttpClient, task: Task) {
    println(client.put {
        url("task/${task.id}/update")
        contentType(ContentType.Application.Json)
        setBody(task)
    }.status)
}