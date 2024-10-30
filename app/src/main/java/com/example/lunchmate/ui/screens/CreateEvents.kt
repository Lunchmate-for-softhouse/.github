package com.example.lunchmate.ui.screens

import BottomNavBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lunchmate.saveEvent
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEvents(navController: NavController) {
    val fontSize = 16.sp
    val textColour = Color.Black

    // Mutable state for event details
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var createdBy by remember { mutableStateOf("") }
    var pickupDineIn by remember { mutableStateOf("Pick up") }
    var isPickup by remember { mutableStateOf(true) }

    // Location Dropdown states
    var location by remember { mutableStateOf("Select Location") }
    var expanded by remember { mutableStateOf(false) }
    val locations = listOf("Malmö", "Stockholm", "Karlskrona", "Växjö","Karlshamn",
        "Jönköping","Luleå","Uppsala","Kalmar")

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Layout using Scaffold to support Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController) // Place BottomNavBar here
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBE8E3))
                    .padding(16.dp)
                    .padding(padding) // Ensure padding for Snackbar
            ) {
                // Header
                Text(
                    "Create Event",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(22.dp)) // Space after header

                // Event Name Input
                Text("Event Name", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before input field
                TextFieldWithLabel(
                    value = eventName,
                    onValueChange = { newValue -> eventName = newValue },
                    placeholder = "Enter Restaurant Name"
                )

                Spacer(modifier = Modifier.height(22.dp)) // Space after input field

                // Description Input
                Text("Description", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before input field
                TextFieldWithLabel(
                    value = eventDescription,
                    onValueChange = { newValue -> eventDescription = newValue },
                    placeholder = "Give A comment"
                )

                Spacer(modifier = Modifier.height(22.dp)) // Space after input field

                // Date and Time Inputs in a Row
                Text("Date", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before date input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date Input
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Button(onClick = { /* Show Date Input */ }) {
                            Text("Select Date")
                        }
                        TextFieldWithLabel(
                            value = eventDate,
                            onValueChange = { newValue -> eventDate = newValue },
                            placeholder = "DD/MM/YYYY" // Placeholder for Date
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Time Input
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Button(onClick = { /* Show Time Input */ }) {
                            Text("Select Time")
                        }
                        TextFieldWithLabel(
                            value = eventTime,
                            onValueChange = { newValue -> eventTime = newValue },
                            placeholder = "HH:MM" // Placeholder for Time
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp)) // Space after date and time inputs

                // Location Dropdown with Field Design
                Text("Location", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before dropdown

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (location == "Select Location") "Select Location" else location,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = if (location == "Select Location") Color.Gray else Color.Black
                        )
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Icon"
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        locations.forEach { loc ->
                            DropdownMenuItem(
                                onClick = {
                                    location = loc
                                    expanded = false
                                },
                                text = { Text(loc) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp)) // Space after dropdown

                // Pick up or Dine in Toggle
                Text("Pickup/Dine In", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before toggle buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = { isPickup = true; pickupDineIn = "Pick up" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPickup) Color(0xFFD1BCE3) else Color(0xFFFBE8E3),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Pick up")
                    }
                    TextButton(
                        onClick = { isPickup = false; pickupDineIn = "Dine in" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isPickup) Color(0xFFD1BCE3) else Color(0xFFFBE8E3),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Dine in")
                    }
                }

                Spacer(modifier = Modifier.height(22.dp)) // Space after toggle buttons

                // Created By Input
                Text("Created By", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp)) // Space before input field
                TextFieldWithLabel(
                    value = createdBy,
                    onValueChange = { newValue -> createdBy = newValue },
                    placeholder = "Enter your name"
                )

                Spacer(modifier = Modifier.height(32.dp)) // Space after input field

                // Create Event Button
                Button(
                    onClick = {
                        if (eventName.isNotBlank() && eventDate.isNotBlank() && eventTime.isNotBlank() && location != "Select Location") {
                            saveEvent(
                                eventName = eventName,
                                eventDate = eventDate,
                                eventTime = eventTime,
                                eventDescription = eventDescription,
                                createdBy = createdBy,
                                pickupDineIn = pickupDineIn,
                                location = location // Add location to the saved event
                            )
                            // Show success feedback
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Event created successfully!")
                                navController.navigate("current_events") // Replace with your actual screen
                            }
                        } else {
                            // Show error feedback
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill in all required fields")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Create Event")
                }
            }
        }
    )
}

// Helper function for labeled text fields
@Composable
fun TextFieldWithLabel(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        decorationBox = { innerTextField -> // Added decorationBox to show placeholder
            if (value.isEmpty()) {
                Text(text = placeholder, color = Color.Gray)
            }
            innerTextField()
        }
    )
}
