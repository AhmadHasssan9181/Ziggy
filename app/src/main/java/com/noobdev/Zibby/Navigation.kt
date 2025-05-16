package com.noobdev.Zibby

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController // Make sure this is imported
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.noobdev.Zibby.screens.Forgot // Assuming this composable exists

import com.noobdev.Zibby.screens.Settings
import com.noobdev.Zibby.screens.Signup // Assuming this composable exists
import com.noobdev.Zibby.screens.TravelPlannerTestApp // Assuming this composable exists
import com.noobdev.Zibby.ui.theme.AppTheme // Make sure AppTheme is accessible

// Define theme colors - these are used by the Settings screen, so good to have them visible
// or ensure they are properly accessed from a theme file if Settings is in a different package.
// ... other colors if needed by Settings or other screens directly in this file

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen ("signup")
    object Forgot : Screen ("forgot")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object MainScreen : Screen("main")
    object MapView : Screen("map")
    object Chat : Screen("chat")
}

@OptIn(ExperimentalMaterial3Api::class) // Added because Settings composable uses it
@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(Screen.MainScreen.route) {
            MainScreen(navController) // Pass navController if MainScreen needs it
        }
        composable(Screen.Signup.route) {
            Signup(navController) // Pass navController if Signup needs it
        }
        composable(Screen.Forgot.route) {
            Forgot(navController) // Pass navController if Forgot needs it
        }
        composable(Screen.Home.route){
            // Your Home screen composable here, e.g., HomeScreen(navController)
        }
        composable ( Screen.MapView.route){
            TravelMapScreen(navController) // TravelMapScreen already takes NavController
        }
        composable(Screen.Chat.route){
            TravelPlannerTestApp(navController) // TravelPlannerTestApp already takes NavController
        }
        composable (Screen.Settings.route){
            Settings(
                navController = navController, // Pass the NavController
                onBackPressed = {
                    navController.popBackStack()
                },
                onThemeChanged = { newTheme ->
                    // Implement your theme changing logic here
                    // For example, update a ViewModel or a global state variable
                    println("Theme changed to: $newTheme")
                },
                onLanguageChanged = { newLanguage ->
                    // Implement your language changing logic here
                    println("Language changed to: $newLanguage")
                },
                onSignOut = {
                    // Implement your sign out logic here
                    // For example, clear user session and navigate to login
                    println("User signed out")
                    // navController.navigate(Screen.Login.route) {
                    //     popUpTo(Screen.MainScreen.route) { inclusive = true } // Example navigation
                    // }
                }
            )
        }
    }
}