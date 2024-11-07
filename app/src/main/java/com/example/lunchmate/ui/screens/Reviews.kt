package com.example.lunchmate.ui.screens

import BottomNavBar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Colors
import coil.compose.rememberAsyncImagePainter
import com.example.lunchmate.Restaurant
import com.example.lunchmate.saveReviews
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reviews(navController: NavController, userName:String) {

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isEditing by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(true) } // State for showing/hiding search bar
    var selectedLocation by remember { mutableStateOf("Stockholm") }
    var restaurantText by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var foodRating by remember { mutableIntStateOf(0) }
    var serviceRating by remember { mutableIntStateOf(0) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    // Snackbar host state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    val locations = listOf(
        "Stockholm", "Malmö", "Växjö", "Karlskrona", "Karlshamn",
        "Kalmar", "Jönköping", "Luleå", "Uppsala", "Sarajevo"
    )
    var expanded by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFCE4EC)) // Light pink background color
                    .padding(16.dp)
            ) {

                // Search Bar with Edit Button
                if (showSearchBar) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(24.dp))
                            .background(
                                Color(0xFFF1E6FF),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search Restaurant", color = Color.Black) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color.Black,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                isEditing = true  // Show the review submission form
                                showSearchBar = false
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Color(0xFFFCE4EC),
                                    RoundedCornerShape(18.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Write Review",
                                tint = Color.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))


                // Show the review form when editing mode is enabled
                if (isEditing) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp), // Add padding and width constraints if needed
                        horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
                    ) {
                        Text(
                            text = "Leave a Review",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.CenterHorizontally) // Center the text horizontally
                        )
                        Spacer(modifier = Modifier.height(22.dp))

                        // Location dropdown menu


                        Text(text = "Choose location for Restaurant")

                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = selectedLocation)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            locations.forEach { location ->
                                DropdownMenuItem(
                                    text = { Text(location) },
                                    onClick = {
                                        selectedLocation = location
                                        expanded = false
                                    }
                                )
                            }
                        }
                        //Restaurant text field
                        Text("Restaurant name")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextFieldWithLabel(
                            value = restaurantText,
                            onValueChange = { restaurantText = it },
                            placeholder = "Type in restaurant name"
                        )

                        Spacer(modifier = Modifier.height(22.dp))


                        // Review text field
                        Text("Review")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextFieldWithLabel(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = "Write a review"
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(16.dp)) // Rounded corners
                                .background(Color.LightGray.copy(alpha = 0.3f)) // Light gray background with slight transparency
                                .border(
                                    2.dp,
                                    Color.Black.copy(alpha = 0.5f),
                                    RoundedCornerShape(16.dp)
                                ) // Subtle border
                                .clickable { launcher.launch("image/*") } // Clickable for image upload
                                .padding(8.dp), // Adjust padding
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri),
                                    contentDescription = "Uploaded Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp)) // Rounded corners on the image as well
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto, // Camera icon for clarity
                                        contentDescription = "Upload Image",
                                        tint = Color.Black.copy(alpha = 0.8f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Upload Image",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating selection
                        Text(text = "Food rating")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            repeat(5) { index ->
                                IconButton(
                                    onClick = { foodRating = index + 1 }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (index < foodRating) Color(0xFFFFD700) else Color.Black
                                    )
                                }
                            }
                        }

                        Text(text = "Service rating")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            repeat(5) { index ->
                                IconButton(
                                    onClick = { serviceRating = index + 1 }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (index < serviceRating) Color(0xFFFFD700) else Color.Black
                                    )
                                }
                            }
                        }

                        // Submit button
                        Button(
                            onClick = {
                                if (restaurantText.isNotBlank() && reviewText.isNotBlank() && selectedLocation.isNotBlank()) {
                                    println("Restaurant: $restaurantText")
                                    println("Resviews: $reviewText")
                                    println("Location: $selectedLocation")
                                    saveReviews(
                                        userName = userName,
                                        restaurantText = restaurantText,
                                        reviewText = reviewText,
                                        foodRating = foodRating,
                                        serviceRating = serviceRating,
                                        selectedLocation = selectedLocation,
                                        imageUri = imageUri.toString(),
                                    )
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Review created successfully")
                                        navController.navigate("reviews")
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
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    )
}