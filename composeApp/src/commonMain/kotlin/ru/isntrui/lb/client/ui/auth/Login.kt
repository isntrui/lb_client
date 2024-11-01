package ru.isntrui.lb.client.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import ru.isntrui.lb.client.Net
import ru.isntrui.lb.client.requests.LoginRequest
import ru.isntrui.lb.client.responses.LoginResponse

data class User(
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
        return username.matches(Regex("^[a-zA-Z0-9_]{3,16}\$"))
    }
}

@Composable
fun Login(navController: NavController) {

    var loginState by remember { mutableStateOf(User("", "")) }
    var responseMessage by remember { mutableStateOf("") } // State for response message
    var responseCode by remember { mutableStateOf<HttpStatusCode?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Lyceum Bells",
            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
            fontSize = 120.sp
        )
        Spacer(Modifier.height(30.dp))

        TextField(
            label = { Text("юзернейм") },
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
                    else Text("Только латинские буквы, цифры и _")
                }
            }
        )

        TextField(
            label = { Text("пароль") },
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
                if (loginState.password.length > 21) {
                    Text("Не более 21 символа")
                } else if (loginState.isShownPasswordError) {
                    Text("Не менее 8 символов, хотя бы одну букву, спецсимвол и одну цифру")
                }
            },
            visualTransformation = if (!loginState.isShownPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (loginState.password.isNotEmpty()) {
                    TextButton(onClick = {
                        loginState = loginState.copy(isShownPassword = !loginState.isShownPassword)
                    }) {
                        Text(if (loginState.isShownPassword) "Скрыть" else "Показать")
                    }
                }
            },
            singleLine = true
        )

        val scope = rememberCoroutineScope()
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
                        Net.httpClient.post("http://igw.isntrui.ru/api/auth/sign-in") {
                            setBody(LoginRequest(loginState.username.lowercase(), loginState.password))
                        }
                    responseMessage = when (response.status) {
                        HttpStatusCode.OK -> "Вы вошли"
                        HttpStatusCode.BadRequest -> "Неверный логин или пароль"
                        HttpStatusCode.InternalServerError -> "Технические проблемы. Сообщите, пожалуйста, своему координатору"
                        else -> "Неизвестная ошибка"
                    }
                    responseCode = response.status
                } catch (e: Throwable) {
                    responseMessage = "Ошибка сети: ${e.message}"
                }
            }
        }, enabled = loginState.isUsernameValid() && loginState.isPasswordValid()) {
            Text("Войти")
        }

        OutlinedButton(onClick = {
            navController.navigate("main")
        }) {
            Text("Регистрация")
        }

        TextButton(onClick = {}) {
            Text("Забыл/-а пароль?")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}