package com.example.lunchmate.ui.screens

import BottomNavBar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reviews(navController: NavController, userName: String) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("Stockholm") }
    var selectedRestaurant by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var restaurants by remember { mutableStateOf<List<String>>(emptyList()) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }

    val db = Firebase.firestore
    val snackbarHostState = remember { SnackbarHostState() }

    val locations = listOf(
        "Stockholm", "Malmö", "Växjö", "Karlskrona", "Karlshamn",
        "Kalmar", "Jönköping", "Luleå", "Uppsala", "Sarajevo"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNavBar(navController = navController) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAF9F6)) // Light background color
                    .padding(16.dp)
            ) {
                // Header with the review icon
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restaurant Reviews", style = MaterialTheme.typography.headlineMedium)
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(imageVector = Icons.Default.AddTask, contentDescription = "Add Review")
                    }
                }

                // Location dropdown menu
                Text(text = "Choose location for Restaurant", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = selectedLocation, style = MaterialTheme.typography.bodyMedium)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    locations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                selectedLocation = loc
                                expanded = false
                                searchText = TextFieldValue("") // Reset search text
                                selectedRestaurant = "" // Reset selected restaurant
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Restaurant search bar and suggestions
                TextField(
                    value = searchText, // Clear suggestions on click
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search Restaurant") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
                )

                // Search Button
                Button(
                    onClick = {
                        if (restaurants.isEmpty()) {
                            // If no restaurants match, search for reviews
                            fetchReviews(searchText.text, selectedLocation) { fetchedReviews ->
                                reviews = fetchedReviews
                            }
                        } else {
                            // If there are restaurant suggestions, fetch the restaurant details
                            fetchRestaurants(searchText.text, selectedLocation) { fetchedRestaurants ->
                                restaurants = fetchedRestaurants.distinct() // Ensure distinct restaurants
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Purple button
                ) {
                    Text("Search", color = Color.White)
                }

                // Restaurant suggestions
                if (restaurants.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(restaurants) { restaurant ->
                            Text(
                                text = restaurant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedRestaurant = restaurant.split(":").first() // Extract restaurant name
                                        fetchReviews(selectedRestaurant, selectedLocation) { fetchedReviews ->
                                            reviews = fetchedReviews
                                        }
                                    }
                                    .padding(8.dp)
                                    .background(Color(0xFFF1F1F1)) // Light background for suggestions
                                    .padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display reviews for the selected restaurant or from the search
                if (selectedRestaurant.isNotEmpty() || reviews.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(reviews) { review ->
                            ReviewCard(review)
                        }
                    }
                }
            }
        }

    )
    LaunchedEffect(isEditing) {
        if (isEditing) {
            navController.navigate("submit_review")
            isEditing = false // Reset after navigating to avoid unnecessary recompositions
        }
    }

}

fun fetchRestaurants(searchname: String, location: String, onResult: (List<String>) -> Unit) {
    val db = Firebase.firestore
    if (searchname.isNotEmpty() && location.isNotEmpty()) {
        db.collection("restaurants")
            .whereEqualTo("restaurantLocation", location) // Filtering by location
            .whereEqualTo("restaurantName", searchname) // Matching restaurant names
            .get()
            .addOnSuccessListener { querySnapshot ->
                val matchedRestaurants = querySnapshot.documents.mapNotNull {
                    it.getString("combinedNameLocation")
                }
                onResult(matchedRestaurants)
            }
    }
}

fun fetchReviews(searchText: String, location: String, onResult: (List<Review>) -> Unit) {
    val db = Firebase.firestore
    if (searchText.isNotEmpty() && location.isNotEmpty()) {
        // Convert the search text to lowercase
        val lowerSearchText = searchText.lowercase()

        // Query the database for reviews, converting the relevant fields to lowercase in the comparison
        db.collection("reviews")
            .whereEqualTo("location", location)
            .get() // Fetch all reviews for the location
            .addOnSuccessListener { querySnapshot ->
                val fetchedReviews = querySnapshot.documents.mapNotNull { document ->
                    val restaurant = document.getString("restaurant")?.lowercase() // Convert to lowercase
                    if (restaurant == lowerSearchText) {
                        document.toObject(Review::class.java)
                    } else {
                        null
                    }
                }
                onResult(fetchedReviews)
            }
    }
}

@Composable
fun ReviewCard(review: Review) {
    // Card with elegant shadow and rounded corners
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),  // Global padding for the card
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))  // Light border for the card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Name
            Text(
                text = "User: ${review.userName}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Review Text
            Text(
                text = review.reviewText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Rating Section: Display food and service ratings
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Food Rating: ${review.foodRating}/5",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.weight(1f))  // Pushes service rating to the right
                Text(
                    text = "Service Rating: ${review.serviceRating}/5",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }

            // Image Section: If an image is present, show it with a nice border and rounded corners
            review.image?.takeIf { it != "null" }?.let { imageUri ->
                Spacer(modifier = Modifier.height(12.dp))  // Space before the image
                Image(
                    painter = rememberImagePainter(data = imageUri),
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.medium)  // Rounded corners for the image
                        .shadow(8.dp, shape = MaterialTheme.shapes.medium)  // Shadow for a subtle 3D effect
                )
            }

            // Add a small separator line for better visual separation (optional)
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        }
    }

}

// Data model for Review
data class Review(
    val userName: String = "",
    val reviewText: String = "",
    val foodRating: Int = 0,
    val serviceRating: Int = 0,
    val image: String? = null // Use String to store image URL if any
)