package ru.isntrui.lb.client

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LB Tool",
        state = WindowState(width = 1000.dp, height = 900.dp)
    ) {
        App()
    }
}