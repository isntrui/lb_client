package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Song

suspend fun approveSong(client: HttpClient, song: Song, a: Boolean) {
    println(client.put {
        url("song/${song.id}/approve?approve=$a")
        contentType(ContentType.Application.Json)
    }.status)
}