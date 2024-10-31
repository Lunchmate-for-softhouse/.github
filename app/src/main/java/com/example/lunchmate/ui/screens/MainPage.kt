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

var chaneloc =""
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController, username: String) {}

    /*var location by remember { mutableStateOf("Karlskrona") }
    var expanded by remember { mutableStateOf(false) }
    val locations = listOf("Karlskrona", "Stockholm", "Malmö", "Gothenburg")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp)
        ) {
            // Greeting the user
            Text(
                text = "Welcome, $username!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                readOnly = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
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
                            chaneloc = location
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate to the EventPage directly
            Button(
                onClick = {
                    // Navigate to EventPage with a hardcoded restaurant name
                    navController.navigate("event_page/KFC")  // You could replace this with a dynamic restaurant selection in the future
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue) // Set a color for the button
            ) {
                Text("Go to Event Page", color = Color.White) // Change text color to white for contrast
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("current_events") // Navigate to current_events directly
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("View Current Events")
            }
        }

        BottomNavBar(navController = navController)
    }
}
*/