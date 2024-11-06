package ru.isntrui.lb.client.api

import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.request.forms.*
import ru.isntrui.lb.client.models.enums.FileType

suspend fun uploadFile(client: HttpClient, fileName: String, fileBytes: ByteArray, type: FileType): HttpResponse {
    return client.submitFormWithBinaryData(
        url = "aws/upload?type=${type}",
        formData = formData {
            append("file", fileBytes, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=$fileName")
            })
        }
    ) {
        method = HttpMethod.Post
    }
}