package com.example.lunchmate.ui.screens

import ChatScreen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController


@Composable
fun EventDetails(navController: NavController, eventName: String, userId: String) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select an Option") },
            text = {
                Text("What would you like to do?")
            },
            confirmButton = {
                Button(onClick = {
                    // Navigate to the chat screen
                    navController.navigate("chat_screen")
                    showDialog = false // Close dialog
                }) {
                    Text("Chat")
                }
            },
            dismissButton = {
                Button(onClick = {
                    // Navigate to the orders screen
                    navController.navigate("ordersScreen")
                    showDialog = false // Close dialog
                }) {
                    Text("View Orders")
                }
            }
        )
    } else {
        // Show the chat screen directly if no dialog is present
        ChatScreen(eventName, userId)
    }
}
