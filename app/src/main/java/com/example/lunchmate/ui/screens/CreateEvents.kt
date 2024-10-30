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
fun CreateEvents(navController: NavController, Location: String, userName: String) {
    val fontSize = 16.sp
    val textColour = Color.Black

    // Mutable state for event details
    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    val createdBy = userName // Set username as constant
    val location = Location // Set location as constant
    var pickupDineIn by remember { mutableStateOf("Pick up") }
    var isPickup by remember { mutableStateOf(true) }

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Layout using Scaffold to support Snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBE8E3))
                    .padding(16.dp)
                    .padding(padding)
            ) {
                // Header
                Text(
                    "Create Event",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Event Name Input
                Text("Event Name", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
                TextFieldWithLabel(
                    value = eventName,
                    onValueChange = { newValue -> eventName = newValue },
                    placeholder = "Enter Restaurant Name"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Description Input
                Text("Description", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
                TextFieldWithLabel(
                    value = eventDescription,
                    onValueChange = { newValue -> eventDescription = newValue },
                    placeholder = "Give A comment"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Date and Time Inputs in a Row
                Text("Date", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
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
                            placeholder = "DD/MM/YYYY"
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
                            placeholder = "HH:MM"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Display Location (uneditable)
                Text("Location", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = location,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Pick up or Dine in Toggle
                Text("Pickup/Dine In", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(22.dp))

                // Created By (uneditable)
                Text("Created By", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = createdBy,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Create Event Button
                Button(
                    onClick = {
                        if (eventName.isNotBlank() && eventDate.isNotBlank() && eventTime.isNotBlank()) {
                            saveEvent(
                                eventName = eventName,
                                eventDate = eventDate,
                                eventTime = eventTime,
                                eventDescription = eventDescription,
                                createdBy = createdBy,
                                pickupDineIn = pickupDineIn,
                                location = location
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Event created successfully!")
                                navController.navigate("current_events")
                            }
                        } else {
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
