package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.Invite

suspend fun createInvite(client: HttpClient, invite: Invite) {
    println(client.post {
        url("invite/create")
        contentType(ContentType.Application.Json)
        setBody(invite)
    }.status)
}