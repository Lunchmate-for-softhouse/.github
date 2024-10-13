package com.example.lunchmate.ui.screens

import BottomNavBar
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable

fun MainPage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Your scrollable content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Welcome to Lunch Mate!")
            Button(onClick = { navController.navigate("create_lunch_event") }) {
                Text(text = "Create Lunch Event")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("view_lunch_events") }) {
                Text(text = "View Lunch Events")
            }
        }

        // Fixed BottomAppBar
        BottomNavBar(navController = navController)
    }
}

