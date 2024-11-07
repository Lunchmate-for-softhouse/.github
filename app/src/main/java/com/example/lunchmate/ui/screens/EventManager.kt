package com.example.lunchmate.ui.screens

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class EventManager {

    private val db = Firebase.firestore
    // Live data or state to hold the events
    private val _eventsList = mutableStateListOf<Event>()
    val eventsList: List<Event> get() = _eventsList

    fun startListeningForEvents(onNewEvents: (List<Event>) -> Unit) {
        db.collection("events")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle the error
                    return@addSnapshotListener
                }

                val newEvents = mutableListOf<Event>()

                snapshot?.let {
                    for (event in it.documentChanges) {
                        when (event.type) {
                            DocumentChange.Type.ADDED -> {
                                val eventObj = event.document.toObject(Event::class.java)
                                // Check if the event already exists to avoid duplicates
                                if (!_eventsList.contains(eventObj)) {
                                    newEvents.add(eventObj) // Store new events
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                // Handle modified events if needed
                                // For example, you can update the existing event in _eventsList
                            }
                            DocumentChange.Type.REMOVED -> {
                                // Handle removed events if needed
                                // For example, you can remove the event from _eventsList
                            }
                        }
                    }
                }
                if (newEvents.isNotEmpty()) {
                    onNewEvents(newEvents) // Trigger the callback with new events
                }
            }
    }


    suspend fun fetchAndCleanEvents(): List<Event> {
        val eventsList = mutableListOf<Event>()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.parse(dateFormat.format(Date()))

        try {
            val events = db.collection("events").get().await()

            for (event in events) {
                val eventObj = event.toObject(Event::class.java)
                val eventDateStr = eventObj.eventDate

                // Check if eventDateStr is null or not in the correct format
                if (eventDateStr.isNullOrEmpty()) {
                    println("Skipping event with missing date: ${event.id}")
                    continue
                }

                // Parse eventDate string to Date
                val eventDate = try {
                    dateFormat.parse(eventDateStr)
                } catch (e: Exception) {
                    println("Skipping event with invalid date format: ${eventDateStr}")
                    continue
                }

                // Compare dates and delete if the event date is in the past
                if (eventDate != null && currentDate != null && eventDate < currentDate) {
                    println("Deleting past event: ${event.id} with date: $eventDateStr")
                    db.collection("events").document(event.id).delete().await()
                } else {
                    eventsList.add(eventObj) // Add only upcoming events
                }
            }
        } catch (e: Exception) {
            throw Exception("Error fetching events: ${e.message}")
        }

        return eventsList
    }



    fun deleteEvent(creatorName: String, eventToDelete:String, onDeleteComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("events")
            .whereEqualTo("eventName", eventToDelete)
            .whereEqualTo("createdBy", creatorName)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Loop through all matching documents and delete them
                    for (document in querySnapshot.documents) {
                        db.collection("events")
                            .document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                println("Event deleted successfully")
                                onDeleteComplete(true) // Notify that delete was successful
                            }
                            .addOnFailureListener { e ->
                                println("Error deleting event: ${e.message}")
                                onDeleteComplete(false) // Notify that delete failed
                            }
                    }
                } else {
                    println("No matching event found to delete")
                    onDeleteComplete(false) // No event found to delete
                }
            }
            .addOnFailureListener { e ->
                println("Error finding event: ${e.message}")
                onDeleteComplete(false) // Notify that delete failed
            }
    }
    fun updateEvent(event: Event, callback: (Boolean) -> Unit) {
        // Query to find the event with the specified eventName and createdBy
        db.collection("events")
            .whereEqualTo("eventName", event.eventName)
            .whereEqualTo("createdBy", event.createdBy)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val eventRef = db.collection("events").document(document.id)

                    // Create a map of the fields to update
                    val updatedFields = mapOf(
                        "eventDate" to event.eventDate,
                        "eventTime" to event.eventTime,
                        "pickupDineIn" to event.pickupDineIn,
                        "estimatedArrivalTime" to event.estimatedArrivalTime,
                        "etaStart" to event.etaStart,
                        "isEventEnded" to event.isEventEnded
                    )

                    // Update the fields in Firestore
                    eventRef.update(updatedFields)
                        .addOnSuccessListener {
                            callback(true) // Update was successful
                        }
                        .addOnFailureListener { e ->
                            callback(false) // Update failed
                        }
                } else {
                    callback(false) // No matching event found
                }
            }
            .addOnFailureListener { e ->
                callback(false) // Error occurred while querying
            }
    }
}

