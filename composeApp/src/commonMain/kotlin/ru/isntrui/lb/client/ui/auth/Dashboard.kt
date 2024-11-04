package ru.isntrui.lb.client.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.defaultAvatar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.isntrui.lb.client.api.fetchTasks
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.fetchCurrentUser
import ru.isntrui.lb.client.models.User
import ru.isntrui.lb.client.models.task.Task


@Composable
fun Dashboard(navController: NavController) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var user by remember {
        mutableStateOf(
            User()
        )
    }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        loading = true
        try {
            user = fetchCurrentUser(Net.client())
            tasks = fetchTasks(Net.client())
        } catch (e: Exception) {
            println("Error fetching: ${e.message}")
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Text(
                text = "Your Tasks",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Spacer(Modifier.weight(1f))
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row {
                    Spacer(Modifier.width(25.dp))
                    Column(verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)) {

                    Spacer(Modifier.height(10.dp))
                        if (user.avatarUrl != null) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape)
                                )
                            Spacer(Modifier.height(16.dp))
                        } else {
                            Image(
                                painter = painterResource(Res.drawable.defaultAvatar),
                                contentDescription = null,
                                Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray, CircleShape)
                            )
                            Spacer(Modifier.height(16.dp))
                        }

                    }
                    Spacer(Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            user.firstName + " " + user.lastName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            stringResource(user.role.res)
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                    Spacer(Modifier.width(25.dp))
                }
            }
            Spacer(Modifier.width(3.dp))
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(tasks) { task ->
                    TaskCard(task)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: ${task.taskStatus}", style = MaterialTheme.typography.bodySmall)
        }
    }
}