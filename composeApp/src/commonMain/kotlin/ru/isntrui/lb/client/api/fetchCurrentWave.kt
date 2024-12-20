package ru.isntrui.lb.client.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.WaveStatus

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun fetchCurrentWave(client: HttpClient): Wave {
    return try {
        val response: HttpResponse = client.get("wave/current") {
            contentType(ContentType.Application.Json)
        }

        if (response.status.value == 200) {
            val responseBody = response.bodyAsText()
            println(responseBody)
            json.decodeFromString<Wave>(responseBody)
        } else {
            println("Error fetching tasks: ${response.status}")
            Wave(
                status = WaveStatus.PLANNED,
                startsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            )
        }
    } catch (e: SerializationException) {
        println("Serialization error: ${e.message}")
        Wave(status = WaveStatus.PLANNED,
            startsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    } catch (e: Exception) {
        println("Error fetching tasks: ${e.message}")
        Wave(status = WaveStatus.PLANNED,
            startsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }
}