package com.example.lunchmate.ui.screens

import BottomNavBar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
//import com.example.lunchmate.userselectedresturant
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

var eventcreator= ""

//var eventNamer :String = ""


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEvents(navController: NavController, Location: String, userName: String,eventName: String? = null ) {
    val fontSize = 16.sp
    val textColour = Color.Black
    val context = LocalContext.current

    // Mutable state for event details
    //var eventName = eventNamer
    var eventName by remember { mutableStateOf(eventName ?: "") }

    var eventDate by remember { mutableStateOf(getFormattedDate()) }
    var eventTime by remember { mutableStateOf(getFormattedTime()) }
    var eventDescription by remember { mutableStateOf("") }
    val createdBy = userName // Set username as constant
    val location = Location // Set location as constant
    var pickupDineIn by remember { mutableStateOf("Pick up") }
    var isPickup by remember { mutableStateOf(true) }
    var isEventEnded by remember { mutableStateOf(false) }

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Show DatePicker
    val showDatePicker = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                eventDate = String.format("%02d/%02d/%d", day, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Show TimePicker
    val showTimePicker = {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                eventTime = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

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
                    onValueChange = { eventName = it },
                    placeholder = "Enter restaurant name"
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Description Input
                Text("Description", fontSize = fontSize, color = textColour)
                Spacer(modifier = Modifier.height(8.dp))
                TextFieldWithLabel(
                    value = eventDescription,
                    onValueChange = { eventDescription = it },
                    placeholder = "Give a comment"
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
                        Button(onClick = showDatePicker) {
                            Text("Select Date")
                        }
                        TextFieldWithLabel(
                            value = eventDate,
                            onValueChange = {},
                            placeholder = "DD/MM/YYYY"
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Time Input
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Button(onClick = showTimePicker) {
                            Text("Select Time")
                        }
                        TextFieldWithLabel(
                            value = eventTime,
                            onValueChange = {},
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
                eventcreator=createdBy

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
                                location = location,
                                isEventEnded = false,
                                etaStart = false
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
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(text = placeholder, color = Color.Gray)
            }
            innerTextField()
        }
    )
}
fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
