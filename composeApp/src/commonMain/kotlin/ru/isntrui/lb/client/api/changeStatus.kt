package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.enums.TaskStatus
import ru.isntrui.lb.client.models.task.Task

suspend fun changeStatus(client: HttpClient, task: Task, status: TaskStatus) {
    println(client.put {
        url("task/${task.id}/setStatus?status=$status")
        contentType(ContentType.Application.Json)
    }.status)
}