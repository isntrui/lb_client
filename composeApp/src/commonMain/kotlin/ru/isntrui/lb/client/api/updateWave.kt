package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Wave

suspend fun updateWave(client: HttpClient, wave: Wave) {
    println(client.put {
        url("wave/${wave.id}/changeStatus?status=${wave.status}")
        contentType(ContentType.Application.Json)
    }.status)
}