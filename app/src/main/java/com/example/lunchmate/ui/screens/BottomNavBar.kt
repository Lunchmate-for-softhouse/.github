import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.lunchmate.ui.screens.CreateEventScreen


@Composable
fun BottomNavBar(navController: NavController) {
    val fontSize = 12.sp
    val textColour = Color.Black

    BottomAppBar(
        containerColor = Color(0xFFB4572F),
        modifier = Modifier.padding(8.dp)
    ) {
        // Use weight to equally space buttons
        IconButton(
            onClick = { navController.navigate("current_events") },
            modifier = Modifier.weight(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Event, contentDescription = "Current Events", modifier = Modifier.size(24.dp))
                Text(
                    text = "Current Events",
                    fontSize = fontSize,
                    color = textColour
                )
            }
        }

        IconButton(
            onClick = { navController.navigate("upcoming_events") },
            modifier = Modifier.weight(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.EventAvailable, contentDescription = "Upcoming Events",
                    modifier = Modifier.size(24.dp))
                Text(
                    text = "Upcoming Events",
                    fontSize = fontSize,
                    color = textColour
                )
            }
        }

        IconButton(
            onClick = { CreateEventScreen() }
            ,
            modifier = Modifier.weight(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Create Event",
                    modifier = Modifier.size(24.dp))
                Text(
                    text = "Create Event",
                    fontSize = fontSize,
                    color = textColour
                )
            }
        }
    }
}


}
