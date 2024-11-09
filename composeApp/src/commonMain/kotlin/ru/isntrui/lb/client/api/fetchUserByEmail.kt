package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

suspend fun isUserExists(client: HttpClient, email: String): Boolean {
    val response: HttpResponse = client.get("user/get?email=$email")
    return response.status == HttpStatusCode.OK
}