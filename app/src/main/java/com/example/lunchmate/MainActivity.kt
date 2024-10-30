package com.example.lunchmate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.lunchmate.ui.screens.HomePage
import com.example.lunchmate.ui.screens.MainPage
import com.example.lunchmate.ui.screens.RegisterPage
import com.example.lunchmate.ui.screens.SignInPage
import com.example.lunchmate.ui.theme.LunchMateTheme
import com.google.firebase.FirebaseApp
//import com.example.lunchmate.ui.screens.EventPage // Import the EventPage
import java.util.concurrent.TimeUnit


/*class MainActivity : ComponentActivity() {
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
                    MainAppNavHost()
                }
            }
        }
    }
}

@Composable
fun MainAppNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePage(navController = navController)
        }
        composable("sign_in") {
            SignInPage(navController = navController) // Sign-in screen
        }
        composable("register") {
            RegisterPage(navController = navController) // Register screen
        }
        composable(
            route = "main_page/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                MainPage(navController, username)
            }
        }
        composable("main_page") {
                MainPage(navController = navController, username = "example_user")
            }

            // Composable for EventPage with dynamic restaurantName argument
        composable(
                route = "event_page/{restaurantName}",
                arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })
            ) { backStackEntry ->
                val restaurantName =
                    backStackEntry.arguments?.getString("restaurantName") ?: "Unknown Restaurant"
                EventPage(navController = navController, restaurantName = restaurantName)
            }
        }
    }*/




import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.lunchmate.com.example.lunchmate.ui.screens.ReviewPage
import com.example.lunchmate.ui.screens.CreateEvents
import com.example.lunchmate.ui.screens.EventsMade
import com.example.lunchmate.ui.screens.ReviewNotificationWorker
import com.example.lunchmate.ui.screens.chaneloc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*class MainActivity : ComponentActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }

    // Declare the permission launcher
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Delay Firebase initialization if not immediately needed
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseApp.initializeApp(applicationContext)
        }
        // Register permission launcher for notifications
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "Notification permission required to remind you to review",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // Check and request notification permission
        checkAndRequestNotificationPermission()

        setContent {
            LunchMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A23) // Default app background color
                ) {
                    MainAppNavHost(context = this)
                }
            }
        }
    }


    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Do nothing here, wait for user to confirm the order
                }
                else -> {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }


    // Function to schedule a notification after the order is confirmed
    private fun scheduleReviewNotification() {
        // Schedule the WorkManager notification with 1-minute delay
        val notificationWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ReviewNotificationWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES) // Delay of 1 minute
            .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }

    // Call this function when the order is confirmed
    fun confirmOrder() {
        clearSelectedOrders()
        scheduleReviewNotification()
        finish()
    }

    private fun clearSelectedOrders() {
        Toast.makeText(this, "Selected orders cleared.", Toast.LENGTH_SHORT).show()
    }
}

// Composable navigation host for the app
@Composable
fun MainAppNavHost(context: Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePage(navController = navController)
        }
        composable("sign_in") {
            SignInPage(navController = navController) // Sign-in screen
        }
        composable("register") {
            RegisterPage(navController = navController) // Register screen
        }

        composable(
            route = "main_page/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                MainPage(navController, username)
            }
        }

        composable("main_page") {
            MainPage(navController = navController, username = "example_user")
        }

        // Composable for EventPage with dynamic restaurantName argument
        composable(
            route = "event_page/{restaurantName}",
            arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantName = backStackEntry.arguments?.getString("restaurantName") ?: "Unknown Restaurant"
            EventPage(navController = navController, restaurantName = restaurantName, context = context) // Pass context here
        }
    }
}*/
class MainActivity : ComponentActivity() {


    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    }

    // Declare the permission launcher
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    private var shouldNavigateToReview by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Delay Firebase initialization if not immediately needed
        GlobalScope.launch(Dispatchers.IO) {
            FirebaseApp.initializeApp(applicationContext)
        }

        // Register permission launcher for notifications
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "Notification permission required to remind you to review",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Check and request notification permission
        checkAndRequestNotificationPermission()

        // Check if the activity was started from a notification
        if (intent?.getStringExtra("navigate_to") == "review") {
            shouldNavigateToReview = true
        }

        setContent {
            LunchMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A0A23) // Default app background color
                ) {
                    MainAppNavHost(context = this, shouldNavigateToReview) // Pass the navigation flag
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Do nothing here, wait for user to confirm the order
                }
                else -> {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    // Function to schedule a notification after the order is confirmed
    private fun scheduleReviewNotification() {
        // Schedule the WorkManager notification with a 1-minute delay
        val notificationWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ReviewNotificationWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES) // Delay of 1 minute
            .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)
    }

    // Call this function when the order is confirmed
    fun confirmOrder() {
        clearSelectedOrders()
        scheduleReviewNotification()
        finish()
    }

    private fun clearSelectedOrders() {
        Toast.makeText(this, "Selected orders cleared.", Toast.LENGTH_SHORT).show()
    }
}

// Composable navigation host for the app
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppNavHost(context: Context, shouldNavigateToReview: Boolean) {
    var userstore = ""

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePage(navController = navController)
        }
        composable("sign_in") {
            SignInPage(navController = navController) // Sign-in screen
        }
        composable("register") {
            RegisterPage(navController = navController) // Register screen
        }

        composable(
            route = "main_page/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                userstore = username
                MainPage(navController, username)
            }
        }
            composable("current_events") {
                // Replace "user_id" with the actual logic to get the current user's ID
                val creatorName = userstore // Get the current user's ID
                EventsMade(navController = navController, creatorName = creatorName) // Pass currentUserId here
            }

        // Composable for EventPage with dynamic restaurantName argument
        composable(
            route = "event_page/{restaurantName}",
            arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantName = backStackEntry.arguments?.getString("restaurantName") ?: "Unknown Restaurant"
            //EventPage(navController = navController, restaurantName = restaurantName, context = context) // Pass context here
        }

        composable("create_event"){
            CreateEvents(navController = navController, chaneloc, userstore)
        }

        // Add the ReviewPage composable
        composable("review_pag") {
            ReviewPage(onBack = { navController.popBackStack() }) // Navigate back to the previous screen
        }
    }

    // Handle navigation to the review page after the nav host is set up
    if (shouldNavigateToReview) {
        LaunchedEffect(Unit) {
            navController.navigate("review")
        }
    }
}