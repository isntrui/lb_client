package ru.isntrui.lb.client.ui.auth

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.models.enums.Role
import ru.isntrui.lb.client.requests.SignUpRequest

fun isValidName(name: String): Boolean {
    return name.all { it.isLetter() }
}

fun isValidUsername(username: String): Boolean {
    val usernameRegex = "^(?![_.-])[A-Za-z0-9_.-]{3,20}(?<![_.-])$".toRegex()
    return usernameRegex.matches(username)
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}


@Composable
fun DropdownArrow() {
    Icon(
        imageVector = Icons.Rounded.ArrowDropDown,
        contentDescription = "Dropdown Arrow",
        tint = Color.Black,
        modifier = Modifier.size(42.dp)
    )
}

fun capitalizeFirstLetter(input: String): String {
    return if (input.isNotEmpty()) {
        input.substring(0, 1).uppercase() + input.substring(1).lowercase()
    } else {
        input
    }
}

data class Cond(
    var isNameCorrect: Boolean = true,
    var isLastNameCorrect: Boolean = true,
    val isBuildingCorrect: Boolean = true,
    var isYearCorrect: Boolean = true,
    var isInviteCodeCorrect: Boolean = true,
    var isShownPassword: Boolean = false,
    var isEmailCorrect: Boolean = true,
    var isUsernameCorrect: Boolean = true
)

@Composable
fun Registration(navController: NavController) {
    var userState by remember {
        mutableStateOf(SignUpRequest(year = 2026))
    }
    var isDropDownExpanded by remember {
        mutableStateOf(false)
    }
    var itemPosition by remember {
        mutableStateOf(-1)
    }
    var cond by remember {
        mutableStateOf(Cond())
    }
    val focusRequester = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().focusable()
            .onKeyEvent { event ->
                if (event.key == Key.Escape) {
                    navController.navigate("login")
                    true
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(Res.string.registration_title), // Updated to use string resource
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
            fontSize = 120.sp
        )
        Spacer(Modifier.height(30.dp))
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            OutlinedTextField(
                label = { Text(stringResource(Res.string.first_name_label)) },
                value = userState.firstName,
                supportingText = { if (!cond.isNameCorrect) Text(stringResource(Res.string.entercorrect)) },
                onValueChange = {
                    if (isValidName(it)) {
                        userState = userState.copy(firstName = capitalizeFirstLetter(it))
                        cond.isNameCorrect = true
                    } else {
                        cond.isNameCorrect = false
                    }
                },
                modifier = Modifier.focusRequester(focusRequester)
            )

            OutlinedTextField(
                label = { Text(stringResource(Res.string.last_name_label)) },
                value = userState.lastName,
                supportingText = { if (!cond.isLastNameCorrect) Text(stringResource(Res.string.entercorrect)) },
                onValueChange = {
                    if (isValidName(it)) {
                        userState = userState.copy(lastName = capitalizeFirstLetter(it))
                        cond.isLastNameCorrect = true
                    } else {
                        cond.isLastNameCorrect = false
                    }
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val text: String = if (itemPosition == -1) stringResource(Res.string.chooserole)
            else stringResource(Role.entries[itemPosition].res)

            OutlinedTextField(
                label = { Text(stringResource(Res.string.building_label)) }, // Updated to use string resource
                value = userState.building,
                isError = !cond.isBuildingCorrect,
                supportingText = {},
                onValueChange = {
                    if (isValidName(it)) {
                        userState = userState.copy(building = capitalizeFirstLetter(it))
                        cond.isNameCorrect = true
                    } else {
                        cond.isNameCorrect = false
                    }
                }
            )

            Box(modifier = Modifier.width(105.dp)) {
                OutlinedTextField(
                    label = { Text(stringResource(Res.string.year)) }, // Updated to use string resource
                    value = userState.year.toString(),
                    isError = !cond.isYearCorrect,
                    supportingText = {
                        if (!cond.isYearCorrect) {
                            Text(stringResource(Res.string.grad_year_range_warn), color = Color.Red)
                        }
                    },
                    onValueChange = {
                        var y: Int
                        try {
                            y = it.toInt()
                            cond.isYearCorrect = y in 2024..2028
                        } catch (e: Exception) {
                            cond.isYearCorrect = false
                            y = userState.year
                        }
                        userState = userState.copy(year = y)
                    }
                )
            }

            OutlinedCard(onClick = {
                isDropDownExpanded = !isDropDownExpanded
            }) {
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
                            text,
                            style = MaterialTheme.typography.headlineSmall,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(5.dp))
                        DropdownArrow()
                        Spacer(Modifier.width(7.dp))
                    }

                    DropdownMenu(
                        expanded = isDropDownExpanded,
                        onDismissRequest = { isDropDownExpanded = false }) {
                        Role.entries.forEachIndexed { index, role ->
                            DropdownMenuItem(text = { Text(text = stringResource(role.res)) },
                                onClick = {
                                    isDropDownExpanded = false
                                    itemPosition = index
                                    userState = userState.copy(role = role)
                                })
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                label = { Text(stringResource(Res.string.invite_code)) }, // Updated to use string resource
                value = userState.inviteCode,
                isError = !cond.isInviteCodeCorrect,
                onValueChange = {
                    userState = userState.copy(inviteCode = it.lowercase())
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                label = { Text(stringResource(Res.string.username)) }, // Updated to use string resource
                value = userState.username,
                isError = !cond.isUsernameCorrect,
                supportingText = { if (!cond.isUsernameCorrect) Text(stringResource(Res.string.incorrectuname, Color.Red)) },
                onValueChange = {
                    if (isValidUsername(it)) {
                        cond.isUsernameCorrect = true
                    } else {
                        cond.isUsernameCorrect = false
                    }
                    userState = userState.copy(username = it.lowercase())
                }
            )
            OutlinedTextField(
                label = { Text(stringResource(Res.string.email)) },
                value = userState.email,
                isError = !cond.isEmailCorrect,
                supportingText = {if (!cond.isEmailCorrect) Text(stringResource(Res.string.entercorrect)) },
                onValueChange = {
                    if (isValidEmail(it)) {
                        userState = userState.copy(email = it)
                        cond.isEmailCorrect = true
                    } else {
                        userState = userState.copy(email = it)
                        cond.isEmailCorrect = false
                    }
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            OutlinedTextField(
                label = { Text(stringResource(Res.string.password_label)) }, // Updated to use string resource
                value = userState.password,
                supportingText = {},
                onValueChange = {
                    userState = userState.copy(password = it)
                },
                visualTransformation = if (!cond.isShownPassword) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    if (userState.password.isNotEmpty()) {
                        TextButton(onClick = {
                            cond = cond.copy(isShownPassword = !cond.isShownPassword)
                        }) {
                            Text(
                                if (cond.isShownPassword) stringResource(Res.string.hide_password_text) else stringResource(
                                    Res.string.show_password_text
                                )
                            ) // Updated to use string resource
                        }
                    }
                },
                singleLine = true
            )

            var repeat by remember { mutableStateOf("") }

            OutlinedTextField(
                label = { Text(stringResource(Res.string.repeat_password_label)) }, // Updated to use string resource
                value = repeat,
                isError = userState.password != repeat && repeat.isNotEmpty() && userState.password.isNotEmpty(),
                supportingText = {
                    if (userState.password != repeat && repeat.isNotEmpty() && userState.password.isNotEmpty())
                        Text(
                            stringResource(Res.string.password_mismatch_error),
                            color = Color.Red
                        ) // Updated to use string resource
                },
                onValueChange = {
                    repeat = it
                },
                visualTransformation = if (!cond.isShownPassword) PasswordVisualTransformation() else VisualTransformation.None,
            )
        }

        val scope = rememberCoroutineScope()
        var responseMessage = ""
        lateinit var responseCode: HttpStatusCode

        Button(onClick = {
            scope.launch {
                try {
                    val response: HttpResponse =
                        Net.httpClient.post("http://igw.isntrui.ru/api/auth/sign-up") {
                            setBody(userState)
                        }
                    responseCode = response.status
                } catch (e: Throwable) {
                    responseMessage = "${e.message}"
                }
            }
        }) {
            Text(stringResource(Res.string.submit_button_text)) // Updated to use string resource
        }

        if (responseMessage.isNotEmpty()) {
            Text(
                responseMessage,
                color = if (responseCode == HttpStatusCode.OK) Color.Green else Color.Red,
                style = MaterialTheme.typography.headlineMedium
            )
        } else {
            Text(responseMessage, style = MaterialTheme.typography.headlineLarge, fontSize = 10.sp)
        }
    }
}
