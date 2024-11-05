import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lunchmate.model.Message
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(eventName: String, userId: String) {
    val documentId = eventName
    var messageInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val db = FirebaseFirestore.getInstance()
    val chatDocRef = db.collection("chats").document(documentId)

    // Attach a listener to load messages in real-time for all users
    LaunchedEffect(documentId) {
        chatDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ChatScreen", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
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
            .background(Color(0xFFEAEAEA)) // Light background color for chat screen
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Chat: $documentId",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Space between messages
        ) {
            items(messages) { message ->
                val isCurrentUser = message.senderId == userId
                MessageBubble(message, isCurrentUser)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Type your message", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color(0xFF6200EE),
                    unfocusedIndicatorColor = Color.Transparent,
                    containerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        val message = Message(
                            text = messageInput,
                            senderId = userId,
                            timestamp = System.currentTimeMillis()
                        )

                        chatDocRef.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result != null && task.result.exists()) {
                                    chatDocRef.update("messages", FieldValue.arrayUnion(mapOf(
                                        "text" to message.text,
                                        "senderId" to message.senderId,
                                        "timestamp" to message.timestamp
                                    ))).addOnSuccessListener {
                                        messageInput = ""
                                    }.addOnFailureListener { e ->
                                        Log.e("ChatScreen", "Failed to send message", e)
                                    }
                                } else {
                                    chatDocRef.set(mapOf("messages" to listOf(mapOf(
                                        "text" to message.text,
                                        "senderId" to message.senderId,
                                        "timestamp" to message.timestamp
                                    )))).addOnSuccessListener {
                                        Log.d("ChatScreen", "Document created and message sent successfully")
                                        messageInput = ""
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
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Button color
            ) {
                Text("Send", color = Color.White)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    val backgroundColor = if (isCurrentUser) Color(0xFFDCEDC8) else Color.White
    val textColor = if (isCurrentUser) Color.Black else Color.Gray

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                text = message.senderId,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = textColor),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
            )
        }
    }
}
