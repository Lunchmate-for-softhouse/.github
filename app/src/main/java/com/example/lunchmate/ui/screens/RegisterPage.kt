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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var swishNumber by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // Controls dropdown visibility
    var showSuccessDialog by remember { mutableStateOf(false) } // Controls success dialog visibility

    val db = FirebaseFirestore.getInstance() // Initialize Firestore
    val locations = listOf("Malmö", "Karlskrona", "Stockholm", "Växjö", "Karlshamn", "Jönköping")

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

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPasswordField = true,
            modifier = Modifier.fillMaxWidth()
        )

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
                label = { Text("Default Location") },
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
                // Create a User object
                val newUser = User(
                    username = username,
                    password = password,
                    swishNumber = swishNumber,
                    location = selectedLocation
                )

                // Save to Firestore
                db.collection("users").document(username)
                    .set(newUser)
                    .addOnSuccessListener {
                        // Show the success dialog on successful registration
                        showSuccessDialog = true
                    }
                    .addOnFailureListener { e ->
                        // Handle the error (e.g., show a Toast message)
                        e.printStackTrace()
                    }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

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
                    navController.navigate("sign_in") // Navigate to sign-in page
                }) {
                    Text("OK")
                }
            },
            title = { Text("Registration Successful") },
            text = { Text("You have registered successfully!")  }
        )
    }
}