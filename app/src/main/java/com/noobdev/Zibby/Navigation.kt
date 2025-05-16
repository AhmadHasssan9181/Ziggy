package com.noobdev.Zibby

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.noobdev.Zibby.screens.Forgot
import com.noobdev.Zibby.screens.Signup

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen ("signup")
    object Forgot : Screen ("forgot")
    object Home : Screen("home")
    object Settings : Screen("settings")
}
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            TravelMapScreen(navController)
        }
        composable(Screen.Signup.route) {
            Signup(navController)
        }
        composable(Screen.Forgot.route) {
            Forgot(navController)
        }
        composable(Screen.Home.route){
        }
    }
}