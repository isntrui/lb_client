package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.musicnote
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.fetchAllUsers
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.enums.Role

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import lbtool.composeapp.generated.resources.copy
import lbtool.composeapp.generated.resources.email
import lbtool.composeapp.generated.resources.emailtaken
import lbtool.composeapp.generated.resources.entercorrect
import ru.isntrui.lb.client.api.deleteUser
import ru.isntrui.lb.client.api.updateUser
import ru.isntrui.lb.client.ui.auth.isValidEmail
import kotlin.math.abs

@Composable
fun UserAdminCard(user: User, onClick: () -> Unit, userInit: User) {
    Card(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        onClick = { onClick() },
        enabled = when (userInit.role) {
            Role.ADMIN -> true
            Role.HEAD -> user.role != Role.ADMIN
            Role.COORDINATOR -> user.role != Role.COORDINATOR && user.role != Role.HEAD && user.role != Role.ADMIN
            else -> false
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.defaultAvatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            }
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 18.sp
            )
            Text(
                text = "${stringResource(user.role.res)} | ${user.building} | ${user.graduateYear}",
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
    onDelete: (User) -> Unit,
    userInit: User
) {
    var newUser by remember { mutableStateOf(user) }
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "юзер #${user.id}") },
        text = {
            Column {
                OutlinedTextField(
                    value = newUser.firstName,
                    onValueChange = { newUser = newUser.copy(firstName = it) },
                    label = { Text("имя") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newUser.lastName,
                    onValueChange = { newUser = newUser.copy(lastName = it) },
                    label = { Text("фамилия") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newUser.building,
                    onValueChange = { newUser = newUser.copy(building = it) },
                    label = { Text("здание") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = true },
                ) {
                    Box {
                        Text(
                            text = newUser.role.toString(),
                            modifier = Modifier.padding(16.dp)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            Role.entries.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role.toString()) },
                                    onClick = {
                                        newUser = newUser.copy(role = role)
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
                if (user.id != userInit.id)
                    IconButton(onClick = { onDelete(user) }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "удалить"
                        )
                    }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onSave(newUser)
                    onDismiss()
                }) {
                    Text("сохранить")
                }
            }
        },
    )
}

@Composable
fun UsersAdminPanel(navController: NavController) {
    var user by remember { mutableStateOf(User()) }
    var users by remember { mutableStateOf(emptyList<User>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()
    var isAddingInvite by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            users = fetchAllUsers(Net.client()).filter { it.lastName != "[DELETED]" }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }
    if (isAddingInvite) {
        CreateInviteDialog(user) {
            isAddingInvite = false
        }
    }
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "секундочку, пожалуйста!",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 72.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            LinearProgressIndicator()
        }
    } else if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color.Red)
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                    CenteredExtendedFloatingActionButton(
                        onClick = {
                            isAddingInvite = true
                        },
                        icon = {
                            Icon(Icons.Filled.Add, "плюс")
                        }
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
                IconButton(onClick = { navController.navigate("designs") }) {
                    Icon(
                        painterResource(Res.drawable.brush),
                        contentDescription = "Дизайны",
                    )
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "юзеры",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user) {
                        isLoading = true
                        navController.navigate("users")
                        isLoading = false
                    }
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 256.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users) { userC ->
                        if (userC.lastName != "[DELETED]")
                            UserAdminCard(
                                userC,
                                userInit = user,
                                onClick = { selectedUser = userC })
                    }
                }
            }
        }
    }

    selectedUser?.let { selUser ->
        EditUserDialog(
            user = selUser,
            onDismiss = { selectedUser = null },
            onSave = { updatedUser ->
                scope.launch {
                    selectedUser = null
                    isLoading = true
                    updateUser(Net.client(), updatedUser)
                    navController.navigate("users")
                    isLoading = false
                }
            },
            onDelete = { removed ->
                scope.launch {
                    selectedUser = null
                    isLoading = true
                    deleteUser(Net.client(), removed)
                    users = users.filter { it.id != selUser.id }
                    isLoading = false
                }
            },
            userInit = user
        )
    }
}

@Composable
fun CreateInviteDialog(user: User, onDismiss: () -> Unit) {
    var isCreated by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "новый юзер") },
        text = {
            Column {
                if (!isCreated) {
                    var isEmailCorrect by remember { mutableStateOf(true) }
                    var isEmailTaken by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        label = { Text(stringResource(Res.string.email)) },
                        value = email,
                        isError = !isEmailCorrect || isEmailTaken,
                        supportingText = {
                            if (!isEmailCorrect) Text(stringResource(Res.string.entercorrect))
                            if (isEmailTaken) Text(stringResource(Res.string.emailtaken))
                        },
                        onValueChange = {
                            email = it
                            validateEmail(it, scope) { emailCorrect, emailTaken ->
                                isEmailCorrect = emailCorrect
                                isEmailTaken = emailTaken
                            }
                        }
                    )
                } else
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Код приглашения для ${email}: $code")
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString("Код приглашения для ${email}: $code"))
                        }) {
                            Icon(
                                painterResource(Res.drawable.copy),
                                contentDescription = "копировать"
                            )
                        }
                    }
            }
        },
        confirmButton = {
            if (!isCreated)
                Button({
                    scope.launch {
                        isCreated = true
                    }
                }) {
                    Text("создать")
                }
            else {
                Button({
                    onDismiss()
                }) {
                    Text("Закрыть")
                }
            }
        })
}

fun validateEmail(email: String, scope: CoroutineScope, onEmailChecked: (Boolean, Boolean) -> Unit) {
    val isEmailCorrect = isValidEmail(email)
    if (isEmailCorrect) {
        scope.launch {
            val isEmailTaken = Net.client().get("auth/check/email?email=$email")
            println(isEmailTaken)
            onEmailChecked(isEmailCorrect, isEmailTaken.status == HttpStatusCode.OK)
        }
    } else {
        onEmailChecked(isEmailCorrect, false)
    }
}