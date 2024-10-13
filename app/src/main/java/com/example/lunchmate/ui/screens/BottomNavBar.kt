import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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

@Composable
fun BottomNavBar(navController: NavController) {
    val fontSize = 12.sp
    val buttonSpacing = 64.dp
    val textcolour = Color.Black

    BottomAppBar(
        containerColor = Color(0xFFB4572F),
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(onClick = { navController.navigate("current_events") }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Event, contentDescription = "Current Events")
                Text(
                    text = "Current Events",
                    fontSize = fontSize,
                    color = textcolour
                )
            }
        }

        Spacer(modifier = Modifier.width(buttonSpacing))

        IconButton(onClick = { navController.navigate("upcoming_events") }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.EventAvailable, contentDescription = "Upcoming Events")
                Text(
                    text = "Upcoming Events",
                    fontSize = fontSize,
                    color = textcolour
                )
            }
        }

        Spacer(modifier = Modifier.width(buttonSpacing))

        IconButton(onClick = { navController.navigate("create_event") }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Create Lunch Event")
                Text(
                    text = "Create Event",
                    fontSize = fontSize,
                    color = textcolour
                )
            }
        }
    }
}
