package com.example.lunchmate.ui.screens

import android.app.Activity
import android.content.Intent
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
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    val authRepository = AuthRepository(activity)

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
                        it,
                        onSuccess = {
                            loginStatus = "Google Sign-In Successful!"
                            // Store user in Firestore (username, password as GoogleAccount, location)
                            val userMap = hashMapOf(
                                "username" to account.displayName.orEmpty(),
                                "password" to "GoogleAccount",
                                "location" to "Malmö" // hardcoded location
                            )
                            db.collection("users").document(account.displayName.orEmpty())
                                .set(userMap)
                                .addOnSuccessListener {
                                    navController.navigate("main_page/${account.displayName}")
                                }
                                .addOnFailureListener { e ->
                                    loginStatus = "Firestore error: ${e.message}"
                                }
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
                // Username/Password Login
                BasicTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (username.isEmpty()) {
                            Text(text = "Enter Username", color = Color.Gray)
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        if (password.isEmpty()) {
                            Text(text = "Enter Password", color = Color.Gray)
                        }
                        innerTextField()
                    }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Sign In")
                }

                // Google Sign-In Button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val signInIntent = authRepository.getGoogleSignInIntent()
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Sign In with Google")
                }

                // Display login status (success/failure)
                Text(
                    text = loginStatus,
                    modifier = Modifier.padding(top = 16.dp),
                    color = if (loginStatus.contains("Successful")) Color.Green else Color.Red
                )
            }
        }
    }
}
