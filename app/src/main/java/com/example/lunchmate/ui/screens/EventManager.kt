package com.example.lunchmate.ui.screens

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class EventManager {

    private val db = Firebase.firestore

    suspend fun fetchAndCleanEvents(): List<Event> {
        val eventsList = mutableListOf<Event>()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        try {
            val events = db.collection("events").get().await()

            for (event in events) {
                val eventObj = event.toObject(Event::class.java)
                val eventDate = eventObj.eventDate // Assuming this is in "dd/MM/yyyy" format

                // Compare only the event date
                if (eventDate < currentDate) {
                    // Delete the event from Firestore
                    db.collection("events").document(event.id).delete().await()
                } else {
                    eventsList.add(eventObj) // Add only upcoming events
                }
            }
        } catch (e: Exception) {
            // Handle exceptions accordingly (logging or throwing)
            throw Exception("Error fetching events: ${e.message}")
        }

        return eventsList
    }
}
