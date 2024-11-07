package com.example.lunchmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lunchmate.R
import com.example.lunchmate.model.User
import com.example.lunchmate.ui.components.CustomButton
import com.example.lunchmate.ui.components.CustomTextField
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import com.example.lunchmate.ui.screens.chaneloc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleRegisterPage(navController: NavController, username: String) {
    var swishNumber by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()
    val locations = listOf("Malmo", "Karlskrona", "Stockholm", "Vaxjo", "Karlshamn", "Jonkoping")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A23)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Pre filled Username from Google Account
        Text(text = "Username: $username", color = Color.White)

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            value = swishNumber,
            onValueChange = { swishNumber = it },
            label = "Swish Number",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dropdown menu for location selection
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedLocation,
                onValueChange = { },
                label = { Text("Select Location") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location) },
                        onClick = {
                            chaneloc = location
                            selectedLocation = location
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Register",
            onClick = {
                if (swishNumber.isEmpty() || selectedLocation.isEmpty()) {
                    errorMessage = "Please fill in all fields."
                } else {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@CustomButton
                    val newUser = User(
                        username = username,
                        swishNumber = swishNumber,
                        location = selectedLocation
                    )

                    db.collection("users").document(userId)
                        .set(newUser)
                        .addOnSuccessListener {
                            showSuccessDialog = true
                            errorMessage = ""
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Registration failed: ${e.message}"
                            Log.e("GoogleRegisterPage", "Firestore Error: ${e.message}")
                        }
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        TextButton(onClick = { navController.navigate("sign_in") }) {
            Text(text = "Already have an account? Sign In", color = Color.Gray)
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.navigate("main_page/$username")
                }) {
                    Text("OK")
                }
            },
            title = { Text("Registration Successful") },
            text = { Text("You have registered successfully!") }
        )
    }
}





/*package com.example.lunchmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lunchmate.R
import com.example.lunchmate.model.User
import com.example.lunchmate.ui.components.CustomButton
import com.example.lunchmate.ui.components.CustomTextField
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleRegisterPage(navController: NavController, username: String) {
    var swishNumber by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var showSuccessDialog by remember { mutableStateOf(false) } // Controls success dialog visibility
    var errorMessage by remember { mutableStateOf("") } // For error handling

    val db = FirebaseFirestore.getInstance() // Initialize Firestore
    val locations = listOf("Malmo", "Karlskrona", "Stockholm", "Vaxjo", "Karlshamn", "Jonkoping")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A23)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Pre-filled Username from Google Account
        Text(text = "Username: $username", color = Color.White)

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            value = swishNumber,
            onValueChange = { swishNumber = it },
            label = "Swish Number",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Dropdown menu for location selection
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedLocation,
                onValueChange = { },
                label = { Text("Select Location") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Ensures dropdown is positioned correctly
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location) },
                        onClick = {
                            selectedLocation = location
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Register",
            onClick = {
                // Validate input fields
                if (swishNumber.isEmpty() || selectedLocation.isEmpty()) {
                    errorMessage = "Please fill in all fields."
                    return@CustomButton
                }

                // Create a User object
                val newUser = User(
                    username = username,
                    swishNumber = swishNumber,
                    location = selectedLocation
                )

                // Save to Firestore
                db.collection("users").document(username)
                    .set(newUser)
                    .addOnSuccessListener {
                        // Show the success dialog on successful registration
                        showSuccessDialog = true
                        errorMessage = "" // Clear error message
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                        errorMessage = "Registration failed: ${e.message}"
                    }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Display error message
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        TextButton(onClick = { navController.navigate("sign_in") }) {
            Text(text = "Already have an account? Sign In", color = Color.Gray)
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.navigate("main_page/$username") // Navigate to main page after registration
                }) {
                    Text("OK")
                }
            },
            title = { Text("Registration Successful") },
            text = { Text("You have registered successfully!") }
        )
    }
}
*/