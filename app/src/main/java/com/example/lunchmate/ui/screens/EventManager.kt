package com.example.lunchmate.ui.screens

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.firestore.DocumentChange
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

    fun deleteEvent(event: Event) {

    }
}