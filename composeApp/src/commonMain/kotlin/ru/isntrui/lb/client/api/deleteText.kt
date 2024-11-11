package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.TextI

suspend fun deleteText(client: HttpClient, textI: TextI) {
    println(client.delete {
        url("text/${textI.id}")
        contentType(ContentType.Application.Json)
    }.status)
}