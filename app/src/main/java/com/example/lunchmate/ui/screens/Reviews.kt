@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.lunchmate.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController


@Composable
fun Reviews(navController: NavController) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFCE4EC)) // Light pink background color
            .padding(16.dp)
    ) {
        // Search Bar with Edit Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .background(Color(0xFFF1E6FF), RoundedCornerShape(24.dp)) // Rounded background with color
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )

            // Search TextField
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search Restaurant", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            // Edit Icon Button
            IconButton(
                onClick = { /* Navigate to review-writing page */ },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFCE4EC), RoundedCornerShape(18.dp)) // Background color for button
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Write Review",
                    tint = Color.Gray
                )
            }
        }
    }
}
