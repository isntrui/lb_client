package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Design

suspend fun createDesign(client: HttpClient, design: Design) {
    println(client.post {
        url("design/create")
        contentType(ContentType.Application.Json)
        setBody(design)
    }.status)
}