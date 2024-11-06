package ru.isntrui.lb.client.utils

import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.models.Song
import ru.isntrui.lb.client.requests.UploadSongsRequest

fun parseSongsFile(file: String): List<Song> {
    // Implement file parsing logic here
    return emptyList()
}

fun validateSongs(songs: List<Song>): Boolean {
    // Implement validation logic here
    return true
}

suspend fun uploadSongs(songs: List<Song>) {
    // Implement upload logic here
    val request = UploadSongsRequest(songs)
    //Net.client().uploadSongs(request)
}