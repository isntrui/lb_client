package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.vinceglb.filekit.core.FileKit
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.launch
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.download
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.pencil
import lbtool.composeapp.generated.resources.status
import org.jetbrains.compose.resources.painterResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.approveSong
import ru.isntrui.lb.client.api.deleteSong
import ru.isntrui.lb.client.api.fetchAllSongs
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.models.Song
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.ui.views.AddSongDialog
import ru.isntrui.lb.client.utils.formatDate

@Composable
fun SongsSection(navController: NavController) {
    var songs by remember { mutableStateOf(emptyList<Song>()) }
    var isLoading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var addSongDialogOpen by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(User()) }

    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            songs = fetchAllSongs(Net.client())
                .sortedWith(compareBy({ it.wave.id }, { it.createdOn }))
                .reversed()
        } catch (e: Exception) {
            dialogMessage = e.message ?: "Unknown error"
            dialogOpen = true
        } finally {
            isLoading = false
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "сек!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 72.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                LinearProgressIndicator()
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                if (user.role in listOf(
                        Role.COORDINATOR,
                        Role.HEAD,
                        Role.ADMIN,
                        Role.SOUNDDESIGNER
                    )
                ) {
                    CenteredExtendedFloatingActionButton(
                        onClick = { addSongDialogOpen = true },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "плюс") }
                    )
                }

                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = { }, enabled = false) {
                    Image(
                        painter = painterResource(Res.drawable.musicnote),
                        contentDescription = "звонки",
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                }
                if (user.role in listOf(
                        Role.COORDINATOR,
                        Role.HEAD,
                        Role.ADMIN,
                        Role.DESIGNER
                    )) {
                    IconButton(onClick = { navController.navigate("designs") }) {
                        Icon(
                            painterResource(Res.drawable.brush),
                            contentDescription = "Дизайны",
                            tint = Color.Black
                        )
                    }
                }
                if (user.role in listOf(
                        Role.COORDINATOR,
                        Role.HEAD,
                        Role.ADMIN,
                        Role.WRITER
                    )
                ) {
                    IconButton(onClick = { navController.navigate("texts") }) {
                        Icon(
                            painterResource(Res.drawable.pencil),
                            contentDescription = "Тексты",
                            tint = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = {
                    navController.navigate("dashboard")
                }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "хоме"
                    )
                }
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN))
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "звоночки",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user, navController) {
                        isLoading = true
                        navController.navigate("songs")
                        isLoading = false
                    }
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                songs.forEach { song ->
                    SongCard(song, user, navController)
                }
            }
        }

        if (dialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    dialogOpen = false; navController.navigate("songs")
                },
                title = { Text("Error") },
                text = { Text(dialogMessage) },
                confirmButton = {
                    Button(onClick = { dialogOpen = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (addSongDialogOpen) {
            AddSongDialog(
                onDismissRequest = {
                    addSongDialogOpen = false; navController.navigate("songs")
                }
            )
        }
    }
}

@Composable
fun SongCard(song: Song, user: User, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("про песенку") },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "\uD83C\uDFB5 ${song.title} — ${song.artist}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "\uD83D\uDDD2\uFE0F ${song.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    Text(
                        "\uD83C\uDF0A #${song.wave.id} ${song.wave.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "✍\uFE0F ${song.madeBy.firstName} ${song.madeBy.lastName} ${
                            formatDate(
                                song.createdOn.dayOfMonth,
                                song.createdOn.monthNumber
                            )
                        } ${song.createdOn.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    if (song.approved && song.approvedBy != null && song.approvedOn != null)
                        Text(
                            "✅ ${song.approvedBy.firstName} ${song.approvedBy.lastName} ${
                                formatDate(
                                    song.approvedOn.dayOfMonth,
                                    song.approvedOn.monthNumber
                                )
                            } ${song.approvedOn.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp
                        )
                }
            },
            confirmButton = {
                Row {
                    var isRemoving by remember {
                        mutableStateOf(false)
                    }
                    if (user.role in listOf(
                        Role.COORDINATOR,
                        Role.HEAD,
                        Role.ADMIN
                    ) || user.id!! == song.madeBy.id
                    )
                    IconButton({
                        showDialog = false
                        coroutineScope.launch {
                            isRemoving = true
                            deleteSong(Net.client(), song)
                            navController.navigate("songs")
                            isRemoving = false
                        }
                    }) {
                        if (!isRemoving)
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete"
                        ) else CircularProgressIndicator()
                    }
                    var lod by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                lod = true
                                download(song)
                                lod = false
                            }
                        },
                    ) {
                        if (!lod)
                        Image(
                            painterResource(Res.drawable.download),
                            contentDescription = "Download",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        else CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.weight(1f))
                    if (user.role in listOf(
                            Role.COORDINATOR,
                            Role.HEAD,
                            Role.ADMIN
                        )
                    ) {

                        if (song.approved) {
                            Button({
                                coroutineScope.launch {
                                    approveSong(Net.client(), song, false)
                                    showDialog = false
                                    navController.navigate("songs")
                                }
                            }) {
                                Text("отменить")
                            }
                        } else {
                            Button({
                                coroutineScope.launch {
                                    approveSong(Net.client(), song, true)
                                    showDialog = false
                                    navController.navigate("songs")
                                }
                            }) {
                                Text("утвердить")
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                    }
                    OutlinedButton(onClick = { showDialog = false }) {
                        Text("ладн")
                    }
                }
            }
        )
    }

    Card(
        onClick = { showDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (song.approved)
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 2.dp).size(16.dp),
                        )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.defaultAvatar),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = "${song.madeBy.firstName} ${song.madeBy.lastName}",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.status),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = song.wave.title,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                    )
                    Text(
                        text = formatDate(
                            song.createdOn.dayOfMonth,
                            song.createdOn.monthNumber
                        ) + " " + song.createdOn.year,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

suspend fun download(song: Song) {
    val resp = Net.client().get(song.url)
    val bytes = resp.readRawBytes()
    FileKit.saveFile(
        baseName = "S_${song.title} — ${song.artist} (${song.madeBy.firstName} ${song.madeBy.lastName})",
        extension = getFileExtension(song.url),
        bytes = bytes
    )
}