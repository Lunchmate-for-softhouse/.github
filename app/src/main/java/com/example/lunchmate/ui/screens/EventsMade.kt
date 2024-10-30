package com.example.lunchmate.ui.screens

import BottomNavBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsMade(navController: NavController,creatorName: String) {
    val eventsList = remember { mutableStateListOf<Event>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val eventManager = EventManager() // Instantiate EventManager

    // Fetch and clean up events from Firestore
    LaunchedEffect(Unit) {
        try {
            eventsList.clear()
            val events = eventManager.fetchAndCleanEvents() // Use the new method
            eventsList.addAll(events)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Greeting line for the creator
            Text(
                text = "Hello $creatorName!",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Created Events", style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp))

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> CircularProgressIndicator()
                errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red)
                eventsList.isEmpty() -> Text("No events available")
                else -> {
                    LazyColumn {
                        items(eventsList) { event ->
                            // Check if the current user is the creator of the event
                            if (event.createdBy == creatorName) {
                                // Render creator view with the creator's name
                                EventCreatorItem(event, navController, creatorName)
                            } else {
                                // Render attendee view
                                EventItem(event, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event, navController: NavController) {
    var remainingTime by remember { mutableStateOf("Loading...") }
    val eventDateTime = "${event.eventDate} ${event.eventTime}"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Timer logic to update remaining time
    LaunchedEffect(eventDateTime) {
        while (true) {
            try {
                val eventTime = dateFormat.parse(eventDateTime) ?: Date()
                val currentTime = Date()
                val timeDiff = eventTime.time - currentTime.time

                if (timeDiff > 0) {
                    val hours = (timeDiff / (1000 * 60 * 60)).toInt()
                    val minutes = ((timeDiff / (1000 * 60)) % 60).toInt()
                    val seconds = ((timeDiff / 1000) % 60).toInt()
                    remainingTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    remainingTime = "Event ended"
                    break // Stop updating if the event has passed
                }
            } catch (e: Exception) {
                remainingTime = "Invalid date"
                break
            }
            delay(1000) // Update every second
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Light Blue
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clickable event name
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp),
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("your_event_detail_route/${event.eventName}") // Leave this empty for now
                        }
                )
                // Menu button
                Button(
                    onClick = {
                        // Handle Menu action here
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                ) {
                    Text("Menu")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = "Event Date")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Date: ${event.eventDate}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Created By")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Created by: ${event.createdBy}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            }
            Text(event.pickupDineIn, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            Text("People: ${event.peopleCount}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

            // Display the remaining time
            Text("Time Left: $remainingTime", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

            // Non-creator UI (for participants)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        navController.navigate("your_join_event_route/${event.eventName}") // Update with actual route
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Filled.PersonAdd, contentDescription = "Join Event")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
fun EventCreatorItem(event: Event, navController: NavController, creatorName: String) {
    // Similar to EventItem but with creator-specific functionality
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Light Blue
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Greeting line for the creator
            Text(
                text = "Hello $creatorName!",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                color = Color.Black
            )
            // Event details
            Text(event.eventName, style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp), color = Color.Black)
            Text("Created by: ${event.createdBy}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

            // Add additional options for the creator, like editing or deleting the event
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { /* Handle Edit Event */ }) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* Handle Delete Event */ }) {
                    Text("Delete")
                }
            }
        }
    }
}



// Data class for events
data class Event(
    val eventName: String = "",
    val eventDate: String = "",
    val eventTime: String = "",
    val eventLocation: String = "",
    val eventDescription: String = "",
    val createdBy: String = "",
    val pickupDineIn: String = "",
    val peopleCount: Int = 0 // Added people count field
)
