package com.example.lunchmate.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lunchmate.MainActivity
import com.example.lunchmate.com.example.lunchmate.model.Order




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventPage(navController: NavController, restaurantName: String, creatorName:String) {
    val context = LocalContext.current // Get the current context
    var inputMeal by remember { mutableStateOf("") }       // For meal name
    var inputDrink by remember { mutableStateOf("") }      // For drink name
    var inputMealPrice by remember { mutableStateOf("") }  // For meal price
    var inputDrinkPrice by remember { mutableStateOf("") } // For drink price
    var showDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf(listOf<Order>()) }
    var totalPrice by remember { mutableStateOf(0.0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEE9E7))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with restaurant name and back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECE6F0))
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = restaurantName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu and Reviews buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Handle Menu button click */ },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Menu")
            }

            Button(
                onClick = { /* Handle Reviews button click */ },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Reviews")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Your selected orders section
        Text(
            text = "Your Selected Orders",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color.Black
        )

        // Row for inputting meal name and price
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputMeal,
                onValueChange = { inputMeal = it },
                placeholder = { Text("Meal name") },
                modifier = Modifier.weight(2f).height(60.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFECE6F0))
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = inputMealPrice,
                onValueChange = {
                    inputMealPrice = it.filter { char -> char.isDigit() || char == '.' }
                },
                placeholder = { Text("Meal Price") },
                modifier = Modifier.weight(1f).height(60.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFECE6F0)),
                trailingIcon = { Text("kr", color = Color.Gray) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row for inputting drink name and price
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputDrink,
                onValueChange = { inputDrink = it },
                placeholder = { Text("Drink name") },
                modifier = Modifier.weight(2f).height(60.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFECE6F0))
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = inputDrinkPrice,
                onValueChange = {
                    inputDrinkPrice = it.filter { char -> char.isDigit() || char == '.' }
                },
                placeholder = { Text("Drink Price") },
                modifier = Modifier.weight(1f).height(60.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xFFECE6F0)),
                trailingIcon = { Text("kr", color = Color.Gray) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add button with "+" symbol
        Button(
            onClick = {
                if (inputMeal.isNotBlank() && inputDrink.isNotBlank() && inputMealPrice.isNotBlank() && inputDrinkPrice.isNotBlank()) {
                    val mealPriceValue = inputMealPrice.toDoubleOrNull() ?: 0.0
                    val drinkPriceValue = inputDrinkPrice.toDoubleOrNull() ?: 0.0
                    val order = Order(
                        name = "${creatorName}",
                        mealName = inputMeal,
                        drink = inputDrink,
                        mealPrice = mealPriceValue,
                        drinkPrice = drinkPriceValue
                    )

                    orders = orders + order
                    inputMeal = ""
                    inputDrink = ""
                    inputMealPrice = ""
                    inputDrinkPrice = ""
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(64.dp)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65558F))
        ) {
            Text("+", color = Color.White, fontSize = 25.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn to display added orders
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onRemoveClick = {
                        orders = orders.filter { it != order }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Row for Confirm and Pay by Swish buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65558F))
            ) {
                Text("Pay by Swish")
            }

            Button(
                onClick = {
                    if (orders.isEmpty()) {
                        showErrorDialog = true
                    } else {
                        totalPrice = orders.sumOf { order -> order.totalPrice }
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF65558F))
            ) {
                Text("Confirm Order")
            }
        }

        // Confirmation Dialog
        if (showDialog) {
            OrderConfirmationDialog(
                totalPrice = totalPrice,
                onDismiss = { showDialog = false },
                onConfirm = {
                    (context as? MainActivity)?.confirmOrder()
                    showDialog = false
                }
            )
        }

        // Error Dialog for no selected orders
        if (showErrorDialog) {
            ErrorDialog(
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}

// OrderCard composable function
@Composable
fun OrderCard(order: Order, onRemoveClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${order.name} + ${order.drink}: ${order.totalPrice} kr", style = MaterialTheme.typography.bodyLarge)

            IconButton(onClick = { onRemoveClick() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove order",
                    tint = Color.Red
                )
            }
        }
    }
}

// OrderConfirmationDialog composable function
@Composable
fun OrderConfirmationDialog(
    totalPrice: Double,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(text = "Confirm Order") },
        text = { Text("Total price is: $totalPrice kr. Do you want to confirm the order?") }
    )
}

// ErrorDialog composable function
@Composable
fun ErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = { Text(text = "Error") },
        text = { Text("No orders selected. Please select an order before proceeding.") }
    )
}


