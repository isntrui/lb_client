package ru.isntrui.lb.client.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.pencil
import lbtool.composeapp.generated.resources.status
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.createWave
import ru.isntrui.lb.client.api.deleteWave
import ru.isntrui.lb.client.api.fetchAllDesigns
import ru.isntrui.lb.client.api.fetchAllSongs
import ru.isntrui.lb.client.api.fetchAllTexts
import ru.isntrui.lb.client.api.fetchAllWaves
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.api.updateWave
import ru.isntrui.lb.client.models.Design
import ru.isntrui.lb.client.models.Song
import ru.isntrui.lb.client.models.TextI
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.models.enums.WaveStatus
import ru.isntrui.lb.client.ui.views.ModalDateInput
import ru.isntrui.lb.client.utils.formatDate

fun doDateRangesOverlap(start1: LocalDate, end1: LocalDate, start2: LocalDate, end2: LocalDate): Boolean {
    return start1 <= end2 && start2 <= end1
}

@Composable
fun WavesAdminPanel(navController: NavController) {
    var waves by remember { mutableStateOf(emptyList<Wave>()) }
    var isLoading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var addWaveDialogOpen by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(User()) }
    var songs by remember { mutableStateOf(emptyList<Song>()) }
    var designs by remember { mutableStateOf(emptyList<Design>()) }
    var texts by remember { mutableStateOf(emptyList<TextI>()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            waves = fetchAllWaves(Net.client())
                .sortedWith(compareBy { it.createdAt })
                .reversed()
            songs = fetchAllSongs(Net.client())
            designs = fetchAllDesigns(Net.client())
            texts = fetchAllTexts(Net.client())
        } catch (e: Exception) {
            dialogMessage = e.message ?: "Unknown error"
            dialogOpen = true
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
                        onClick = { addWaveDialogOpen = true },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "плюс") }
                    )
                }

                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = {navController.navigate("songs")}) {
                    Image(
                        painter = painterResource(Res.drawable.musicnote),
                        contentDescription = "звонки",
                    )
                }
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN, Role.DESIGNER)) {
                    IconButton(onClick = { navController.navigate("designs") }) {
                        Icon(
                            painter = painterResource(Res.drawable.brush),
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
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "хоме"
                    )
                }
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            VerticalDivider()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "волны",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user, navController) {
                        isLoading = true
                        navController.navigate("waves")
                        isLoading = false
                    }
                }
                HorizontalDivider(Modifier.padding(16.dp))
                waves.forEach { wave ->
                    WaveCard(wave, user, navController, songs, designs, texts)
                }
            }
        }

        if (dialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    dialogOpen = false; navController.navigate("waves")
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

        if (addWaveDialogOpen) {
            CreateWaveDialog(
                onSave = {
                    scope.launch {
                        println(it)
                        addWaveDialogOpen = false
                        isLoading = true
                        createWave(Net.client(), it)
                        navController.navigate("waves")
                        isLoading = false
                    }
                },
                onDismiss = { addWaveDialogOpen = false },
                existingWaves = waves,
            )
        }
    }
}

fun isDatePassed(date: LocalDate): Boolean {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return date < currentDate
}

@Composable
fun CreateWaveDialog(
    existingWaves: List<Wave>,
    onDismiss: () -> Unit,
    onSave: (Wave) -> Unit
) {
    var wave by remember {
        mutableStateOf(Wave(
            title = "",
            status = WaveStatus.PLANNED,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            startsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        ))
    }
    var titleError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var overlapError by remember { mutableStateOf(false) }

    fun validateTitle(title: String) = title.isNotBlank()
    fun validateDates(start: LocalDate?, end: LocalDate?): Boolean {
        return start != null && end != null && !isDatePassed(start) && start <= end
    }
    fun checkOverlap(start: LocalDate, end: LocalDate): Boolean {
        return existingWaves.any { wave ->
            doDateRangesOverlap(wave.startsOn, wave.endsOn, start, end)
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "новая волна") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = wave.title,
                    onValueChange = {
                        wave = wave.copy(title = it)
                        titleError = !validateTitle(it)
                    },
                    label = { Text("тематика") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError
                )
                if (titleError) {
                    Text(
                        text = "не может быть пустым",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ModalDateInput(
                    text = "начало",
                    onDateSelected = { date ->
                        wave = wave.copy(startsOn = date)
                        dateError = !validateDates(wave.startsOn, wave.endsOn)
                        overlapError = checkOverlap(wave.startsOn, wave.endsOn)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ModalDateInput(
                    text = "конец",
                    onDateSelected = { date ->
                        wave = wave.copy(endsOn = date)
                        dateError = !validateDates(wave.startsOn, wave.endsOn)
                        overlapError = checkOverlap(wave.startsOn, wave.endsOn)
                    }
                )
                if (dateError) {
                    Text(
                        text = "неверные даты",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (overlapError) {
                    Text(
                        text = "даты пересекаются с существующей волной",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    titleError = !validateTitle(wave.title)
                    dateError = !validateDates(wave.startsOn, wave.endsOn)
                    overlapError = checkOverlap(wave.startsOn, wave.endsOn)
                    if (!titleError && !dateError && !overlapError) {
                        onSave(wave)
                        onDismiss()
                    }
                },
                enabled = !titleError && !dateError && !overlapError
            ) {
                Text("Save")
            }
        }
    )
}

@Composable
fun WaveCard(wavee: Wave, user: User, navController: NavController, songs: List<Song>, designs: List<Design>, texts: List<TextI>) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var wave by remember { mutableStateOf(wavee) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("про волну") },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "\uD83C\uDF0A ${wave.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp
                    )
                    Text(
                        "✅ ${formatDate(wave.createdAt.dayOfMonth, wave.createdAt.monthNumber)} ${wave.createdAt.year}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "\uD83D\uDCC6 ${formatDate(
                                wave.startsOn.dayOfMonth,
                                wave.startsOn.monthNumber)
                        } ${wave.startsOn.year} — ${formatDate(
                            wave.endsOn.dayOfMonth,
                            wave.endsOn.monthNumber
                        )} ${wave.endsOn.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { expanded = true },
                    ) {
                        Box {
                            Text(
                                text = stringResource(wave.status.res),
                                modifier = Modifier.padding(16.dp)
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                WaveStatus.entries.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(status.res)) },
                                        onClick = {
                                            wave = wave.copy(status=status)
                                            coroutineScope.launch {
                                                updateWave(Net.client(), wave)
                                            }
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    var isRemoving by remember { mutableStateOf(false) }
                    if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                        IconButton(onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                isRemoving = true
                                deleteWave(Net.client(), wave)
                                navController.navigate("waves")
                                isRemoving = false
                            }
                        }) {
                            if (!isRemoving)
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "удалить"
                                ) else CircularProgressIndicator()
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { showDialog = false }) {
                        Text("Close")
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
                        text = wave.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Icon(
                        painterResource(Res.drawable.pencil),
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = "${texts.filter { it.wave!!.id == wave.id }.size} текстов",
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
                        painterResource(Res.drawable.brush),
                        contentDescription = "",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = "${designs.filter { it.wave.id == wave.id }.size} креативов",
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
                        painterResource(Res.drawable.musicnote),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = "${songs.filter { it.wave.id == wave.id }.size} песен",
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
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text = stringResource(wave.status.res),
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}