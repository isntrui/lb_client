package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.input.pointer.pointerInput
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
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.extension
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.chooserole
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.status
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.*
import ru.isntrui.lb.client.models.*
import ru.isntrui.lb.client.models.enums.FileType
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.models.enums.TaskStatus
import ru.isntrui.lb.client.models.enums.WaveStatus
import ru.isntrui.lb.client.models.task.Task
import ru.isntrui.lb.client.models.task.UserTask
import ru.isntrui.lb.client.requests.TaskRequest
import ru.isntrui.lb.client.ui.auth.DropdownArrow
import ru.isntrui.lb.client.ui.views.ModalDateInput
import ru.isntrui.lb.client.ui.views.NoServerConnectionAlertDialog
import ru.isntrui.lb.client.utils.formatDate
import ru.isntrui.lb.client.utils.isDatePassed

var isAgain = false

@Composable
fun Dashboard(navController: NavController) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var user by remember { mutableStateOf(User()) }
    var openDialog by remember { mutableStateOf(false) }
    var currentWave by remember { mutableStateOf(Wave(status = WaveStatus.PLANNED, startsOn = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).date, endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)) }
    var loading by remember { mutableStateOf(true) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var exception by remember { mutableStateOf<Exception?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            user = fetchCurrentUser(Net.client())
            tasks = loadTasks()
            currentWave = fetchCurrentWave(Net.client())
        } catch (e: Exception) {
            exception = e
            showErrorDialog = true
        } finally {
            loading = false
        }
    }

    if (loading) {
        LoadingScreen(user)
    } else {
        isAgain = true
        Scaffold(
            content = {
                Row {
                    NavigationRail {
                        if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                            CenteredExtendedFloatingActionButton(
                                onClick = { openDialog = true },
                                icon = { Icon(Icons.Filled.Add, "плюс") }
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
                                Image(
                                    painter = painterResource(Res.drawable.musicnote),
                                    contentDescription = "Звонки",
                                    colorFilter = ColorFilter.tint(Color.Black)
                                )
                            }
                        }
                        if (user.role in listOf(
                                Role.COORDINATOR,
                                Role.HEAD,
                                Role.ADMIN,
                                Role.DESIGNER
                            )
                        ) {
                            IconButton(onClick = { navController.navigate("designs") }) {
                                Icon(
                                    painterResource(Res.drawable.brush),
                                    contentDescription = "Дизайны",
                                    tint = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = "Хоме",
                                tint = Color.Gray
                            )
                        }
                        if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN))
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Настройки")
                            }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    VerticalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "твои таски",
                                style = MaterialTheme.typography.headlineLarge,
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Spacer(Modifier.weight(1f))
                            UserCard(user) {
                                loading = true
                                navController.navigate("dashboard")
                                loading = false
                            }
                        }
                        Spacer(Modifier.fillMaxWidth().height(10.dp))
                        HorizontalDivider()
                        if (tasks.isNotEmpty()) {
                            LazyColumn {
                                items(tasks) { task ->
                                    TaskCard(
                                        task,
                                        coroutineScope = rememberCoroutineScope(),
                                        navController
                                    )
                                }
                                items(1) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        HorizontalDivider()
                                        Spacer(modifier = Modifier.height(28.dp))
                                        Text(
                                            text = "текущая волна #${currentWave.id}",
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.weight(0.5f))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "у тебя нет активных задач :)",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontSize = 48.sp,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(Modifier.weight(0.5f))
                                HorizontalDivider(color = Color.LightGray)
                                Text(
                                    text = "текущая волна #${currentWave.id}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    if (openDialog) {
        CreateTaskDialog(
            user = user,
            wave = currentWave,
            onDismiss = { openDialog = false },
            navController = navController
        )
    }
    if (showErrorDialog && exception != null) {
        NoServerConnectionAlertDialog(e = exception!!)
    }
}

@Composable
fun LoadingScreen(user: User) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isAgain) {
                    Text(
                        "секундочку, пожалуйста!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                } else if (user.firstName.isNotEmpty()) {
                    Text(
                        "привет, ${user.firstName}!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 72.sp
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
                LinearProgressIndicator()
            }
        }
    }
}

@Composable
fun UserCard(user: User, onAvatarChange: () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(
        type = PickerType.Image,
        mode = PickerMode.Single,
        title = "выбери аву"
    ) { file ->
        if (file != null) {
            isLoading = true
            scope.launch {
                val re = uploadFile(Net.client(), user.id.toString() + "." + file.extension, file.readBytes(), FileType.IMG)
                val newUser = user.copy(avatarUrl = re.bodyAsText())
                updateUser(Net.client(), newUser)
                isLoading = false
                onAvatarChange()
            }
        }
    }

    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHovered = true
                                tryAwaitRelease()
                                isHovered = false
                            }
                        )
                    }
                    .clickable { launcher.launch() }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    if (user.avatarUrl != null) {
                        AsyncImage(
                            model = user.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.defaultAvatar),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                    if (isHovered) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp)
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(user.role.res))
                    Text(" | ", modifier = Modifier.padding(horizontal = 4.dp))
                    Text(user.building)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, coroutineScope: CoroutineScope, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    if (expanded) {
        TaskInfoDialog(task = task, onDismiss = { expanded = false }, onStatusChange = {
            coroutineScope.launch {
                changeStatus(Net.client(), task, TaskStatus.valueOf(it))
                expanded = false
                navController.navigate("dashboard")
            }
        })
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        onClick = {
            expanded = true
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "назначил/-а ${task.createdBy.firstName} ${task.createdBy.lastName} \n${
                        formatDate(
                            task.createdOn.dayOfMonth,
                            task.createdOn.monthNumber
                        )
                    } ${task.createdOn.hour}:${task.createdOn.minute}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
            val deadColor =
                if (isDatePassed(task.deadline) && task.taskStatus != TaskStatus.DONE) Color.Red else MaterialTheme.colorScheme.primary
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Image(
                        painterResource(Res.drawable.status),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = task.taskStatus.toString(),
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
                        tint = deadColor
                    )
                    Text(
                        text = formatDate(task.deadline.dayOfMonth, task.deadline.monthNumber),
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CenteredExtendedFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(16.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

@Composable
fun CreateTaskDialog(user: User, wave: Wave, navController: NavController, onDismiss: () -> Unit) {
    var taskState by remember {
        mutableStateOf(
            TaskRequest(
                createdBy = user,
                wave = wave,
                takenBy = null
            )
        )
    }
    val scope = rememberCoroutineScope()
    var isRoleDropDownExpanded by remember { mutableStateOf(false) }
    var isUserDropDownExpanded by remember { mutableStateOf(false) }
    var isWaveDropDownExpanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var selectedUser by remember { mutableStateOf<UserTask?>(null) }
    var selectedWave by remember { mutableStateOf<Wave?>(null) }
    var users by remember { mutableStateOf<List<UserTask>>(emptyList()) }
    var actualWaves by remember { mutableStateOf<List<Wave>>(emptyList()) }
    var itemPosition by remember { mutableStateOf(-1) }
    var deadlineError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    val roleText: String =
        if (itemPosition == -1) stringResource(Res.string.chooserole) else stringResource(Role.entries[itemPosition].res)

    LaunchedEffect(Unit) {
        actualWaves = fetchAllActualWaves(Net.client())
    }
    LaunchedEffect(selectedRole) {
        if (selectedRole != null) {
            users = fetchAllUsersByRole(Net.client(), selectedRole!!)
        }
    }

    fun validateTitle(title: String) = title.isNotBlank()
    fun validateDescription(description: String) = description.isNotBlank()
    fun validateDeadline(deadline: LocalDate) = !isDatePassed(deadline)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "новая задача") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = taskState.title,
                    onValueChange = {
                        taskState = taskState.copy(title = it)
                        titleError = !validateTitle(it)
                    },
                    label = { Text("таск") },
                    isError = titleError
                )
                if (titleError) {
                    Text(
                        text = "Title cannot be blank",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = taskState.description,
                    onValueChange = {
                        taskState = taskState.copy(description = it)
                        descriptionError = !validateDescription(it)
                    },
                    label = { Text("описание") },
                    isError = descriptionError
                )
                if (descriptionError) {
                    Text(
                        text = "описание не может быть пустым",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                ModalDateInput(
                    onDateSelected = { date ->
                        taskState = taskState.copy(deadline = date)
                        deadlineError = !validateDeadline(date)
                    }
                )
                if (deadlineError) {
                    Text(
                        text = "дедлайн не может быть в прошлом",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Text(
                    text = "выбери роль и исполнителя:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isRoleDropDownExpanded = !isRoleDropDownExpanded }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row {
                                Spacer(Modifier.width(20.dp).height(5.dp))
                                Text(
                                    roleText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(5.dp))
                                DropdownArrow()
                                Spacer(Modifier.width(7.dp))
                            }
                            DropdownMenu(
                                expanded = isRoleDropDownExpanded,
                                onDismissRequest = { isRoleDropDownExpanded = false }
                            ) {
                                Role.entries.forEachIndexed { index, role ->
                                    DropdownMenuItem(
                                        text = {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = stringResource(role.res),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        },
                                        onClick = {
                                            isRoleDropDownExpanded = false
                                            itemPosition = index
                                            selectedRole = role
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (selectedRole != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { isUserDropDownExpanded = !isUserDropDownExpanded }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp,
                                    alignment = Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row {
                                    Spacer(Modifier.width(20.dp).height(5.dp))
                                    Text(
                                        selectedUser?.let { it.firstName + " " + it.lastName + if (selectedRole == Role.TECHNICAL) " (${it.building})" else "" }
                                            ?: "выбрать пользователя",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontSize = 16.sp
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    DropdownArrow()
                                    Spacer(Modifier.width(7.dp))
                                }
                                DropdownMenu(
                                    expanded = isUserDropDownExpanded,
                                    onDismissRequest = { isUserDropDownExpanded = false }
                                ) {
                                    users.forEach { user ->
                                        DropdownMenuItem(
                                            text = {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth()
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    Text(
                                                        text = user.firstName + " " + user.lastName + if (selectedRole == Role.TECHNICAL) " (${user.building})" else "",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            },
                                            onClick = {
                                                isUserDropDownExpanded = false
                                                selectedUser = user
                                                taskState = taskState.copy(takenBy = user)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "выбери роль и исполнителя:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { isWaveDropDownExpanded = !isWaveDropDownExpanded }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row {
                                Spacer(Modifier.width(20.dp).height(5.dp))
                                Text(
                                    selectedWave?.let { "#" + it.id + " " + it.title }
                                        ?: "выбрать волну",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(5.dp))
                                DropdownArrow()
                                Spacer(Modifier.width(7.dp))
                            }
                            DropdownMenu(
                                expanded = isWaveDropDownExpanded,
                                onDismissRequest = { isWaveDropDownExpanded = false }
                            ) {
                                actualWaves.forEach { wave ->
                                    DropdownMenuItem(
                                        text = {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = "#" + wave.id + " " + wave.title,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        },
                                        onClick = {
                                            isWaveDropDownExpanded = false
                                            selectedWave = wave
                                            taskState = taskState.copy(wave = wave)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            createTask(Net.client(), taskState)
                        } catch (e: Exception) {
                            println("Error: ${e.message}")
                        } finally {
                            navController.navigate("dashboard")
                            onDismiss()
                        }
                    }
                },
                enabled = !deadlineError && !titleError && !descriptionError && taskState.title.isNotBlank() && taskState.description.isNotBlank() && selectedUser != null && selectedWave != null && validateUserRole(
                    selectedRole,
                    selectedUser
                )
            ) {
                Text("ок", fontSize = 22.sp)
            }
        }
    )
}

suspend fun loadTasks(): List<Task> {
    return fetchTasks(Net.client()).sortedWith(compareBy<Task> { it.deadline }.thenBy { it.taskStatus })
}

fun validateUserRole(selectedRole: Role?, selectedUser: UserTask?): Boolean {
    return selectedRole != null && selectedUser != null && selectedRole == selectedUser.role
}

@Composable
fun TaskInfoDialog(task: Task, onDismiss: () -> Unit, onStatusChange: (String) -> Unit) {
    var selectedStatus by remember { mutableStateOf(task.taskStatus) }
    var expanded by remember { mutableStateOf(false) }
    val statuses = TaskStatus.entries

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "про таску") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "создал/-а ${task.createdBy.firstName} ${task.createdBy.lastName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "дедлайн ${
                        formatDate(
                            task.deadline.dayOfMonth,
                            task.deadline.monthNumber
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "статус",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { expanded = true }
                ) {
                    Box {
                        Text(
                            text = selectedStatus.toString(),
                            modifier = Modifier
                                .padding(16.dp)
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statuses.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.toString()) },
                                    onClick = {
                                        selectedStatus = status
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
            Button(onClick = {
                if (selectedStatus != task.taskStatus) {
                    onStatusChange(selectedStatus.toString())
                }
                onDismiss()
            }) {
                Text("доне")
            }
        }
    )
}