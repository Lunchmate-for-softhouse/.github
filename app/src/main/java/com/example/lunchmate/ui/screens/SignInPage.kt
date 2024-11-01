package com.example.lunchmate.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lunchmate.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInPage(navController: NavController, activity: Activity) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginStatus by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authRepository = AuthRepository(activity)
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    // Launcher for Google Sign-In Intent
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        scope.launch {
            try {
                val account: GoogleSignInAccount? = task.getResult(Exception::class.java)
                account?.let {
                    authRepository.firebaseAuthWithGoogle(
                        account = it,
                        onUserExists = {
                            loginStatus = "Welcome back, ${account.displayName}!"
                            navController.navigate("main_page/${account.displayName}")
                        },
                        onUserDoesNotExist = { newAccount ->
                            navController.navigate("google_registration_page/${newAccount.displayName}")
                        },
                        onFailure = { exception ->
                            loginStatus = "Google Sign-In Failed: ${exception.message}"
                        }
                    )
                } ?: run {
                    loginStatus = "Google Sign-In Failed!"
                }
            } catch (e: Exception) {
                loginStatus = "Google Sign-In Error: ${e.message}"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Username field
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password field
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Password visibility toggle
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(text = if (passwordVisible) "Hide Password" else "Show Password")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign In Button (For username/password sign-in logic)
                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty()) {
                            loginStatus = "Please enter both username and password."
                        } else {
                            // Query Firestore for Username/Password Authentication
                            db.collection("users")
                                .whereEqualTo("username", username)
                                .whereEqualTo("password", password)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        loginStatus = "Login Successful!"
                                        navController.navigate("main_page/$username")
                                    } else {
                                        loginStatus = "Login Failed! Invalid credentials."
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    loginStatus = "Error accessing database: ${exception.message}"
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sign In")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-In Button
                Button(
                    onClick = {
                        val signInIntent = authRepository.getGoogleSignInIntent()
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sign In with Google")
                }

                // Display login status (success/failure)
                Text(
                    text = loginStatus,
                    modifier = Modifier.padding(top = 16.dp),
                    color = if (loginStatus.contains("Welcome")) Color.Green else Color.Red
                )
            }
        }
    }
}
