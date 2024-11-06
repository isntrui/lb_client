package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
import kotlinx.coroutines.launch
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.status
import org.jetbrains.compose.resources.painterResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.fetchAllSongs
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.api.uploadFile
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
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
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
                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "хоме"
                    )
                }
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
                    UserCard(user)
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                songs.forEach { song ->
                    SongCard(song = song)
                }
            }
        }

        if (dialogOpen) {
            AlertDialog(
                onDismissRequest = { dialogOpen = false },
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
                onDismissRequest = { addSongDialogOpen = false }
            )
        }
    }
}

@Composable
fun SongCard(song: Song) {
    Card(
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
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
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