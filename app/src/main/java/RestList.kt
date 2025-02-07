package com.example.lunchmate
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*


import androidx.compose.ui.unit.dp
import com.example.lunchmate.ui.screens.chaneloc


@Composable
fun RestList(navController: NavController) {
    val resloc = chaneloc

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

    val selectedLocationCoordinates = locationCoordinates[resloc]
    var restaurantList by remember { mutableStateOf(listOf<Restaurant>()) }
    var sortOption by remember { mutableStateOf("Price") }

    LaunchedEffect(selectedLocationCoordinates) {
        selectedLocationCoordinates?.let { location ->
            searchNearbyRestaurants(location) { restaurants ->
                restaurantList = restaurants
            }
        }
    }
    fun sortRestaurants() {
        restaurantList = when (sortOption) {
            "Price" -> restaurantList.sortedBy { it.priceLevel }
            "Popularity" -> restaurantList.sortedByDescending { it.rating }
            else -> restaurantList
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.navigate("macp") }) {
                Text("Back to Map")
            }

            // Toggle Button for sorting
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        sortOption = "Price"
                        sortRestaurants()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sortOption == "Price") Color.Gray else Color.LightGray
                    )
                ) {
                    Text("by Price")
                }

                Button(
                    onClick = {
                        sortOption = "Popularity"
                        sortRestaurants()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sortOption == "Popularity") Color.Gray else Color.LightGray
                    )
                ) {
                    Text("by Popularity")
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(restaurantList, key = { it.id }) { restaurant ->
                RestaurantItem(navController, restaurant, selectedLocationCoordinates)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
        // Restaurant list display


@Composable
fun RestaurantItem(navController: NavController, restaurant: Restaurant, origin: LatLng?) {
    val distance = origin?.let {
        val originLocation = Location("").apply {
            latitude = it.latitude
            longitude = it.longitude
        }
        val restaurantLocation = Location("").apply {
            latitude = restaurant.location.latitude
            longitude = restaurant.location.longitude
        }
        originLocation.distanceTo(restaurantLocation) / 1000 // Convert to kilometers
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray)
            .padding(16.dp)
    ) {
        // Restaurant data part (3/4 of the width)
        Column(
            modifier = Modifier
                .weight(3f)
        ) {
            Text(text = restaurant.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = restaurant.address, fontSize = 14.sp, color = Color.Gray)
            if (distance != null) {
                Text(text = "Distance: ${"%.2f".format(distance)} km", fontSize = 14.sp, color = Color.Gray)
            }
            Text(
                text = "Price Level: ${getPriceLevelString(restaurant.priceLevel)}",
                fontSize = 14.sp,
                color = Color(0xFFFFA500) // Orange color
            )
            Text(
                text = "Rating: ${restaurant.rating?.toString() ?: "N/A"}",
                fontSize = 14.sp,
                color = Color.Red // Red color
            )
        }

        // Buttons part (1/4 of the width)
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    restaurant.websiteUrl?.let { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        navController.context.startActivity(intent)
                    }
                },
                //enabled = restaurant.websiteUrl != null
            ) {
                Text("Menu")
            }

            Button(
                onClick = {
                    navController.navigate("create_event/${restaurant.name}")
                }
            ) {
                Text("Order")
            }
        }
    }
}



data class Restaurant(
    val id:String,
    val name: String,
    val address: String,
    val location: LatLng,
    val placeId: String,    // To store the Place ID
    var websiteUrl: String? = null, // To store the website URL
    var priceLevel: Int? = null, // To store the price level
    var rating: Double? = null // To store the rating
)


// Function to fetch nearby restaurants with place ID
private fun searchNearbyRestaurants(location: LatLng, onResult: (List<Restaurant>) -> Unit) {
    val radius = 1000
    val type = "restaurant"
    val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08"
    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=$radius&type=$type&key=$apiKey"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("API Error", "Failed to fetch nearby places: ${response.code}")
                    return@use
                }

                val jsonResponse = response.body!!.string()
                val results = JSONObject(jsonResponse).getJSONArray("results")
                val restaurants = mutableListOf<Restaurant>()

                for (i in 0 until results.length()) {
                    val id = "a$i"
                    val place = results.getJSONObject(i)
                    val name = place.getString("name")
                    val address = place.optString("vicinity", "Address not available")
                    val lat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    val lng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                    val placeId = place.getString("place_id")
                    val priceLevel = place.optInt("price_level", -1).takeIf { it != -1 }
                    val rating = place.optDouble("rating", -1.0).takeIf { it != -1.0 }

                    val restaurant = Restaurant(id,name, address, LatLng(lat, lng), placeId, priceLevel = priceLevel, rating = rating)
                    getWebsiteUrl(restaurant) // Fetch the website URL based on placeId
                    restaurants.add(restaurant)
                }

                withContext(Dispatchers.Main) {
                    onResult(restaurants)
                }
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error fetching nearby restaurants", e)
        }
    }
}

// Function to fetch website URL for a restaurant
private fun getWebsiteUrl(restaurant: Restaurant) {
    val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08"
    val url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=${restaurant.placeId}&fields=website&key=$apiKey"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = response.body!!.string()
                    val result = JSONObject(jsonResponse).optJSONObject("result")
                    val websiteUrl = result?.optString("website")

                    withContext(Dispatchers.Main) {
                        restaurant.websiteUrl = websiteUrl
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("API Error", "Error fetching website URL", e)
        }
    }
}

fun getPriceLevelString(priceLevel: Int?): String {
    return priceLevel?.let {
        "$".repeat(it)
    } ?: "N/A"
}

