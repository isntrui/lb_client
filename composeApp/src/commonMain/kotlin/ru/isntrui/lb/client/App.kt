package ru.isntrui.lb.client

import CustomTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.isntrui.lb.client.ui.Login

@Composable
@Preview
fun App() {
    CustomTheme {
        Login()
    }
}