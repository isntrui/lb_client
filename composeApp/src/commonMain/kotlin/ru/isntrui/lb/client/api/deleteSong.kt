package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Song

suspend fun deleteSong(client: HttpClient, song: Song) {
    println(client.delete {
        url("song/${song.id}/delete")
        contentType(ContentType.Application.Json)
    }.status)
}