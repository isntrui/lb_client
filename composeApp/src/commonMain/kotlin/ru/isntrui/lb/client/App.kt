package ru.isntrui.lb.client

import CustomTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.isntrui.lb.client.ui.AdminPanel
import ru.isntrui.lb.client.ui.auth.Login
import ru.isntrui.lb.client.ui.auth.Registration
import ru.isntrui.lb.client.ui.Dashboard
import ru.isntrui.lb.client.ui.DesignSection
import ru.isntrui.lb.client.ui.SongsSection
import ru.isntrui.lb.client.ui.UsersAdminPanel
import ru.isntrui.lb.client.ui.WavesAdminPanel

@Composable
@Preview
fun App() {
    CustomTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "login") {
            composable("login") {
                Login(navController)
            }
            composable("registration") {
                Registration(navController)
            }
            composable("dashboard") {
                Dashboard(navController)
            }
            composable("songs") {
                SongsSection(navController)
            }
            composable("designs") {
                DesignSection(navController)
            }
            composable("settings") {
                AdminPanel(navController)
            }
            composable("users") {
                UsersAdminPanel(navController)
            }
            composable("waves") {
                WavesAdminPanel(navController)
            }
        }
    }
}
