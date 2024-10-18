import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lunchmate.MapsActivityCurrentPlace

@Composable
fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current

    // Bottom navigation bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB4572F))
            .padding(8.dp)
    ) {
        // Button for "Current Events"
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("current_events") }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Placeholder for an icon or image
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Black)
                )
                BasicText(
                    text = "Current Events",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black) // Updated here
                )
            }
        }

        // Button for "Upcoming Events"
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("upcoming_events") }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Black)
                )
                BasicText(
                    text = "Upcoming Events",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black) // Updated here
                )
            }
        }

        // Button for "Create Event" that launches MapsActivityCurrentPlace
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    val intent = Intent(context, MapsActivityCurrentPlace::class.java)
                    context.startActivity(intent)
                }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Black)
                )
                BasicText(
                    text = "Create Event",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black) // Updated here
                )
            }
        }
    }
}
