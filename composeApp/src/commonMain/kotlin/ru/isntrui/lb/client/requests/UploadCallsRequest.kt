package ru.isntrui.lb.client.requests

import ru.isntrui.lb.client.models.Song

data class UploadSongsRequest(val songs: List<Song>)