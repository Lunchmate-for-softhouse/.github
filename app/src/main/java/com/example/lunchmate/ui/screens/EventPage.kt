package com.example.lunchmate.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Order data class
data class Order(
    var mealName: String = "",
    var mealPrice: Double = 0.0,
    var drinkName: String = "",
    var drinkPrice: Double = 0.0,
    var eventName: String = "",
    var location: String = "",
    var creator: String = ""
)

// Event order screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPage(navController: NavController, eventName: String, location: String, creatorName: String) {
    var mealName by remember { mutableStateOf("") }
    var mealPrice by remember { mutableStateOf("") }
    var drinkName by remember { mutableStateOf("") }
    var drinkPrice by remember { mutableStateOf("")  }
    var orders by remember { mutableStateOf(listOf<Order>()) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header Row with back navigation and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6C4E90)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$eventName - $location",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields for Meals
        TextField(
            value = mealName,
            onValueChange = { mealName = it },
            label = { Text("Meal Name") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = mealPrice,
            onValueChange = { mealPrice = it },
            label = { Text("Meal Price") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields for Drinks
        TextField(
            value = drinkName,
            onValueChange = { drinkName = it },
            label = { Text("Drink Name") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = drinkPrice,
            onValueChange = { drinkPrice = it },
            label = { Text("Drink Price") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val priceMeal = mealPrice.toDoubleOrNull()
                val priceDrink = drinkPrice.toDoubleOrNull()
                if (mealName.isNotEmpty() && priceMeal != null || drinkName.isNotEmpty() && priceDrink != null) {
                    val order = Order(
                        mealName = mealName,
                        mealPrice = priceMeal ?: 0.0,
                        drinkName = drinkName,
                        drinkPrice = priceDrink ?: 0.0,
                        eventName = eventName,
                        location = location,
                        creator = creatorName
                    )
                    orders = orders + order
                    mealName = ""
                    mealPrice = ""
                    drinkName = ""
                    drinkPrice = ""
                } else {
                    showErrorDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4E90)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Add Order", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Order Summary",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF333333)
        )

        LazyColumn {
            items(orders) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .shadow(4.dp, shape = MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            if (order.mealName.isNotEmpty()) {
                                Text("Meal: ${order.mealName}", fontWeight = FontWeight.Bold)
                                Text("Price: ${order.mealPrice} SEK", color = Color.Gray)
                            }
                            if (order.drinkName.isNotEmpty()) {
                                Text("Drink: ${order.drinkName}", fontWeight = FontWeight.Bold)
                                Text("Price: ${order.drinkPrice} SEK", color = Color.Gray)
                            }
                        }
                        IconButton(
                            onClick = { orders = orders - order },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Payment and Confirm Order buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {navController.navigate("swish_screen") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Swish", color = Color.White)
            }

            Button(
                onClick = {
                    if (orders.isEmpty()) {
                        showErrorDialog = true
                    } else {
                        saveOrdersToDatabase(db, orders, eventName, location) {
                            navController.navigate("current_events")
                        }
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C4E90)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm Order", color = Color.White)
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Ok", color = Color(0xFF6C4E90))
                    }
                },
                title = {
                    Text("Error", style = MaterialTheme.typography.titleMedium.copy(color = Color.Red))
                },
                text = { Text("Please ensure all fields are filled correctly.") }
            )
        }
    }
}

// Function to save orders to Firestore and increment 'people' count for the event
fun saveOrdersToDatabase(
    db: FirebaseFirestore,
    orders: List<Order>,
    eventName: String,
    location: String,
    onComplete: () -> Unit
) {
    val batch = db.batch()
    val ordersCollectionRef = db.collection("Orders")
    val eventsCollectionRef = db.collection("events")  // Replace with your actual events collection name

    // Save each order in the Orders collection
    for (order in orders) {
        val newOrderRef = ordersCollectionRef.document()
        batch.set(newOrderRef, order)
    }

    // Query to find the specific event document matching eventName and location
    eventsCollectionRef
        .whereEqualTo("eventName", eventName)
        .whereEqualTo("location", location)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Assume there is only one document matching the eventName and location
                val eventDocRef = querySnapshot.documents[0].reference
                // Increment 'people' count by 1
                batch.update(eventDocRef, "people", FieldValue.increment(1))

                // Commit the batch with both the new orders and people count increment
                batch.commit().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("Firestore", "Orders and people count successfully updated!")
                        onComplete()
                    } else {
                        Log.w("Firestore", "Error updating orders or people count", it.exception)
                    }
                }
            } else {
                Log.w("Firestore", "No matching event found for incrementing people count.")
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error finding event document", e)
        }
}
