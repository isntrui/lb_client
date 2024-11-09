package ru.isntrui.lb.client.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.hide_password_text
import lbtool.composeapp.generated.resources.incorrectuname
import lbtool.composeapp.generated.resources.incorrpassform
import lbtool.composeapp.generated.resources.login
import lbtool.composeapp.generated.resources.password_label
import lbtool.composeapp.generated.resources.registration_title
import lbtool.composeapp.generated.resources.show_password_text
import lbtool.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource
import retrieveToken
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.api.fetchCurrentUserResp
import ru.isntrui.lb.client.requests.LoginRequest
import ru.isntrui.lb.client.responses.LoginResponse
import ru.isntrui.lb.client.storage.TokenStorage
import ru.isntrui.lb.client.utils.NetworkUtils

data class Creds(
    val username: String,
    val password: String,
    val isShownPassword: Boolean = false,
    val isShownUsernameError: Boolean = false,
    val isShownPasswordError: Boolean = false,
    val isInvalidCredentials: Boolean? = null,
    var response: LoginResponse? = null,
    var responseCode: HttpStatusCode? = null
) {
    fun isPasswordValid(): Boolean {
        return password.matches(Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,21}\$")) || password == "testPass"
    }


    fun isUsernameValid(): Boolean {
        val usernameRegex = "^(?![_.-])[A-Za-z0-9_.-]{3,20}(?<![_.-])$".toRegex()
        return usernameRegex.matches(username)
    }
}

@Composable
fun Login(navController: NavController, exited: Boolean = false) {
    var loginState by remember { mutableStateOf(Creds("", "")) }
    var responseMessage by remember { mutableStateOf("") }
    var responseCode by remember { mutableStateOf<HttpStatusCode?>(null) }
    val openNoConnectionDialog = remember { mutableStateOf(true) }
    val existingToken = retrieveToken()
    val scope = rememberCoroutineScope()

    suspend fun checkTokenValidity() {
        if (!exited) {
            try {
                val response = fetchCurrentUserResp(Net.client())
                if (response.status == HttpStatusCode.OK) {
                    navController.navigate("dashboard")
                } else if (response.status != HttpStatusCode.InternalServerError && TokenStorage.getToken() != null) {
                    TokenStorage.clearToken()
                }
            } catch (e: Exception) {
                TokenStorage.clearToken()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (existingToken != null && !exited) {
            checkTokenValidity()
        }
    }

    if (!NetworkUtils.isNetworkAvailable() && openNoConnectionDialog.value) {
        AlertDialog(
            onDismissRequest = { openNoConnectionDialog.value = false },
            title = { Text(text = "нет инета :(") },
            text = { Text("для корректной работы программы нужен интернет, увы\nжми кнопку ниже, как появится!") },
            confirmButton = {
                OutlinedButton(
                    {
                        navController.navigate("login")
                    }
                ) {
                    Text("ну лан", fontSize = 22.sp)
                }
            }
        )
    } else if (TokenStorage.getToken() == null || exited) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Lyceum Bells",
                fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                fontSize = 120.sp
            )
            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                label = { Text(stringResource(Res.string.username)) },
                value = loginState.username,
                onValueChange = {
                    loginState = loginState.copy(username = it.lowercase())
                    loginState = if (!loginState.isUsernameValid()) {
                        loginState.copy(isShownUsernameError = true)
                    } else {
                        loginState.copy(isShownUsernameError = false)
                    }
                },
                isError = loginState.isShownUsernameError,
                supportingText = {
                    if (loginState.isShownUsernameError) {
                        if (loginState.username.length < 3) Text("Не менее 3 символов")
                        else if (loginState.username.length > 16) Text("Не более 16 символов")
                        else Text(stringResource(Res.string.incorrectuname))
                    }
                }
            )

            OutlinedTextField(
                label = { Text(stringResource(Res.string.password_label)) },
                value = loginState.password,
                onValueChange = {
                    loginState = loginState.copy(password = it)
                    loginState = if (!loginState.isPasswordValid()) {
                        loginState.copy(isShownPasswordError = true)
                    } else {
                        loginState.copy(isShownPasswordError = false)
                    }
                },
                isError = loginState.isShownPasswordError,
                supportingText = {
                    if (loginState.isShownPasswordError) {
                        Text(stringResource(Res.string.incorrpassform))
                    }
                },
                visualTransformation = if (!loginState.isShownPassword) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    if (loginState.password.isNotEmpty()) {
                        TextButton(onClick = {
                            loginState =
                                loginState.copy(isShownPassword = !loginState.isShownPassword)
                        }) {
                            Text(
                                if (loginState.isShownPassword) stringResource(Res.string.hide_password_text) else stringResource(
                                    Res.string.show_password_text
                                )
                            )
                        }
                    }
                },
                singleLine = true
            )

            if (responseMessage.isNotEmpty()) {
                Text(
                    responseMessage,
                    color = if (responseCode == HttpStatusCode.OK) Color.Green else Color.Red,
                    style = MaterialTheme.typography.headlineMedium
                )
            } else {
                Text("", style = MaterialTheme.typography.headlineLarge, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Button(onClick = {
                scope.launch {
                    try {
                        val response: HttpResponse =
                            Net.client().post("auth/sign-in") {
                                setBody(
                                    LoginRequest(
                                        loginState.username.lowercase(),
                                        loginState.password
                                    )
                                )
                                contentType(ContentType.Application.Json)
                            }
                        responseMessage = when (response.status) {
                            HttpStatusCode.OK -> "Вы вошли"
                            HttpStatusCode.BadRequest -> "Неверный логин или пароль"
                            HttpStatusCode.InternalServerError -> "Технические проблемы. Сообщите, пожалуйста, своему координатору"
                            else -> "Неизвестная ошибка"
                        }
                        if (response.status == HttpStatusCode.OK) {
                            val respTok =
                                Json.decodeFromString<LoginResponse>(response.bodyAsText())
                            TokenStorage.saveToken(respTok.token)
                            Net.recreate()
                            navController.navigate("dashboard")
                        }

                        responseCode = response.status
                    } catch (e: Throwable) {
                        responseMessage = "Ошибка сети: ${e.message}"
                    }
                }
            }, enabled = loginState.isUsernameValid() && loginState.isPasswordValid()) {
                Text(stringResource(Res.string.login))
            }

            OutlinedButton(onClick = {
                navController.navigate("registration")
            }) {
                Text(stringResource(Res.string.registration_title))
            }
            val openDialog = remember { mutableStateOf(false) }

            TextButton(onClick = { openDialog.value = true }) {
                Text("забыл/-а пароль?")
            }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = {
                        Text(
                            text = "забыл/-а пароль? :c",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    text = {
                        Text(
                            "напиши своему координатору по этому вопросу – он тебе поможет!",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    confirmButton = {
                        Button({ openDialog.value = false }) {
                            Text("OK", fontSize = 22.sp)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}