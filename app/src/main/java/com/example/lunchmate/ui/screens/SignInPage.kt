package com.example.lunchmate.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.lunchmate.repository.AuthRepository
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
                            // Query Firestore
                            db.collection("users")
                                .whereEqualTo("username", username)
                                .whereEqualTo("password", password)
                                .get()
                                .addOnSuccessListener { documents ->
                                    Log.d("SignInPage", "Documents fetched: ${documents.size()}")

                                    if (!documents.isEmpty) {
                                        loginStatus = "Login Successful!"
                                        // Navigate to MainPage with the username
                                        navController.navigate("main_page/$username")
                                    } else {
                                        loginStatus = "Login Failed! Invalid credentials."
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("SignInPage", "Error accessing database: ${exception.message}")
                                    loginStatus = "Error accessing database."
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Sign In")
                }

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

                Text(
                    text = loginStatus,
                    modifier = Modifier.padding(top = 16.dp),
                    color = if (loginStatus.contains("Successful")) Color.Green else Color.Red
                )
            }
        }
    }
}
