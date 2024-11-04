package com.example.lunchmate.ui.screens

import android.R
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lunchmate.model.Message
import com.google.firebase.database.*

@Composable
fun ChatScreen(groupId: String, userId: String) {
    var messageInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val database = FirebaseDatabase.getInstance()
    val messagesRef = database.getReference("chats").child(groupId)

    // Load messages in real-time
    LaunchedEffect(Unit) {
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messages = messages + it
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatScreen", "Database error: ${error.message}")
            }
        })
    }

    // UI for chat messages and input
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display messages
        Column(modifier = Modifier.weight(1f)) {
            messages.forEach { message ->
                Text(text = "${message.senderId}: ${message.text}", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Input field and send button
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Type your message") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (messageInput.isNotBlank()) {
                    // Create and send a message
                    val message = Message(
                        text = messageInput,
                        senderId = userId,
                        timestamp = System.currentTimeMillis()
                    )
                    messagesRef.push().setValue(message)
                        .addOnSuccessListener {
                            Log.d("ChatScreen", "Message sent successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatScreen", "Failed to send message", e)
                        }
                    messageInput = "" // Clear input field
                }
            }) {
                Text("Send")
            }
        }
    }
}