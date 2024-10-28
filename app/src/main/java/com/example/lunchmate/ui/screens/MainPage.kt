package com.example.lunchmate.ui.screens

import BottomNavBar
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

/*@Composable
fun MainPage(navController: NavController, username: String) {
    var location by remember { mutableStateOf("Karlskrona") }
    var events by remember { mutableStateOf(listOf<Map<String, String>>()) }
    var expanded by remember { mutableStateOf(false) }
    val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Gothenburg")

    // Fetch the location and events from Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(username)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                location = document.getString("location") ?: "Karlskrona"

                fetchEventsForLocation(location) { eventList ->
                    events = eventList
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("MainPage", "Error fetching user data: ${exception.message}")
        }
    }

    // Function to update location in Firestore
    fun updateLocation(newLocation: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(username)
            .update("location", newLocation)
            .addOnSuccessListener {
                location = newLocation
                // Fetch new events for the updated location
                fetchEventsForLocation(location) { eventList ->
                    events = eventList
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainPage", "Error updating location: ${exception.message}")
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp)
        ) {
            Text(
                text = "Where do you want to eat today in $location?",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display Dropdown for selecting location
            TextField(
                value = location,
                onValueChange = {},
                label = { Text("Select Location") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                readOnly = true // Disable editing
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { loc ->
                    DropdownMenuItem(
                        text = { Text(loc) },
                        onClick = {
                            updateLocation(loc)
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show events for the selected location
            if (events.isNotEmpty()) {
                Text(text = "Lunch Events in $location:", color = Color.White)

                Column(modifier = Modifier.fillMaxWidth()) {
                    events.forEach { event ->
                        // Ensure safe access to map values
                        val eventTitle = event["title"] ?: "Untitled Event"
                        val eventDescription = event["Description"] ?: "No description available"
                        val restaurantName = event["Restaurantname"] ?: "Unknown Restaurant"

                        // Card layout for each event
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)) // Darker background for card
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = eventTitle, style = MaterialTheme.typography.titleLarge, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = eventDescription, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Restaurant: $restaurantName", color = Color.White)

                                // Button to join the event
                                Button(
                                    onClick = {
                                        navController.navigate("event_page")
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(text = "Join Event")
                                }
                            }
                        }
                    }
                }
            } else {
                Text(text = "No events found for $location.", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Fixed BottomAppBar
        BottomNavBar(navController = navController)
    }
}*/
@Composable
fun MainPage(navController: NavController, username: String) {
    var location by remember { mutableStateOf("Karlskrona") }
    var expanded by remember { mutableStateOf(false) }
    val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Gothenburg")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp)
        ) {
            Text(
                text = "Where do you want to eat today in $location?",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown for selecting location
            TextField(
                value = location,
                onValueChange = {},
                label = { Text("Select Location") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                readOnly = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { loc ->
                    DropdownMenuItem(
                        text = { Text(loc) },
                        onClick = {
                            location = loc
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate to the EventPage directly
            Button(
                onClick = {
                    // Navigate to EventPage with a hardcoded restaurant name
                    navController.navigate("event_page/KFC")  // Pass "KFC" as restaurant name
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Go to Event Page")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        BottomNavBar(navController = navController)
    }
}
