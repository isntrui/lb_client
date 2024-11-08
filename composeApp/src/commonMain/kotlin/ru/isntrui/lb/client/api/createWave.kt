package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Wave

suspend fun createWave(client: HttpClient, wave: Wave) {
    println(client.post {
        url("wave/create")
        contentType(ContentType.Application.Json)
        setBody(wave)
    }.status)
}