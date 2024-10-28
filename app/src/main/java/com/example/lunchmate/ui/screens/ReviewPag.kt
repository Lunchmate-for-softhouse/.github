package com.example.lunchmate.com.example.lunchmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable

import androidx.compose.runtime.remember

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

/*@Composable
fun ReviewPage(onBack: () -> Unit) {
    // Sample data for the review
    val restaurantName = remember { "Example Restaurant" }
    val reviewText = remember { StringBuilder() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Review for $restaurantName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = reviewText.toString(),
            onValueChange = { reviewText.clear(); reviewText.append(it) },
            label = { Text("Write your review") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Handle review submission
            // Implement review submission logic
        }) {
            Text("Submit Review")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to go back
        Button(onClick = { onBack() }) {
            Text("Back")
        }
    }
}*/
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import com.example.lunchmate.R


@Composable
fun ReviewPage(onBack: () -> Unit) {
    val restaurantName = remember { "Example Restaurant" }
    val reviewText = remember { mutableStateOf(TextFieldValue()) }
    var restaurantRating by remember { mutableStateOf(0) }
    var foodRating by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEE9E7))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Review for $restaurantName",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Restaurant Rating Field
        Text(text = "Rate the Restaurant", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = restaurantRating.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECE6F0))
                .padding(vertical = 8.dp),
            trailingIcon = {
                StarRating(rating = restaurantRating, onRatingChanged = { restaurantRating = it })
            },
            label = { Text("Restaurant Rating") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Food Rating Field
        Text(text = "Rate the Food", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = foodRating.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECE6F0))
                .padding(vertical = 8.dp),
            trailingIcon = {
                StarRating(rating = foodRating, onRatingChanged = { foodRating = it })
            },
            label = { Text("Food Rating") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Review Text Field
        TextField(
            value = reviewText.value,
            onValueChange = { reviewText.value = it },
            label = { Text("Write your review") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Handle review submission here
            // By Save the review text and ratings in the database
        }) {
            Text("Submit Review")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to go back
        Button(onClick = { onBack() }) {
            Text("Back")
        }
    }
}

// Composable to show a row of clickable stars for rating
@Composable
fun StarRating(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { starIndex ->
            Icon(
                painter = painterResource(id = if (starIndex <= rating) R.drawable.star_filled else R.drawable.star),
                contentDescription = if (starIndex <= rating) "Filled star" else "Empty star",
                tint = if (starIndex <= rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(50.dp) // Increase star size here
                    .clickable { onRatingChanged(starIndex) }
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
