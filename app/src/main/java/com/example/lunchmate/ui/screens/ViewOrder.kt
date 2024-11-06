package com.example.lunchmate.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lunchmate.com.example.lunchmate.model.Order
import com.example.lunchmate.R

// ViewOrder screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrder(eventName: String, location: String) {
    var orders by remember { mutableStateOf(listOf<Order>()) }
    val db = FirebaseFirestore.getInstance()

    // Log the parameters to check if they have values
    Log.d("Firestore", "Fetching orders for Event: $eventName, Location: $location")

    // Fetch orders from Firestore based on event name and location
    LaunchedEffect(Unit) {
        db.collection("Orders")
            .whereEqualTo("eventName", eventName)
            .whereEqualTo("location", location)
            .get()
            .addOnSuccessListener { result ->
                val fetchedOrders = result.mapNotNull { document ->
                    document.toObject(Order::class.java) // Mapping Firestore document to Order object
                }
                orders = fetchedOrders

                // Log the fetched orders and the number of orders fetched
                Log.d("Firestore", "Fetched orders: $fetchedOrders")
                Log.d("Firestore", "Fetched orders count: ${fetchedOrders.size}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching orders", e)
            }
    }

    // Main UI layout
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Orders for the $eventName - $location") })
        },
        content = { padding ->
            if (orders.isEmpty()) {
                // Display a message if no orders were fetched
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "No orders available for this event.", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(orders) { order ->
                        OrderItem(order = order)
                    }
                }
            }
        }
    )
}

@Composable
fun OrderItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Creator Name with Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person, // Material Icon for person
                    contentDescription = "Creator Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Creator: ${order.creator}", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drink Icon and Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocalBar, // Material Icon for drink
                    contentDescription = "Drink Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Drink: ${order.drinkName}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Meal Icon and Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Restaurant, // Material Icon for meal
                    contentDescription = "Meal Icon",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Meal: ${order.mealName}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total Price with Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Money, // Material Icon for price
                    contentDescription = "Price Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Total Price: ${order.totalPrice} SEK",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        }
    }
}