package com.example.lunchmate

import com.google.firebase.firestore.FirebaseFirestore

fun saveReviews(
    userName: String,
    restaurantText: String,
    reviewText: String,
    foodRating: Int,
    serviceRating: Int,
    selectedLocation: String,
    imageUri: String?
) {
    val db = FirebaseFirestore.getInstance()
    val combinedNameLocation = "$restaurantText:$selectedLocation"

    println("Saving review for restaurant: $restaurantText")

    val review = hashMapOf(
        "userName" to userName,
        "restaurant" to restaurantText,
        "reviewText" to reviewText,
        "foodRating" to foodRating,
        "serviceRating" to serviceRating,
        "location" to selectedLocation,
        "image" to imageUri
    )

    db.collection("reviews")
        .add(review)
        .addOnSuccessListener { documentReference ->
            println("Review added with ID: ${documentReference.id}")

            db.collection("restaurants")
                .whereEqualTo("combinedNameLocation", combinedNameLocation)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val restaurantDoc = documents.first()
                        val restaurantId = restaurantDoc.id
                        val currentTotalReviews = restaurantDoc.getLong("totalReviews") ?: 0

                        db.collection("restaurants")
                            .document(restaurantId)
                            .update("totalReviews", currentTotalReviews + 1)
                            .addOnSuccessListener {
                                println("Total reviews incremented for restaurant: $combinedNameLocation")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating total reviews: $e")
                            }
                    } else {
                        // Restaurant not found, create a new entry
                        addNewRestaurant(db, restaurantText, selectedLocation)
                    }
                }
                .addOnFailureListener { e ->
                    println("Error checking for restaurant in the database: $e")
                }
        }
        .addOnFailureListener { e ->
            println("Error adding review: $e")
        }
}

// Function to add a new restaurant entry to Firestore
fun addNewRestaurant(db: FirebaseFirestore, restaurantName: String, restaurantLocation: String) {
    val combinedNameLocation = "$restaurantName:$restaurantLocation"
    val totalReviews = 1

    val restaurantData = hashMapOf(
        "restaurantName" to restaurantName,
        "restaurantLocation" to restaurantLocation,
        "combinedNameLocation" to combinedNameLocation,
        "totalReviews" to totalReviews
    )

    db.collection("restaurants")
        .add(restaurantData)
        .addOnSuccessListener { documentReference ->
            println("Restaurant added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding restaurant: $e")
        }
}
