package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import ru.isntrui.lb.client.requests.TaskRequest

suspend fun createTask(client: HttpClient, taskRequest: TaskRequest) {
    println(client.post {
        url("task/")
        contentType(ContentType.Application.Json)
        setBody(taskRequest)
    }.status)
}