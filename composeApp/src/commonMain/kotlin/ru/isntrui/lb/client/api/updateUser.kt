package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.task.Task

suspend fun updateUser(client: HttpClient, user: User) {
    println(client.put {
        url("user/${user.id}/update")
        contentType(ContentType.Application.Json)
        setBody(user)
    }.status)
}