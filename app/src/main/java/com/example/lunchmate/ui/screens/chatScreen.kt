import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lunchmate.model.Message

@Composable
fun ChatScreen(eventName: String, userId: String) {
    val documentId = "$eventName by $userId" // Custom document ID
    var messageInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val db = FirebaseFirestore.getInstance()
    val chatDocRef = db.collection("chats").document(documentId)

    // Attach a listener to load messages in real-time
    LaunchedEffect(documentId) {
        chatDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ChatScreen", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                // Load messages from the document
                messages = snapshot.get("messages")?.let { messagesData ->
                    (messagesData as List<Map<String, Any>>).map { messageData ->
                        Message(
                            text = messageData["text"] as? String ?: "",
                            senderId = messageData["senderId"] as? String ?: "",
                            timestamp = messageData["timestamp"] as? Long ?: 0L
                        )
                    }
                } ?: emptyList()
            }
        }
    }

    // UI for chat messages and input
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Welcome message
        Text(
            text = "Welcome to the discussion space for: $documentId",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { message ->
                Text(
                    text = "${message.senderId}: ${message.text}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
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
                // Log to see if the send button is clicked
                Log.d("ChatScreen", "Send button clicked with input: $messageInput")

                if (messageInput.isNotBlank()) {
                    val message = Message(
                        text = messageInput,
                        senderId = userId,
                        timestamp = System.currentTimeMillis()
                    )

                    // Debug log before sending
                    Log.d("ChatScreen", "Preparing to send message: $message")

                    // Check if document exists
                    chatDocRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null && task.result.exists()) {
                                // Document exists, update it
                                chatDocRef.update("messages", FieldValue.arrayUnion(mapOf(
                                    "text" to message.text,
                                    "senderId" to message.senderId,
                                    "timestamp" to message.timestamp
                                ))).addOnSuccessListener {
                                    Log.d("ChatScreen", "Message sent successfully")
                                    messageInput = "" // Clear the input field
                                }.addOnFailureListener { e ->
                                    Log.e("ChatScreen", "Failed to send message", e)
                                }
                            } else {
                                // Document does not exist, create it with the initial message
                                chatDocRef.set(mapOf("messages" to listOf(mapOf(
                                    "text" to message.text,
                                    "senderId" to message.senderId,
                                    "timestamp" to message.timestamp
                                )))).addOnSuccessListener {
                                    Log.d("ChatScreen", "Document created and message sent successfully")
                                    messageInput = "" // Clear the input field
                                }.addOnFailureListener { e ->
                                    Log.e("ChatScreen", "Failed to create document and send message", e)
                                }
                            }
                        } else {
                            Log.e("ChatScreen", "Failed to check document existence", task.exception)
                        }
                    }
                } else {
                    Log.d("ChatScreen", "Message input is blank, not sending.")
                }
            }) {
                Text("Send")
            }
        }
    }
}
