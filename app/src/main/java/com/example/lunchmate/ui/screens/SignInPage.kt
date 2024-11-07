package com.example.lunchmate.ui.screens
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
var chaneloc = ""

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInPage(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginStatus by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text(text = "Enter Username", color = Color.Gray) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text(text = "Enter Password", color = Color.Gray) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )

                Text(
                    text = if (passwordVisible) "Hide Password" else "Show Password",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { passwordVisible = !passwordVisible },
                    color = Color.Blue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty()) {
                            loginStatus = "Please enter both username and password."
                        } else {
                            // Query Firestore for all users
                            db.collection("users")
                                .get()
                                .addOnSuccessListener { documents ->
                                    Log.d("SignInPage", "Documents fetched: ${documents.size()}")

                                    var userFound = false
                                    var storedPassword: String? = null

                                    for (document in documents) {
                                        val storedUsername = document.getString("username")?.lowercase()
                                        storedPassword = document.getString("password")

                                        // Case-insensitive username comparison
                                        if (username.lowercase() == storedUsername) {
                                            userFound = true
                                            chaneloc = document.getString("location") ?: "Unknown Location" // Update location
                                            Log.d("SignInPage", "User location: $chaneloc")
                                            break
                                        }
                                    }

                                    if (userFound && password.lowercase() == storedPassword?.lowercase()) {
                                        loginStatus = "Login Successful!"
                                        // Navigate to MainPage with the username
                                        navController.navigate("main_page/$username")
                                    } else {
                                        loginStatus = "Login Failed! Invalid credentials."
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("SignInPage", "Error fetching documents", e)
                                    loginStatus = "Login Failed! Please try again."
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Sign In")
                }

                Text(
                    text = loginStatus,
                    modifier = Modifier.padding(top = 16.dp),
                    color = if (loginStatus.contains("Successful")) Color.Green else Color.Red
                )
            }
        }
    }
}