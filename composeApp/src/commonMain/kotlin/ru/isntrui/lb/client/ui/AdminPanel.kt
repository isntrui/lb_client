package ru.isntrui.lb.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.brush
import lbtool.composeapp.generated.resources.defaultAvatar
import lbtool.composeapp.generated.resources.group
import lbtool.composeapp.generated.resources.musicnote
import lbtool.composeapp.generated.resources.status
import lbtool.composeapp.generated.resources.wave
import org.jetbrains.compose.resources.painterResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.deleteTask
import ru.isntrui.lb.client.api.fetchAllTasks
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.api.fetchCurrentWave
import ru.isntrui.lb.client.api.updateTask
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.Wave
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.models.enums.TaskStatus
import ru.isntrui.lb.client.models.enums.WaveStatus
import ru.isntrui.lb.client.models.task.Task
import ru.isntrui.lb.client.ui.views.ModalDateInput
import ru.isntrui.lb.client.utils.formatDate
import ru.isntrui.lb.client.utils.isDatePassed

@Composable
fun AdminPanel(navController: NavController) {

    var user by remember { mutableStateOf(User()) }
    rememberCoroutineScope().launch {
        user = fetchCurrentUser(Net.client())
    }
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var currentWave by remember { mutableStateOf(Wave(status = WaveStatus.PLANNED, startsOn = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).date,
        endsOn = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)) }
    LaunchedEffect(Unit) {
        try {
            tasks =
                fetchAllTasks(Net.client()).sortedWith(compareBy<Task> { it.deadline }.thenBy { it.takenBy?.id }
                    .thenBy { it.taskStatus })
            currentWave = fetchCurrentWave(Net.client())
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }
    if (isLoading) {
        LoadingScreen(user)
    } else if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
        )
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                    CenteredExtendedFloatingActionButton(
                        onClick = { navController.navigate("users") },
                        icon = {
                            Icon(painterResource(Res.drawable.group), "юзеры")
                        }
                    )
                }
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN)) {
                    CenteredExtendedFloatingActionButton(
                        onClick = { navController.navigate("waves") },
                        icon = {
                            Icon(painterResource(Res.drawable.wave), "волны")
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
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = "Хоме",
                    )
                }
                if (user.role in listOf(Role.COORDINATOR, Role.HEAD, Role.ADMIN))
                IconButton(onClick = { navController.navigate("settings") }, enabled = false) {
                    Icon(
                        Icons.Outlined.Settings, contentDescription = "Настройки", tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            VerticalDivider()
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "админка",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    UserCard(user,
                        {
                            isLoading = true
                            navController.navigate("settings")
                            isLoading = false
                        })
                }
                Spacer(Modifier.fillMaxWidth().height(10.dp))
                HorizontalDivider()
                StatisticsSection(tasks)
                Spacer(modifier = Modifier.height(16.dp))
                if (tasks.isNotEmpty())
                TasksList(tasks, navController)
                else
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = """
                     ____________________
                    < ура, всё выполнено >
                     --------------------
                            \   ^__^
                             \  (oo)\_______
                                (__)\       )\/\
                                    ||----w |
                                    ||     ||
                """.trimIndent(),
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
            }
        }
    }
}


@Composable
fun StatisticsSection(tasks: List<Task>) {
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.taskStatus == TaskStatus.DONE }
    val todoTasks = tasks.count { it.taskStatus == TaskStatus.TODO }
    val inProgress = tasks.count { it.taskStatus == TaskStatus.PROGRESS }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticCard("всего тасков", totalTasks)
        StatisticCard("выполнено", completedTasks)
        StatisticCard("в процессе", inProgress)
        StatisticCard("ждём-с", todoTasks)
    }
    HorizontalDivider()
}

@Composable
fun StatisticCard(title: String, count: Int) {
    Card(
        modifier = Modifier.padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TasksList(tasks: List<Task>, navController: NavController) {
    LazyColumn {
        items(tasks) { task ->
            TaskItem(task, rememberCoroutineScope(), navController)
        }
    }
}

@Composable
fun TaskItem(task: Task, coroutineScope: CoroutineScope, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    if (expanded) {
        AdminTaskInfoDialog(task = task, onDismiss = { expanded = false }, onUpdate = {
            coroutineScope.launch {
                updateTask(Net.client(), it)
                expanded = false
                navController.navigate("settings")
            }
        }, afterRemove = {
            navController.navigate("settings")
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
            OutlinedCard(Modifier.padding(10.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    if (task.takenBy!!.avatarUrl == null)
                        Image(
                            painterResource(Res.drawable.defaultAvatar),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(32.dp),
                        )
                    else
                        AsyncImage(
                            model = task.takenBy.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    Text(
                        text = "${task.takenBy.firstName} ${task.takenBy.lastName}",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
fun AdminTaskInfoDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit,
    afterRemove: () -> Unit,
) {
    var selectedStatus by remember { mutableStateOf(task.taskStatus) }
    var expanded by remember { mutableStateOf(false) }
    var taskState by remember { mutableStateOf(task) }
    val statuses = TaskStatus.entries
    val scope = rememberCoroutineScope()
    var deadlineError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    fun validateDeadline(deadline: LocalDate) = !isDatePassed(deadline)
    fun validateTitle(title: String) = title.isNotBlank()
    fun validateDescription(description: String) = description.isNotBlank()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "про таску") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                OutlinedTextField(
                    value = taskState.title,
                    onValueChange = {
                        taskState = taskState.copy(title = it)
                        titleError = !validateTitle(it)
                    },
                    label = { Text("заголовок") },
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
                OutlinedTextField(
                    value = taskState.description,
                    onValueChange = {
                        taskState = taskState.copy(description = it)
                        descriptionError = !validateDescription(it)
                    },
                    label = { Text("описание") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descriptionError
                )
                if (descriptionError) {
                    Text(
                        text = "не может быть пустым",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ModalDateInput(
                    initialDate = taskState.deadline,
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
                            modifier = Modifier.padding(16.dp)
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
                                        taskState = taskState.copy(taskStatus = status)
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
                IconButton({
                    scope.launch {
                        isRemoving = true
                        deleteTask(Net.client(), task)
                        afterRemove()
                        isRemoving = false
                    }
                }) {
                    if (!isRemoving) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "удалить"
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    if (taskState != task && !titleError && !descriptionError && !deadlineError)
                        onUpdate(taskState)
                    onDismiss()
                }) {
                    Text("доне")
                }
            }
        }
    )
}