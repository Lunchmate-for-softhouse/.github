package com.example.lunchmate.com.example.lunchmate.ui.screens


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrder() {
    // Main UI layout for the Orders Screen
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Orders") },
                // You can add actions or navigation icon if needed
            )
        },
        content = { padding ->
            // Content of the screen
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Display a message indicating where orders will be shown
                Text(
                    text = "Here your orders will be shown.",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    )
}

