
package com.example.lunchmate

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.example.lunchmate.ui.screens.chaneloc
import android.graphics.Paint
import android.graphics.Typeface


@Composable
fun MapsActivityCurrentPlaceScreen(navController: NavController) {
    MapsScreen(navController)
}


@Composable
fun rememberMapViewWithLifecycle(context: Context): MapView {
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

private fun setupMap(
    googleMap: GoogleMap,
    context: Context,
    navController: NavController,
    onMarkerClick: (String) -> Unit
) {

    val customMarker = BitmapDescriptorFactory.fromResource(R.drawable.restaurants_icon)
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

    val selectedLocation = chaneloc
    val latLng = locationCoordinates[selectedLocation]

    latLng?.let {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM.toFloat()))
        drawCircle(googleMap, it, 1000)
        // Update the call to match the correct function signature
        searchNearbyRestaurants(it, googleMap, onMarkerClick, context)

    }
}
@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)

    // State to control the dialog
    var selectedRestaurantName by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AndroidView({ mapView }, modifier = Modifier.weight(1f)) { map ->
                map.getMapAsync { googleMap ->
                    setupMap(googleMap, context, navController) { restaurantName ->
                        selectedRestaurantName = restaurantName
                        showDialog = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("restaurant_list") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("View Restaurant List")
            }
        }

        if (showDialog && selectedRestaurantName != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Restaurant Selected") },
                text = {
                    Column {
                        Text(
                            text = selectedRestaurantName!!,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
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
private fun createCustomMarkerWithTitle(context: Context, title: String): BitmapDescriptor {
    // Define paint and dimensions
    val paint = Paint().apply {
        color = android.graphics.Color.RED
        textSize = 40f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    // Create a bitmap with a larger height to accommodate the icon and title
    val width = 250
    val height = 150 // Increased height for better fit
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Load and scale the restaurants_icon to fit within the custom marker dimensions
    val markerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.restaurants_icon)
    val scaledIcon = Bitmap.createScaledBitmap(markerBitmap, 80, 80, false) // Adjust the scaling if necessary

    // Draw the scaled icon centered in the bottom half of the canvas
    canvas.drawBitmap(scaledIcon, (width - scaledIcon.width) / 2f, 50f, null)

    // Draw the title centered above the icon
    canvas.drawText(title, 20f, 40f, paint) // Adjusted text position

    return BitmapDescriptorFactory.fromBitmap(bitmap)
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



@SuppressLint("CoroutineCreationDuringComposition")
private fun searchNearbyRestaurants(
    location: LatLng,
    map: GoogleMap,
    onMarkerClick: (String) -> Unit,
    context: Context
) {
    val radius = 1000
    val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08" // Replace with your actual API key
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=$radius&type=restaurant&key=$apiKey"

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

                        // Use custom marker with title included
                        val customMarkerWithText = createCustomMarkerWithTitle(context, placeName)

                        // Add the marker to the map
                        map.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .icon(customMarkerWithText) // Custom marker with title
                        )?.tag = placeName // Store the name in the tag
                    }

                    map.setOnMarkerClickListener { marker ->
                        val restaurantName = marker.tag as? String ?: ""
                        onMarkerClick(restaurantName)
                        true
                    }
                }
            }
        } catch (e: Exception) {
            // Handle the error
            e.printStackTrace()
        }
    }
}

private const val DEFAULT_ZOOM = 15

