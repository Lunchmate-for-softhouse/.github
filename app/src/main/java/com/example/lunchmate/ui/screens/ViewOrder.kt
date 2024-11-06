package com.example.lunchmate.ui.screens
/*
import com.example.lunchmate.ui.screens.EventPage
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.navigation.NavController



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrder(navController: NavController, nameOfEvent: String, orderLoc: String, orderOwner: String) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val db = FirebaseFirestore.getInstance()

    // Fetch orders from Firestore when the screen is first loaded
    LaunchedEffect(Unit) {
        fetchOrders(db, nameOfEvent, orderLoc) { fetchedOrders ->
            orders = fetchedOrders
            isLoading = false
        }
    }

    // Main UI layout for the Orders Screen
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Orders") },
            )
        },
        content = { padding ->
            // Content of the screen
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (isLoading) {
                    // Show a loading spinner while fetching data
                    CircularProgressIndicator(modifier = Modifier.align(LineHeightStyle.Alignment.Center))
                } else {
                    if (orders.isNotEmpty()) {
                        // Display orders in a list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        ) {
                            items(orders) { order ->
                                OrderCard(order)
                            }
                        }
                    } else {
                        // Display a message if no orders are found
                        Text(
                            text = "No orders found for this restaurant and location.",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    )
}

// Function to fetch orders from Firestore
fun fetchOrders(db: FirebaseFirestore, eventName: String, location: String, callback: (List<Order>) -> Unit) {
    db.collection("Orders")
        .whereEqualTo("eventName", eventName)
        .whereEqualTo("location", location)
        .get()
        .addOnSuccessListener { result ->
            val ordersList = result.documents.mapNotNull { document ->
                val mealName = document.getString("mealName") ?: ""
                val mealPrice = document.getDouble("mealPrice") ?: 0.0
                val drinkName = document.getString("drinkName") ?: ""
                val drinkPrice = document.getDouble("drinkPrice") ?: 0.0
                val eventName = document.getString("eventName") ?: ""
                val location = document.getString("location") ?: ""
                val creator = document.getString("creator") ?: ""

                Order(mealName, mealPrice, drinkName, drinkPrice, eventName, location, creator)
            }
            callback(ordersList)
        }
        .addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents: ", exception)
            callback(emptyList())  // Return empty list on failure
        }
}

// Composable to display individual order in a card
@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Meal: ${order.mealName}", style = MaterialTheme.typography.bodyMedium)
                if (order.mealPrice > 0) {
                    Text("Price: ${order.mealPrice} SEK", style = MaterialTheme.typography.bodySmall)
                }
                if (order.drinkName.isNotEmpty()) {
                    Text("Drink: ${order.drinkName}", style = MaterialTheme.typography.bodyMedium)
                    Text("Price: ${order.drinkPrice} SEK", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewViewOrder() {
    ViewOrder(navController = NavController(LocalContext.current), nameOfEvent = "Test Event", orderLoc = "Test Location", orderOwner = "Owner")
}
*/