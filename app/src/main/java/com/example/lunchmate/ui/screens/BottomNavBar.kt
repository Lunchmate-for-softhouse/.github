import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB4572F))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("current_events") }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Event,
                    contentDescription = "Current Events",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Current Events",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black)
                )
            }
        }
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
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "Maps",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Maps",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("create_event") }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Event",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Create Event",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black)
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { navController.navigate("review_pag") }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Reviews,
                    contentDescription = "Reviews",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Text(
                    text = "Reviews",
                    style = TextStyle(fontSize = 12.sp, color = Color.Black)
                )
            }
        }
    }
}
