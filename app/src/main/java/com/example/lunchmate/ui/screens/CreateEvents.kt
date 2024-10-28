package com.example.lunchmate.ui.screens

import BottomNavBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowDropDown


@Composable
fun CreateEvents(navController: NavController) {
    val fontSize = 16.sp
    val textColour = Color.Black
    val backgroundColor = Color(0xFFFFF1E6) // Soft peach background color

    // Event details as mutable state
    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }  // New location state
    var eventDate by remember { mutableStateOf(getFormattedDate()) }
    var eventTime by remember { mutableStateOf(getFormattedTime()) }
    var pickupOrDineIn by remember { mutableStateOf("Pick up") }
    var createdBy by remember { mutableStateOf("Default: You") }

    // Firestore instance
    val firestore = FirebaseFirestore.getInstance()

    // Formatter for date and time
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Function to save event to Firestore
    fun saveEventToFirestore() {
        val event = hashMapOf(
            "Name of Restaurant" to eventName,
            "Event Description" to eventDescription,
            "Location" to eventLocation,  // Add location to Firestore event
            "Date" to eventDate,
            "Time" to eventTime,
            "Pickup or Dine In" to pickupOrDineIn,
            "Event Created by" to createdBy
        )

        firestore.collection("events")
            .add(event)
            .addOnSuccessListener {
                navController.navigate("upcoming_events")
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Create Event",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Event name text field
        Text(
            text = "Event name",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = eventName,
            onValueChange = { eventName = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (eventName.isEmpty()) Text("Restaurant name", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description text field
        Text(
            text = "Description",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = eventDescription,
            onValueChange = { eventDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (eventDescription.isEmpty()) Text("Write description", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Göteborg")
        var expanded by remember { mutableStateOf(false) }

        // Location dropdown menu
        Text(
            text = "Location",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (eventLocation.isEmpty()) "Choose location" else eventLocation,
                    color = if (eventLocation.isEmpty()) Color.Gray else Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown Icon")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location) },
                        onClick = {
                            eventLocation = location
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date input
        Text(
            text = "Date",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = eventDate,
            onValueChange = { eventDate = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (eventDate.isEmpty()) Text("DD/MM/YYYY", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Time input
        Text(
            text = "Time",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = eventTime,
            onValueChange = { eventTime = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (eventTime.isEmpty()) Text("HH:MM", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pickup or Dine-in section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pick up", Modifier.padding(8.dp))
            RadioButton(
                selected = pickupOrDineIn == "Pick up",
                onClick = { pickupOrDineIn = "Pick up" }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Dine in", Modifier.padding(8.dp))
            RadioButton(
                selected = pickupOrDineIn == "Dine in",
                onClick = { pickupOrDineIn = "Dine in" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Created by text field
        Text(
            text = "Created by",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BasicTextField(
            value = createdBy,
            onValueChange = { createdBy = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                if (createdBy.isEmpty()) Text("Created by", color = Color.Gray)
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Button to save event
        Button(
            onClick = { saveEventToFirestore() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text(text = "Create Event", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom navigation bar
        BottomNavBar(navController = navController)
    }
}

fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

fun getFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}