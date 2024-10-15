package com.example.lunchmate.ui.screens

import BottomNavBar
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun MainPage(navController: NavController, username: String) {
    var location by remember { mutableStateOf("Karlskrona") } // Default location
    var events by remember { mutableStateOf(listOf<String>()) } // Events for the location
    var expanded by remember { mutableStateOf(false) } // State for DropdownMenu
    val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Gothenburg") // Predefined locations

    // Fetch the location from Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(username)
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                location = document.getString("location") ?: "Karlskrona"
                Log.d("MainPage", "Fetched location from Firestore: $location")
                fetchEventsForLocation(location) { eventList ->
                    events = eventList
                    Log.d("MainPage", "Fetched events: $eventList")
                }
            } else {
                Log.e("MainPage", "User document not found.")
            }
        }.addOnFailureListener { exception ->
            // Handle error (e.g., log it)
            Log.e("MainPage", "Error fetching user data: ${exception.message}")
        }
    }

    // Function to update location in Firestore
    fun updateLocation(newLocation: String) {
        val db = FirebaseFirestore.getInstance()
        Log.d("MainPage", "Updating location to Firestore: $newLocation")
        db.collection("users").document(username)
            .update("location", newLocation)
            .addOnSuccessListener {
                Log.d("MainPage", "Location updated successfully to $newLocation")
                location = newLocation
                fetchEventsForLocation(location) { eventList ->
                    events = eventList
                    Log.d("MainPage", "Fetched events after location update: $eventList")
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
        // Scrollable content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Welcome to Lunch Mate in $location!",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White // Change text color to white
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display TextField for current location
            TextField(
                value = location,
                onValueChange = {}, // Prevent direct editing
                label = { Text("Select Location") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                readOnly = true // Keep it read-only
            )

            // Show DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { loc ->
                    DropdownMenuItem(
                        text = { Text(loc) },
                        onClick = {
                            Log.d("MainPage", "Dropdown item clicked: $loc")
                            updateLocation(loc) // Update location in Firestore
                            expanded = false // Close dropdown after selection
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show events for the selected location
            if (events.isNotEmpty()) {
                Text(text = "Events in $location:", color = Color.White)
                events.forEach { event ->
                    Text(text = "- $event", color = Color.White)
                }
            } else {
                Text(text = "No events found for $location.", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to create new lunch event
            Button(onClick = { navController.navigate("create_lunch_event") }) {
                Text(text = "Create Lunch Event")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to view all events
            Button(onClick = { navController.navigate("view_lunch_events") }) {
                Text(text = "View All Lunch Events")
            }
        }

        // Fixed BottomAppBar
        BottomNavBar(navController = navController)
    }
}

// Function to fetch events based on location
fun fetchEventsForLocation(location: String, callback: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    Log.d("MainPage", "Fetching events for location: $location")
    db.collection("events")
        .whereEqualTo("location", location)
        .get()
        .addOnSuccessListener { result ->
            val eventList = result.map { it.getString("name") ?: "Unnamed Event" }
            callback(eventList)
            Log.d("MainPage", "Fetched event list: $eventList")
        }
        .addOnFailureListener { exception ->
            callback(emptyList()) // Return an empty list in case of failure
            Log.e("MainPage", "Error fetching events: ${exception.message}")
        }
}
