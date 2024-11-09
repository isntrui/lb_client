package ru.isntrui.lb.client.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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
import ru.isntrui.lb.client.api.approveDesign
import ru.isntrui.lb.client.api.deleteDesign
import ru.isntrui.lb.client.api.fetchAllDesigns
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.api.fetchMyDesigns
import ru.isntrui.lb.client.models.Design
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.ui.views.AddDesignDialog
import ru.isntrui.lb.client.utils.formatDate

@Composable
fun DesignSection(navController: NavController) {
    var designs by remember { mutableStateOf(emptyList<Design>()) }
    var isLoading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var addDesignDialogOpen by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(User()) }

    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN))
                designs = fetchAllDesigns(Net.client()).sortedWith(
                    compareBy({ it.wave.id },
                        { it.createdOn })
                )
                    .reversed()
            else fetchMyDesigns(Net.client()).reversed()
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
                        Role.DESIGNER
                    )
                ) {
                    CenteredExtendedFloatingActionButton(
                        onClick = { addDesignDialogOpen = true },
                        icon = { Icon(Icons.Filled.Add, contentDescription = "плюс") }
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
                if (user.role in listOf(
                        Role.COORDINATOR,
                        Role.HEAD,
                        Role.ADMIN,
                        Role.SOUNDDESIGNER
                    )
                ) {
                    IconButton(onClick = { navController.navigate("songs") }) {
                        Icon(
                            painterResource(Res.drawable.musicnote),
                            contentDescription = "Звонки",
                            tint = Color.Black
                        )
                    }
                }
                IconButton(onClick = { }, enabled = false) {
                    Icon(
                        painterResource(Res.drawable.brush),
                        contentDescription = "Дизайны",
                        tint = Color.Gray
                    )
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
                        text = "дизайны",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user, navController) {
                        isLoading = true
                        navController.navigate("designs")
                        isLoading = false
                    }
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                designs.forEach { design ->
                    DesignCard(design, user, navController)
                }
            }
        }

        if (dialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    dialogOpen = false; navController.navigate("designs")
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

        if (addDesignDialogOpen) {
            AddDesignDialog(
                onDismissRequest = {
                    addDesignDialogOpen = false; navController.navigate("designs")
                }
            )
        }
    }
}

@Composable
fun DesignCard(design: Design, user: User, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("про дизайн") },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "\uD83C\uDFB5 ${design.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 18.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "\uD83C\uDF0A #${design.wave.id} ${design.wave.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "✍\uFE0F ${design.createdBy.firstName} ${design.createdBy.lastName} ${
                            formatDate(
                                design.createdOn.dayOfMonth,
                                design.createdOn.monthNumber
                            )
                        } ${design.createdOn.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp
                    )
                    if (getFileExtension(design.url) == "jpg" || getFileExtension(design.url) == "png" || getFileExtension(
                            design.url
                        ) == "jpeg" || getFileExtension(design.url) == "gif" || getFileExtension(design.url) == "webp" || getFileExtension(
                            design.url
                        ) == "bmp" || getFileExtension(design.url) == "svg" || getFileExtension(design.url) == "ico"
                    ) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        AsyncImage(
                            model = design.url,
                            contentDescription = null,
                            modifier = Modifier.size(400.dp).clip(CircleShape)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    if (design.approved && design.approvedBy != null && design.approvedOn != null)
                        Text(
                            "✅ ${design.approvedBy.firstName} ${design.approvedBy.lastName} ${
                                formatDate(
                                    design.approvedOn.dayOfMonth,
                                    design.approvedOn.monthNumber
                                )
                            } ${design.approvedOn.year}",
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
                        ) || user.id!! == design.createdBy.id
                    )
                        IconButton({
                            showDialog = false
                            coroutineScope.launch {
                                isRemoving = true
                                deleteDesign(Net.client(), design)
                                navController.navigate("designs")
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
                                download(design)
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

                        if (design.approved) {
                            Button({
                                coroutineScope.launch {
                                    approveDesign(Net.client(), design, false)
                                    showDialog = false
                                    navController.navigate("designs")
                                }
                            }) {
                                Text("отменить")
                            }
                        } else {
                            Button({
                                coroutineScope.launch {
                                    approveDesign(Net.client(), design, true)
                                    showDialog = false
                                    navController.navigate("designs")
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
        Box {
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
                            text = design.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        if (design.approved)
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = 2.dp).size(16.dp),
                            )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Spacer(modifier = Modifier.width(4.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "." + getFileExtension(design.url),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
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
                            text = "${design.createdBy.firstName} ${design.createdBy.lastName}",
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
                            text = design.wave.title,
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
                                design.createdOn.dayOfMonth,
                                design.createdOn.monthNumber
                            ) + " " + design.createdOn.year,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

fun getFileExtension(url: String): String {
    return url.substringAfterLast('.', "")
}

suspend fun download(design: Design) {
    val resp = Net.client().get(design.url)
    val bytes = resp.readRawBytes()
    FileKit.saveFile(
        baseName = "D${design.id} ${design.title} (${design.createdBy.firstName} ${design.createdBy.lastName})",
        extension = getFileExtension(design.url),
        bytes = bytes
    )
}