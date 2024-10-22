package com.example.lunchmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lunchmate.ui.screens.*
import com.example.lunchmate.ui.theme.LunchMateTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            LunchMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A23) // Default app background color
                ) {
                    MainAppNavHost(activity = this) // Pass the activity to the navigation host
                }
            }
        }
    }
}

@Composable
fun MainAppNavHost(activity: MainActivity) {
    val navController: NavHostController = rememberNavController()

    // Define the navigation host
    NavHost(navController = navController, startDestination = "home") {

        // Home page route
        composable("home") {
            HomePage(navController = navController)
        }

        // Sign In page route
        composable("sign_in") {
            SignInPage(navController = navController, activity = activity) // Pass activity to SignInPage
        }

        // Register page route
        composable("register") {
            RegisterPage(navController = navController) // Register screen
        }

        // Main page route with username argument
        composable(
            route = "main_page/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                MainPage(navController = navController, username = username)
            }
        }
    }
}
