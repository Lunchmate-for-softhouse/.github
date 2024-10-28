/*package com.example.lunchmate.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reviews(navController: NavController) {
    var rating by remember { mutableStateOf(0f) }
    var reviewText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) } // Loading state

    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(Unit) {
        // Fetch reviews from Firestore when the screen is loaded
        firestore.collection("Reviews").get().addOnSuccessListener { snapshot ->
            reviews = snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Review Your Food", fontWeight = FontWeight.Bold, color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Food Image Display
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Food Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } ?: PlaceholderImage()

        Spacer(modifier = Modifier.height(16.dp))

        // Rating Section
        RatingSection(rating) { newRating -> rating = newRating }

        Spacer(modifier = Modifier.height(16.dp))

        // Review Text Input
        ReviewInputField(reviewText) { newText -> reviewText = newText }

        Spacer(modifier = Modifier.height(16.dp))

        // Image Upload Button
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Upload Image", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Review Button
        Button(
            onClick = {
                if (rating > 0f) {
                    isLoading = true // Start loading indicator
                    uploadImageAndSaveReview(storage, imageUri, rating, reviewText, navController) {
                        isLoading = false // Stop loading indicator
                    }
                } else {
                    // Show a warning if no rating is provided
                    println("Please provide a rating before submitting.")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
        ) {
            Text("Submit Review", color = Color.Black)
        }

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFFFFA500))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Reviews
        Text("Reviews", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFFFA500))

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(reviews) { review ->
                ReviewItem(review)
            }
        }
    }
}

@Composable
fun PlaceholderImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text("Select Image", color = Color.White)
    }
}

@Composable
fun RatingSection(rating: Float, onRatingChange: (Float) -> Unit) {
    Text("Rating (1-5)", fontWeight = FontWeight.Bold, color = Color.White)
    Row {
        (1..5).forEach { index ->
            Icon(
                imageVector = if (index <= rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "$index star",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChange(index.toFloat()) }
                    .padding(4.dp),
                tint = Color.Yellow
            )
        }
    }
}

@Composable
fun ReviewInputField(reviewText: String, onTextChange: (String) -> Unit) {
    Text("Review", fontWeight = FontWeight.Bold, color = Color.White)
    BasicTextField(
        value = reviewText,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(8.dp),
        textStyle = LocalTextStyle.current.copy(color = Color.White)
    )
}

@Composable
fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.DarkGray)
            .padding(16.dp)
    ) {
        Text("Rating: ${review.rating}", fontWeight = FontWeight.Bold, color = Color(0xFFFFA500))
        Text("Review: ${review.reviewText}", color = Color.White)
        review.imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Review Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun uploadImageAndSaveReview(
    storage: FirebaseStorage, imageUri: Uri?, rating: Float, reviewText: String,
    navController: NavController, onUploadComplete: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    fun saveReviewToFirestore(imageUrl: String?) {
        val review = hashMapOf(
            "rating" to rating,
            "reviewText" to reviewText,
            "imageUrl" to imageUrl
        )

        firestore.collection("Reviews")
            .add(review)
            .addOnSuccessListener {
                onUploadComplete() // Stop loading indicator
                navController.popBackStack() // Navigate back
            }
            .addOnFailureListener {
                onUploadComplete() // Stop loading indicator
                // Handle failure (e.g., show a toast or log error)
            }
    }

    if (imageUri != null) {
        val imageRef = storage.reference.child("reviews/${UUID.randomUUID()}.jpg")
        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveReviewToFirestore(uri.toString())
            }
        }.addOnFailureListener {
            onUploadComplete() // Stop loading indicator
            // Handle failure (e.g., show a toast or log error)
        }
    } else {
        saveReviewToFirestore(null)
    }
}

data class Review(
    val rating: Float = 0f,
    val reviewText: String = "",
    val imageUrl: String? = null
)

 */