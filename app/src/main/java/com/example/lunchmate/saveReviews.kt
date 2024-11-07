import com.google.firebase.database.FirebaseDatabase

fun saveReviews(
    restaurantText: String,
    reviewText: String,
    foodRating: Int,
    serviceRating: Int,
    selectedLocation: String,
    //imageUri: Uri?
) {
    // Call the function to check and increment review count

    fun checkAndIncrementReviewCount(
        restaurantName: String,
        location: String,
        reviewText: String,
        foodRating: Int,
        serviceRating: Int,
        //imageUri: Uri?
    ) {
        val db = FirebaseDatabase.getInstance().reference
        val reviewKey = "${restaurantName}_$location" // Unique key based on restaurant and location

        // Fetch the review data for the restaurant and location
        db.child("reviews").child(reviewKey).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // If the review already exists, increment the count
                val currentCount = snapshot.child("reviewCount").getValue(Int::class.java) ?: 0
                db.child("reviews").child(reviewKey).child("reviewCount").setValue(currentCount + 1)

                // Add the new review
                val newReview = mapOf(
                    "reviewText" to reviewText,
                    "foodRating" to foodRating,
                    "serviceRating" to serviceRating,
                    //"imageUri" to imageUri.toString(),
                    "timestamp" to System.currentTimeMillis()
                )
                db.child("reviews").child(reviewKey).child("reviews").push().setValue(newReview)
            } else {
                // If no reviews exist, create a new entry for the restaurant and location
                val newReviewData = mapOf(
                    "restaurantName" to restaurantName,
                    "location" to location,
                    "reviewCount" to 1,
                    "reviews" to listOf(
                        mapOf(
                            "reviewText" to reviewText,
                            "foodRating" to foodRating,
                            "serviceRating" to serviceRating,
                            //"imageUri" to imageUri.toString(),
                            "timestamp" to System.currentTimeMillis()
                        )
                    )
                )
                db.child("reviews").child(reviewKey).setValue(newReviewData)
            }
        }
    }

    checkAndIncrementReviewCount(
        restaurantText,
        selectedLocation,
        reviewText,
        foodRating,
        serviceRating,
        //imageUri
    )
}
