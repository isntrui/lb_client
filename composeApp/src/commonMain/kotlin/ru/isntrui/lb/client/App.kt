package ru.isntrui.lb.client

import CustomTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.isntrui.lb.client.ui.auth.Login
import ru.isntrui.lb.client.ui.auth.Registration

@Composable
@Preview
fun App() {
    CustomTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "login") {
            composable("login") {
                Login(navController)
            }
            composable("main") {
                Registration(navController)
            }
        }
    }
}
