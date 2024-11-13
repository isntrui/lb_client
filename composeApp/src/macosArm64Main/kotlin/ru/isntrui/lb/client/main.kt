/*
package ru.isntrui.lb.client

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.*
import androidx.compose.ui.window.rememberWindowState
import lbtool.composeapp.generated.resources.Res
import lbtool.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val winState = rememberWindowState(size = DpSize(1000.dp, 900.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "LB Tool",
        state = winState,
        icon = painterResource(Res.drawable.logo),
    ) {
        LaunchedEffect(winState.size) {
            if (winState.size.width < 770.dp) winState.size = DpSize(770.dp, winState.size.height)
            if (winState.size.height < 700.dp) winState.size = DpSize(winState.size.width, 700.dp)
        }
        App()
    }
}

@Composable
fun App() {
    // Your app content goes here
}*/
