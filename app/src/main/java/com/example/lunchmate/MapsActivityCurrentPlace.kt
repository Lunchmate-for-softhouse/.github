package com.example.lunchmate

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.lunchmate.ui.screens.chaneloc
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import androidx.navigation.NavHostController

//var userselectedresturant= ""


/*class MapsActivityCurrentPlace : ComponentActivity(navController = navController) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsScreen()
        }
    }
}*/
@Composable
fun MapsActivityCurrentPlaceScreen(navController: NavController) {
    // This is the entry point of the MapsActivityCurrentPlace content
    MapsScreen(navController)
}

@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    var test = false

    // State to control the dialog
    var selectedRestaurantName by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AndroidView({ mapView }) { map ->
            map.getMapAsync { googleMap ->
                setupMap(googleMap, context, navController) { restaurantName ->
                    // Set the selected restaurant name and show the dialog
                    selectedRestaurantName = restaurantName
                    showDialog = true
                    //userselectedresturant = restaurantName

                }
            }
        }

        Button(
            onClick = {  val intent = Intent(context, ResturantListActivity::class.java)
                context.startActivity(intent) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("View Restaurant List")
        }

        // Show dialog if selectedRestaurantName is not null
        if (showDialog && selectedRestaurantName != null) {

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Restaurant Selected") },
                text = {
                    Column {
                        Text(text = selectedRestaurantName!!, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            //eventNamer= userselectedresturant
                            //navController.navigate("create_event")
                            navController.navigate("create_event/${selectedRestaurantName}")
                            showDialog = false
                        }) {
                            Text("Create")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )

        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(context: android.content.Context): MapView {
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

private fun setupMap(googleMap: GoogleMap, context: Context, navController: NavController, onMarkerClick: (String) -> Unit) {
    val customMarkerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.restaurants_icon)
    val customMarker = BitmapDescriptorFactory.fromBitmap(
        Bitmap.createScaledBitmap(customMarkerBitmap, 110, 110, false)
    )

    val locationCoordinates = mapOf(
        "Malmö" to LatLng(55.611331059590206, 13.002231964445214),
        "Karlskrona" to LatLng(56.18320240744998, 15.593305575774751),
        "Växjö" to LatLng(56.87750939887021, 14.808042591619134),
        "Stockholm" to LatLng(59.33887571833381, 18.057613734616805),
        "Karlshamn" to LatLng(56.16464684268269, 14.866310010348741),
        "Kalmar" to LatLng(56.66415607872528, 16.37026323525738),
        "Jönköping" to LatLng(57.78355698256043, 14.164313337394715),
        "Luleå" to LatLng(65.58116493400121, 22.148896566956076),
        "Uppsala" to LatLng(59.85965384178488, 17.636767367327494),
        "Sarajevo" to LatLng(43.84662503289982, 18.35077154879623)
    )

    val selectedLocation = chaneloc // Replace this with the location you want
    val latLng = locationCoordinates[selectedLocation]

    latLng?.let {
        // Move the camera to the selected location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM.toFloat()))
        // Draw a circle around the selected location
        drawCircle(googleMap, it, 1000)
        // Search for nearby restaurants and add them as markers
        searchNearbyRestaurants(it, googleMap, customMarker, onMarkerClick)
    }
}


private fun drawCircle(map: GoogleMap, location: LatLng, radius: Int) {
    map.addCircle(
        CircleOptions()
            .center(location)
            .radius(radius.toDouble())
            .strokeColor(0x220000FF)
            .fillColor(0x220000FF)
            .strokeWidth(2f)
    )
}

private fun searchNearbyRestaurants(location: LatLng, map: GoogleMap, customMarker: BitmapDescriptor, onMarkerClick: (String) -> Unit) {
    val radius = 1000
    val type = "restaurant"
    val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08" // Replace with your actual API key
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=$radius&type=$type&key=$apiKey"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use

                val jsonResponse = JSONObject(response.body!!.string())
                val results = jsonResponse.getJSONArray("results")

                withContext(Dispatchers.Main) {
                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val latLng = LatLng(
                            place.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                            place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                        )
                        val placeName = place.getString("name")

                        map.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(placeName)
                                .icon(customMarker)
                        )
                    }

                    // Set the OnMarkerClickListener
                    map.setOnMarkerClickListener { marker ->
                        // Get the restaurant name from the marker title
                        val restaurantName = marker.title ?: ""
                        onMarkerClick(restaurantName) // Invoke the callback
                        true // Return true to indicate that we have consumed the click event
                    }
                }
            }
        } catch (e: Exception) {
            // Handle the error
        }
    }
}

/*@Composable
fun showRestaurantCardDialog(restaurantName: String, navController: NavController) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Restaurant Selected") },
            text = {
                Column {
                    Text(text = restaurantName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        navController.navigate("create_event/$restaurantName")
                        showDialog = false
                    }) {
                        Text("Create")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}
*/


private fun openRestaurantList() {
    // Implement the logic to open the restaurant list activity
}

private const val DEFAULT_ZOOM = 14
