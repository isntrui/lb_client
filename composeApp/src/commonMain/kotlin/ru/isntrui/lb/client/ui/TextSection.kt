package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.copy
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.pencil
import org.jetbrains.compose.resources.painterResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.approveText
import ru.isntrui.lb.client.api.createText
import ru.isntrui.lb.client.api.deleteText
import ru.isntrui.lb.client.api.fetchAllActualWaves
import ru.isntrui.lb.client.api.fetchAllTexts
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.models.TextI
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.ui.auth.DropdownArrow
import ru.isntrui.lb.client.utils.formatDate

@Composable
fun TextSection(navController: NavController) {
    var texts by remember { mutableStateOf(emptyList<TextI>()) }
    var isLoading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var user by remember { mutableStateOf(User()) }
    var createTextDialogOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            texts = fetchAllTexts(Net.client()).sortedByDescending { it.id }
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
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN, Role.WRITER)) {
                    CenteredExtendedFloatingActionButton(
                        onClick = { createTextDialogOpen = true },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "плюс") }
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = { navController.navigate("songs") }) {
                    Icon(
                        painterResource(Res.drawable.musicnote),
                        contentDescription = "Звонки",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = { navController.navigate("designs") }) {
                    Icon(
                        painterResource(Res.drawable.brush),
                        contentDescription = "Дизайны",
                    )
                }
                IconButton(onClick = { }, enabled = false) {
                    Icon(
                        painterResource(Res.drawable.pencil),
                        contentDescription = "Тексты",
                        tint = Color.Gray
                    )
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
                        text = "тексты",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user, navController) {
                        isLoading = true
                        navController.navigate("texts")
                        isLoading = false
                    }
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                texts.forEach { text ->
                    TextCard(text, user, navController)
                }
            }
        }

        if (dialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    dialogOpen = false; navController.navigate("texts")
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

        if (createTextDialogOpen) {
            CreateTextDialog(
                onDismiss = { createTextDialogOpen = false },
                onCreate = { newText ->
                    createTextDialogOpen = false
                    isLoading = true
                    scope.launch {
                        createText(Net.client(), newText)
                    }
                    isLoading = false
                    texts += newText
                },
                user = user
            )
        }
    }
}

@Composable
fun CreateTextDialog(user: User, onDismiss: () -> Unit, onCreate: (TextI) -> Unit) {
    var text by remember { mutableStateOf(TextI(madeBy = user)) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedWave by remember { mutableStateOf<Wave?>(null) }
    var waves by remember { mutableStateOf(emptyList<Wave>()) }
    val scope = rememberCoroutineScope()
    var isFormValid by remember { mutableStateOf(false) }

    LaunchedEffect(text, selectedWave) {
        isFormValid = text.title.isNotBlank() && text.body.isNotBlank() && selectedWave != null
    }

    scope.launch {
        waves = fetchAllActualWaves(Net.client())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Text") },
        text = {
            Column {
                OutlinedTextField(
                    value = text.title,
                    onValueChange = { text = text.copy(title = it) },
                    label = { Text("название") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = text.body,
                    onValueChange = { text = text.copy(body = it) },
                    label = { Text("текст") },
                    maxLines = 9,
                    modifier = Modifier
                        .height(120.dp)
                        .padding(bottom = 8.dp)
                )
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
            Button(onClick = {
                onCreate(text.copy(wave = selectedWave))
            }, enabled = isFormValid) {
                Text("создать")
            }
        }
    )
}

@Composable
fun TextCard(text: TextI, user: User, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("текст ${text.title}") },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text.body,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "✍\uFE0F ${text.madeBy.firstName} ${text.madeBy.lastName} ${
                            formatDate(
                                text.madeOn.dayOfMonth,
                                text.madeOn.monthNumber
                            )
                        } ${text.madeOn.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    if (text.approved) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            "✅ ${text.approvedBy?.firstName} ${text.approvedBy?.lastName} ${
                                formatDate(
                                    text.approvedOn!!.dayOfMonth,
                                    text.approvedOn.monthNumber
                                )
                            } ${text.approvedOn.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Row {
                    var isRemoving by remember { mutableStateOf(false) }
                    if (user.role in listOf(
                            Role.COORDINATOR,
                            Role.HEAD,
                            Role.ADMIN
                        ) || user.id == text.madeBy.id
                    ) {
                        IconButton({
                            showDialog = false
                            coroutineScope.launch {
                                isRemoving = true
                                deleteText(Net.client(), text)
                                navController.navigate("texts")
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
                    IconButton({
                        clipboardManager.setText(AnnotatedString(text.body))
                    }) {
                        Image(
                            painterResource(Res.drawable.copy),
                            contentDescription = "Copy",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                        if (text.approved) {
                            Button({
                                coroutineScope.launch {
                                    approveText(Net.client(), text, false)
                                    showDialog = false
                                    navController.navigate("texts")
                                }
                            }) {
                                Text("отменить")
                            }
                        } else {
                            Button({
                                coroutineScope.launch {
                                    approveText(Net.client(), text, true)
                                    showDialog = false
                                    navController.navigate("texts")
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
                        text = text.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    if (text.approved)
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 2.dp).size(16.dp),
                        )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${text.body[0]}${
                        text.body.substring(
                            1,
                            text.body.length.coerceAtMost(60)
                        )
                    }...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
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
                        text = "${text.madeBy.firstName} ${text.madeBy.lastName}",
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
                            text.madeOn.dayOfMonth,
                            text.madeOn.monthNumber
                        ) + " " + text.madeOn.year,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}