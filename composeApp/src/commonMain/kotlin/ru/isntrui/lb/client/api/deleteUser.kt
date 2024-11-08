package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.User

suspend fun deleteUser(client: HttpClient, user: User) {
    println(client.delete {
        url("user/${user.id}")
        contentType(ContentType.Application.Json)
    }.status)
}