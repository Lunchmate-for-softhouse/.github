package com.example.lunchmate
import com.google.firebase.firestore.FirebaseFirestore


fun saveEvent(
        eventName: String,
        eventDate: String,
        eventTime: String, // Add this line
        eventDescription: String,
        createdBy: String,
        pickupDineIn: String,
        location: String
) {
        val db = FirebaseFirestore.getInstance()

        // Create a new event map
        val event = hashMapOf(
            "eventName" to eventName,
            "eventDate" to eventDate,
            "eventTime" to eventTime,
            "eventDescription" to eventDescription,
            "createdBy" to createdBy,
            "pickupDineIn" to pickupDineIn,
            "location" to location
        )

        // Add event to Firestore
        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                println("Event successfully added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding event: $e")
            }
    }
