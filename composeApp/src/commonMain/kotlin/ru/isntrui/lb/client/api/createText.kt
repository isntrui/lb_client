package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.TextI

suspend fun createText(client: HttpClient, text: TextI) {
    println(text)
    println(client.post {
        url("text/save")
        contentType(ContentType.Application.Json)
        setBody(text)
    }.status)
}