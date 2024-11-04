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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.font.FontWeight

var nameofevent= ""

@Composable
fun EventsMade(navController: NavController, creatorName: String) {
    val eventsList = remember { mutableStateListOf<Event>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Stockholm") } // Default selected location
    val eventManager = EventManager() // Instantiate EventManager

    // Debugging: Log when events are fetched
    LaunchedEffect(Unit) {
        try {
            val events = eventManager.fetchAndCleanEvents()
            events.forEach { event ->
                if (!eventsList.contains(event)) {
                    eventsList.add(event)
                    println("Event fetched: ${event.eventName} at ${event.location}")
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error"
            println("Error fetching events: $errorMessage")
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        eventManager.startListeningForEvents { newEvents ->
            newEvents.forEach { event ->
                if (!eventsList.contains(event)) {
                    eventsList.add(event)
                    println("New event detected: ${event.eventName} at ${event.location}")
                }
            }
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
            Text(
                text = "Hello $creatorName!",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Where do you want to eat today?",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            var expanded by remember { mutableStateOf(false) }
            val locations = listOf("Stockholm", "Malmö", "Växjö", "Karlskrona", "Karlshamn", "Kalmar","Jönköping","Luleå","Uppsala","Sarajevo")

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

            when {
                isLoading -> CircularProgressIndicator()
                errorMessage.isNotEmpty() -> Text(errorMessage, color = Color.Red)
                eventsList.none { it.location == selectedLocation } -> Text("No events available for $selectedLocation")
                else -> {
                    LazyColumn {
                        items(eventsList.filter { it.location == selectedLocation }) { event ->
                            if (event.createdBy == creatorName) {
                                EventCreatorItem(event, navController)
                            } else {
                                EventItem(event, navController)
                            }
                            println("Displaying event: ${event.eventName} at ${event.location}")
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
                            nameofevent = event.eventName
                            navController.navigate("events_detail") // Leave this empty for now
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

            // Add event description below the event name
            Text(
                text = event.eventDescription, // Assuming event has a description property
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold // Make the text bold
                ),
                color = Color.Black, // Change the color to black for better visibility
                modifier = Modifier.padding(bottom = 8.dp) // Space between description and other elements
            )

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
fun EventCreatorItem(event: Event, navController: NavController) {
    var remainingTime by remember { mutableStateOf("Loading...") }
    val eventDateTime = "${event.eventDate} ${event.eventTime}"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val eventManager = EventManager() // Instantiate EventManager


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
                            navController.navigate("events_detail") // Leave this empty for now
                        }
                )
            }
            // Add event description below the event name
            Text(
                text = event.eventDescription, // Assuming event has a description property
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold // Make the text bold
                ),
                color = Color.Black, // Change the color to black for better visibility
                modifier = Modifier.padding(bottom = 8.dp) // Space between description and other elements
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = "Event Date")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Date: ${event.eventDate}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "People Count")
                Spacer(modifier = Modifier.width(4.dp))
                Text("People: ${event.peopleCount}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            }
            // Display pickup or dine-in option
            Text("Option: ${if (event.pickupDineIn == "Pickup") "Pickup" else "Dine In"}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

            // Display the remaining time
            Text("Time Left: $remainingTime", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))

            // Buttons for creator functionality
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly // Evenly space the buttons
            ) {
                Button(onClick = { /* Handle Edit Event */ }) {
                    Text("Edit")
                }
                Button(onClick = { eventManager.deleteEvent(event) }) {
                    Text("Delete")
                }
                Button(onClick = { /* Handle Set ETA */ }) {
                    Text("Set ETA")
                }
            }
        }
    }
}






data class Event(
    val eventName: String = "",
    val eventDate: String = "",
    val eventTime: String = "",
    val location: String = "",
    val eventDescription: String = "",
    val createdBy: String = "",
    val pickupDineIn: String = "",
    val peopleCount: Int = 0 // Added people count field
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        return eventName == other.eventName &&
                eventDate == other.eventDate &&
                eventTime == other.eventTime &&
                createdBy == other.createdBy
    }

    override fun hashCode(): Int {
        return Objects.hash(eventName, eventDate, eventTime, createdBy)
    }
}
