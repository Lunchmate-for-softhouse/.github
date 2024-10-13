package com.example.lunchmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.lunchmate.ui.components.CustomButton
import com.example.lunchmate.R
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A23)), // Background color
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),  // Placeholder for your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "Sign in",
            onClick = { navController.navigate("sign_in") }
        )

        CustomButton(
            text = "Register",
            onClick = {navController.navigate("register") },
            containerColor = Color.Black,
            textColor = Color.White
        )
    }
}