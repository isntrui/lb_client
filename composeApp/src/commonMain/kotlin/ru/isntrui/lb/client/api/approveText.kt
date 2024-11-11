package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.TextI

suspend fun approveText(client: HttpClient, textI: TextI, a: Boolean) {
    println(client.put {
        url("text/approve?approve=$a&textId=${textI.id}")
        contentType(ContentType.Application.Json)
    }.status)
}