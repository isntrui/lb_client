package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.requests.TaskRequest

suspend fun createSong(client: HttpClient, taskRequest: TaskRequest) {
    println(client.post {
        url("song/create")
        contentType(ContentType.Application.Json)
        setBody(taskRequest)
    }.status)
}