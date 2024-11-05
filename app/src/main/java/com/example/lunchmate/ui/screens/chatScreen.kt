import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.lunchmate.ui.screens.eventcreator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(eventName: String, userId: String) {
    val documentId = "$eventName by $eventcreator"
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
                // Load messages from the "messages" array field in the document
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
            .padding(8.dp)
            .background(Color(0xFFF7F7F7)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display messages in a chat bubble style
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true // Newest message at the bottom
        ) {
            items(messages) { message ->
                val isCurrentUser = message.senderId == userId
                val backgroundColor = if (isCurrentUser) Color(0xFFD1F7C4) else Color.White
                val alignment = if (isCurrentUser) Alignment.End else Alignment.Start

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .widthIn(max = 250.dp),
                        horizontalAlignment = alignment
                    ) {
                        Text(
                            text = message.text,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Type your message...") },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
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
                        chatDocRef.update("messages", FieldValue.arrayUnion(mapOf(
                            "text" to message.text,
                            "senderId" to message.senderId,
                            "timestamp" to message.timestamp
                        ))).addOnSuccessListener {
                            messageInput = ""
                        }.addOnFailureListener { e ->
                            Log.e("ChatScreen", "Failed to send message", e)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF128C7E)),
                modifier = Modifier.size(48.dp)
            ) {
                Text("Send", color = Color.White)
            }
        }
    }
}
