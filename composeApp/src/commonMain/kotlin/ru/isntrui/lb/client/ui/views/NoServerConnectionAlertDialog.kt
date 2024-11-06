package ru.isntrui.lb.client.ui.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import io.ktor.websocket.Frame

@Composable
fun NoServerConnectionAlertDialog(e: Exception) {
    val openDialog = remember { mutableStateOf(true) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {  },
            title = { Text(text = "Проблемы с подключением к серверу...") },
            text = { Text("Напиши своему координатору и скинь следующий текст: \n${e.message}") },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString("Проблемы с подключением к серверу... \n${e.message}"))
                    }
                ) {
                    Text("Скопировать", fontSize = 22.sp)
                }
            }
        )
    }
}