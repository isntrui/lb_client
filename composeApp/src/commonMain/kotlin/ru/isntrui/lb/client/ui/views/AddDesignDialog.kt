package ru.isntrui.lb.client.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.uploadFile
import ru.isntrui.lb.client.models.User

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.extension
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import ru.isntrui.lb.client.api.createDesign
import ru.isntrui.lb.client.api.fetchAllActualWaves
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.models.Design
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.FileType
import ru.isntrui.lb.client.ui.auth.DropdownArrow

@Composable
fun AddDesignDialog(
    onDismissRequest: () -> Unit
) {
    var songName by remember { mutableStateOf("выбрать файл") }
    var selectedFile by remember { mutableStateOf<PlatformFile?>(null) }
    var title by remember { mutableStateOf("") }
    var selectedWave by remember { mutableStateOf<Wave?>(null) }
    var waves by remember { mutableStateOf(emptyList<Wave>()) }
    var url by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf(User()) }
    var triggerUpload by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        waves = fetchAllActualWaves(Net.client())
        currentUser = fetchCurrentUser(Net.client())
    }
    var file by remember { mutableStateOf<PlatformFile?>(null) }
    if (file != null) {
        songName = file!!.name
        selectedFile = file
    }

    val isFormValid =
        title.isNotBlank() && selectedFile != null && selectedWave != null
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("добавить новый дизайн") },
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
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        file = FileKit.pickFile(
                            mode = PickerMode.Single,
                            title = "Выбери креатив",
                        )
                    }
                }) {
                    Text(songName)
                }
                Box {
                    OutlinedCard(onClick = { dropdownExpanded = !dropdownExpanded }) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row {
                                Spacer(Modifier.width(20.dp).height(5.dp))
                                if (selectedWave != null)
                                    Text(
                                        "#${selectedWave!!.id} ${selectedWave!!.title}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontSize = 16.sp
                                    ) else Text(
                                    "выбрать волну",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(5.dp))
                                DropdownArrow()
                                Spacer(Modifier.width(7.dp))
                            }
                        }
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        waves.forEach { wave ->
                            DropdownMenuItem(onClick = {
                                selectedWave = wave
                                dropdownExpanded = false
                            }, text = {
                                Text("#${wave.id} ${wave.title}")
                            })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    triggerUpload = true
                    coroutineScope.launch {
                        url = uploadFile(
                            Net.client(),
                            selectedFile!!.name + "." + selectedFile!!.extension,
                            selectedFile!!.readBytes(),
                            FileType.MEDIA
                        ).bodyAsText()
                        println(url)
                        val newDesign = Design(
                            id = 0,
                            createdBy = currentUser,
                            createdOn = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()),
                            wave = selectedWave!!,
                            url = url,
                            title = title,
                            approved = false,
                            approvedBy = null,
                            approvedOn = null
                        )
                        createDesign(Net.client(), newDesign)
                        triggerUpload = false
                        onDismissRequest()
                    }
                },
                enabled = isFormValid && !triggerUpload
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("добавить")
                    if (triggerUpload) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    )
}