package com.example.lunchmate.com.example.lunchmate.ui.screens

import BottomNavBar
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lunchmate.saveReviews
import com.example.lunchmate.ui.screens.TextFieldWithLabel
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReview(navController: NavController, userName: String) {
    var expanded by remember { mutableStateOf(false) }
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

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val locations = listOf(
        "Stockholm", "Malmö", "Växjö", "Karlskrona", "Karlshamn",
        "Kalmar", "Jönköping", "Luleå", "Uppsala", "Sarajevo"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNavBar(navController = navController) },
        topBar = {
            // Back Button
            androidx.compose.material3.TopAppBar(
                title = { Text("Go Back") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },

        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFCE4EC))
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()), // Make it scrollable
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Leave a Review",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    // Location dropdown menu
                    Text(text = "Choose location for Restaurant")
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedLocation)
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Selected Location: $selectedLocation")
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
                                    }
                                )
                            }
                        }
                    }

                    // Restaurant text field
                    Text("Restaurant name")
                    TextFieldWithLabel(
                        value = restaurantText,
                        onValueChange = { restaurantText = it },
                        placeholder = "Type in restaurant name"
                    )

                    // Review text field
                    Text("Review")
                    TextFieldWithLabel(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = "Write a review"
                    )

                    // Image upload box
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .border(
                                2.dp,
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { launcher.launch("image/*") }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Uploaded Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
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

                    // Rating selection
                    Text(text = "Food rating")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                        verticalAlignment = Alignment.CenterVertically
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
                                saveReviews(
                                    userName = userName,
                                    restaurantText = restaurantText,
                                    reviewText = reviewText,
                                    foodRating = foodRating,
                                    selectedLocation = selectedLocation,
                                    serviceRating = serviceRating,
                                    imageUri = imageUri.toString()
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Review saved!")
                                }
                                navController.navigate("reviews")
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please fill all fields!")
                                }
                            }
                        }
                    ) {
                        Text(text = "Submit Review")
                    }
                }
            }
        }
    )
}