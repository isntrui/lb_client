package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Wave

suspend fun deleteWave(client: HttpClient, wave: Wave) {
    println(client.delete {
        url("wave/${wave.id}/delete")
        contentType(ContentType.Application.Json)
    }.status)
}