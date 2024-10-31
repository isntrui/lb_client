package ru.isntrui.lb.client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class User(
    val username: String,
    val password: String,
    val isShownPassword: Boolean = false,
    val isShownUsernameError: Boolean = false,
    val isShownPasswordError: Boolean = false
) {
    fun isPasswordValid(): Boolean {
        return password.matches(Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,21}\$"))
    }

    fun isUsernameValid(): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{3,16}\$"))
    }
}

@Composable
fun Login() {
    var loginState by remember<MutableState<User>> {
        mutableStateOf<User>(value = User("", ""))
    }


    Column(verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically), modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("LB Tool", fontFamily = MaterialTheme.typography.headlineLarge.fontFamily, fontSize = 120.sp)
        Spacer(Modifier.height(30.dp))
        TextField(
            label = {
                Text("юзернейм")
            },
            value = loginState.username,
            onValueChange = {
                loginState = loginState.copy(username = it)
                loginState = if (!loginState.isUsernameValid()) {
                    loginState.copy(isShownUsernameError = true)
                } else {
                    loginState.copy(isShownUsernameError = false)
                }
            },
            isError = loginState.isShownUsernameError, singleLine = true,
            supportingText = {
                if (loginState.isShownUsernameError) {
                    if (loginState.username.length < 3) Text("Не менее 3 символов")
                    else if (loginState.username.length > 16) Text(
                        "Не более 16 символов"
                    ) else Text("Только латинские буквы, цифры и _")
                } else null
            }
        )

        TextField(
            label = {
                Text("пароль")
            },
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
                } else null
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            println("loginState = $loginState")
        }, enabled = loginState.isUsernameValid() && loginState.isPasswordValid()) {
            Text("Войти")
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}