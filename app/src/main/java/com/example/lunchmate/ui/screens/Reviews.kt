package com.example.lunchmate.ui.screens

import BottomNavBar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reviews(navController: NavController, userName: String) {

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(true) }
    var selectedLocation by remember { mutableStateOf("Stockholm") }
    var restaurantText by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var foodRating by remember { mutableIntStateOf(0) }
    var serviceRating by remember { mutableIntStateOf(0) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val locations = listOf(
        "Stockholm", "Malmö", "Växjö", "Karlskrona", "Karlshamn",
        "Kalmar", "Jönköping", "Luleå", "Uppsala", "Sarajevo"
    )
    val restaurantsByLocation = remember {
        mapOf(
            "Stockholm" to listOf("Restaurant A", "Restaurant B"),
            "Malmö" to listOf("Restaurant C", "Restaurant D"),
            "Växjö" to listOf("Restaurant E", "Restaurant F"),
            // Add more locations and corresponding restaurants
        )
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedRestaurant by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFCE4EC))
                    .padding(16.dp)
            ) {
                // Header with the review icon
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        isEditing = !isEditing
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Review")
                    }

//                    IconButton(onClick = { navController.navigate("leave_review") }) {
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = "Write Review",
//                            tint = Color.Black
//                        )
//                    }
                }
                // Location dropdown menu
                Text(text = "Choose location for Restaurant")
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = selectedLocation)
                }

                // Location dropdown menu
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Selected Location: $selectedLocation")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        locations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    selectedLocation = loc
                                    chaneloc = selectedLocation
                                    expanded = false
                                    println("Selected location: $selectedLocation")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Restaurant search bar
                if (selectedLocation.isNotEmpty()) {
                    val filteredRestaurants = restaurantsByLocation[selectedLocation]?.filter {
                        it.contains(searchText.text, ignoreCase = true)
                    } ?: emptyList()

                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search Restaurant") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.Black,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )

                    // Show filtered restaurants
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredRestaurants) { restaurant ->
                            Text(
                                text = restaurant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedRestaurant = restaurant
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show the review form when editing mode is enabled
                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        navController.navigate("submit_review")
                        isEditing = false // Reset after navigating to avoid unnecessary recompositions
                    }
                }

            }
        }
    )
}

