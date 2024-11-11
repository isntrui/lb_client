package ru.isntrui.lb.client.api

import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.request.forms.*
import ru.isntrui.lb.client.models.enums.FileType

suspend fun uploadFile(client: HttpClient, fileName: String, fileBytes: ByteArray, type: FileType): HttpResponse {
    val response = client.submitFormWithBinaryData(
        url = "aws/upload?type=${type}",
        formData = formData {
            append("file", fileBytes, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=$fileName")
            })
        }
    ) {
        method = HttpMethod.Post
        timeout {
            requestTimeoutMillis = 300_000
            connectTimeoutMillis = 300_000
            socketTimeoutMillis = 300_000
        }
    }
    println(response.bodyAsText())
    return response
}