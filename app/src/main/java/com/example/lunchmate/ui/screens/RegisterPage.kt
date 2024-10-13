package com.example.lunchmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.unit.dp
import com.example.lunchmate.R
import com.example.lunchmate.ui.components.CustomButton
import com.example.lunchmate.ui.components.CustomTextField

@Composable
fun RegisterPage(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var swishNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A23)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with your actual logo resource
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Input fields
        CustomTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = secondName,
            onValueChange = { secondName = it },
            label = "Second Name",
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = swishNumber,
            onValueChange = { swishNumber = it },
            label = "Swish Number",
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
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isPasswordField = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Register",
            onClick = { /* Handle registration logic */ }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = {navController.navigate("sign_in") }) {
            Text(text = "Already have an account? Sign In", color = Color.Gray)
        }
    }
}
