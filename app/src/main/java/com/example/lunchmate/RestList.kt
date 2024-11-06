/*package com.example.lunchmate

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lunchmate.model.Restaurant
import com.example.lunchmate.ui.screens.chaneloc
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// Hardcoding the location into a global variable (you can adjust as needed)
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

@Composable
fun RestaurantListScreen() {
    val viewModel: RestaurantListViewModel = viewModel()
    val restaurantList by viewModel.restaurantList.collectAsState()

    Column(Modifier.fillMaxSize()) {
        // Removing the location selection button
        // Button(
        //     onClick = { viewModel.showLocationSelectionDialog(locationCoordinates) },
        //     modifier = Modifier.align(Alignment.CenterHorizontally)
        // ) {
        //     Text("Select Location")
        // }

        Button(
            onClick = { viewModel.searchNearbyRestaurants(latLng) }, // Directly using chaneloc
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Find Nearby Restaurants")
        }

        LazyColumn(Modifier.fillMaxSize()) {
            items(restaurantList) { restaurant ->
                RestaurantItem(restaurant)
            }
        }
    }
}

@Composable
fun RestaurantItem(restaurant: Restaurant) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = restaurant.name)
        Text(text = restaurant.address ?: "No address available")
        Text(text = "Rating: ${restaurant.rating ?: "N/A"}")
    }
}

// Removed LocationSelectionDialog as it is no longer needed

@Composable
fun FilterDialog(onFilterApplied: (String) -> Unit) {
    val priceLevels = listOf("All", "Inexpensive", "Moderate", "Expensive", "Very Expensive")
    var selectedPrice by remember { mutableStateOf(priceLevels.first()) }

    AlertDialog(
        onDismissRequest = { },
        title = { Text("Filter Restaurants") },
        text = {
            Column {
                priceLevels.forEach { level ->
                    RadioButton(selected = selectedPrice == level, onClick = {
                        selectedPrice = level
                    })
                    Text(text = level)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onFilterApplied(selectedPrice) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = { }) { Text("Cancel") }
        }
    )
}

class RestaurantListViewModel : ViewModel() {
    private val _restaurantList = mutableStateListOf<Restaurant>()
    val restaurantList: State<List<Restaurant>> = _restaurantList

    // Removed the showLocationSelectionDialog method as it is no longer needed

    fun showFilterDialog() {
        // Logic to show filter dialog
    }

    fun searchNearbyRestaurants(location: LatLng?) {
        val radius = 1000
        val type = "restaurant"
        val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08" // Replace with your API key
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=$radius&type=$type&key=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("RestaurantListViewModel", "Failed to fetch nearby places: ${response.code}")
                        return@use
                    }

                    val jsonResponse = response.body!!.string()
                    val results = JSONObject(jsonResponse).getJSONArray("results")

                    _restaurantList.clear()
                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val placeId = place.getString("place_id")
                        fetchPlaceDetails(placeId)
                    }
                }
            } catch (e: Exception) {
                Log.e("RestaurantListViewModel", "Error fetching nearby restaurants", e)
            }
        }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&key=AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(detailsUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Failed to fetch place details: ${response.code}")
                        return@use
                    }

                    val jsonResponse = response.body!!.string()
                    Log.d(TAG, "Place Details Response: $jsonResponse")

                    val placeDetails = JSONObject(jsonResponse).getJSONObject("result")

                    // Extracting details
                    val placeName = placeDetails.optString("name")
                    val placeAddress = placeDetails.optString("vicinity", "No address available")
                    val rating = placeDetails.optDouble("rating", -1.0).toFloat()
                    val userRatingsTotal = placeDetails.optInt("user_ratings_total", 0)
                    val openingHours = placeDetails.optJSONObject("opening_hours")?.optJSONArray("weekday_text")?.let {
                        List(it.length()) { index -> it.getString(index) }
                    }
                    val priceLevel = placeDetails.optString("price_level")
                    val photoReference = placeDetails.optJSONArray("photos")?.getJSONObject(0)?.getString("photo_reference")
                    val types = placeDetails.optJSONArray("types")?.let {
                        List(it.length()) { index -> it.getString(index) }
                    } ?: emptyList()
                    val website = placeDetails.optString("website", null)

                    // Create a new Restaurant object and add it to the list
                    val restaurant = Restaurant(
                        name = placeName,
                        address = placeAddress,
                        rating = rating,
                        userRatingsTotal = userRatingsTotal,
                        openingHours = openingHours,
                        priceLevel = priceLevel,
                        photoReference = photoReference,
                        types = types,
                        website = website // Now using the website
                    )

                    withContext(Dispatchers.Main) {
                        _restaurantList.add(restaurant) // Add restaurant to the list
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching place details", e)
            }
        }
    }
}
*/