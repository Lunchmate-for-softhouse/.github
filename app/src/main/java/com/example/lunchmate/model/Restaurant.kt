package com.example.lunchmate.model

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(
    val name: String,
    val address: String,
    val rating: Float?, // Rating of the restaurant
    val userRatingsTotal: Int?, // Total user ratings
    val openingHours: List<String>?, // List of opening hours (you might want to define a custom data structure for this)
    val priceLevel: String?,
    val photoReference: String?,
    val types: List<String>,
    val website: String? // Website link
)