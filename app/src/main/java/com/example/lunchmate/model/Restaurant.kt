package com.example.lunchmate.model

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(
    val name: String,
    val address: String,
    val rating: Float?, // Rating of the restaurant
    val userRatingsTotal: Int?, // Total user ratings
    val openingHours: List<String>?, // List of opening hours (you might want to define a custom data structure for this)
    val priceLevel: Int?, // Price level (1-4)
    val photoReference: String?, // Photo reference for images
    val types: List<String>, // List of types (e.g., Italian, Fast Food)
    val website: String? // Website link
)