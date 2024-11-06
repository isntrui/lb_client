package ru.isntrui.lb.client.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.uploadFile
import ru.isntrui.lb.client.models.Song
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.Wave

import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.core.extension
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import ru.isntrui.lb.client.models.enums.FileType

@Composable
fun AddSongDialog(
    onDismissRequest: () -> Unit
) {
    var songName by remember { mutableStateOf("выбрать файл") }
    var selectedFile by remember {
        mutableStateOf<PlatformFile?>(null)
    }
    val launcher = rememberFilePickerLauncher(
        type = PickerType.File(
            extensions = listOf("mp3", "wav", "flac"),
        ),
        mode = PickerMode.Single,
        title = "Выбери песенку",
    ) { file ->
        songName = file!!.name
        selectedFile = file
    }
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("добавить новую песенку") },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("название") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("исполнитель") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("описание") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    launcher.launch()
                }) {
                    Text(songName)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        url = uploadFile(Net.client(), selectedFile!!.name + "." + selectedFile!!.extension, selectedFile!!.readBytes(), FileType.AUDIO).bodyAsText()
                        println(url)
                    }
                    val newSong = Song(
                        title = title,
                        artist = artist,
                        description = description,
                        url = url,
                        madeBy = User(),
                        createdOn = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()),
                        wave = Wave(),
                        approvedBy = null,
                        approvedOn = null,
                        id = null
                    )
                }
            ) {
                Text("добавить")
            }
        }
    )
}