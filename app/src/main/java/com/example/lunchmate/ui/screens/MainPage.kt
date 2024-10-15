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
    var events by remember { mutableStateOf(listOf<Map<String, String>>()) } // Events for the location
    var expanded by remember { mutableStateOf(false) } // State for DropdownMenu
    val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Gothenburg") // Predefined locations

    // Fetch the location and events from Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(username)

        // Fetch user location
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                location = document.getString("location") ?: "Karlskrona"
                // Fetch events based on the location
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

    // Main Page UI
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Scrollable content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Where do you want to eat today in $location?",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White // Change text color to white
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display Dropdown for selecting location
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
                            updateLocation(loc) // Update location in Firestore
                            expanded = false // Close dropdown after selection
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show events for the selected location
            if (events.isNotEmpty()) {
                Text(text = "Lunch Events in $location:", color = Color.White)
                events.forEach { event ->
                    // Ensure safe access to map values
                    val eventTitle = event["title"] ?: "Untitled Event"
                    val eventDescription = event["description"] ?: "No description available"
                    val restaurantName = event["restaurant"] ?: "Unknown Restaurant"

                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = eventTitle, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(text = eventDescription, color = Color.White)
                        Text(text = "Restaurant: $restaurantName", color = Color.White)

                        // Button to join the event
                        Button(onClick = {
                            // Navigate to the event page (You can pass event details if necessary)
                            navController.navigate("event_page")
                        }) {
                            Text(text = "Join Event")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
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

// Function to fetch events for a selected location from Firestore
fun fetchEventsForLocation(location: String, onEventsFetched: (List<Map<String, String>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("events").whereEqualTo("location", location).get()
        .addOnSuccessListener { querySnapshot ->
            val eventsList = querySnapshot.documents.map { document ->
                document.data?.mapValues { it.value.toString() } ?: emptyMap()
            }
            onEventsFetched(eventsList)
        }
        .addOnFailureListener { exception ->
            Log.e("fetchEventsForLocation", "Error fetching events: ${exception.message}")
            onEventsFetched(emptyList())
        }
}

