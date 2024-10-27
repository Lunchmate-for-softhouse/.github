package com.example.lunchmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunchmate.model.Restaurant
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ResturantListActivity : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient
    private lateinit var restaurantRecyclerView: RecyclerView
    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurantList = mutableListOf<Restaurant>()

    // Predefined locations and coordinates
    private val locationOptions = arrayOf(
        "Softhouse Malmo",
        "Softhouse Karlskrona",
        "Softhouse Vaxjo"
    )

    private val locationCoordinates = mapOf(
        "Softhouse Malmo" to LatLng(55.611331059590206, 13.002231964445214),
        "Softhouse Karlskrona" to LatLng(56.18320240744998, 15.593305575774751),
        "Softhouse Vaxjo" to LatLng(56.87750939887021, 14.808042591619134)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout from XML
        setContentView(R.layout.restaurant_item)

        // Initialize Places
        Places.initialize(applicationContext, "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08") // Replace with your API key
        placesClient = Places.createClient(this)

        // Initialize RecyclerView
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView)
        restaurantRecyclerView.layoutManager = LinearLayoutManager(this)
        restaurantAdapter = RestaurantAdapter(restaurantList) { restaurant ->
            // Handle restaurant click, e.g., navigate to restaurant details
        }
        restaurantRecyclerView.adapter = restaurantAdapter

        // Show location selection dialog on activity start
        showLocationSelectionDialog()

        // Filter button logic (assuming filter button is defined in XML)
        val filterButton: Button = findViewById(R.id.filterButton)
        filterButton.setOnClickListener {
            showFilterDialog()
        }

        val MapViewButton: Button = findViewById(R.id.btnListView)
        MapViewButton.setOnClickListener {
            openRestaurantList()
        }
    }
    private fun openRestaurantList() {
        val intent = Intent(this@ResturantListActivity, MapsActivityCurrentPlace::class.java)
        startActivity(intent)
    }

    private fun showLocationSelectionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Location")

        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(spinner)

        builder.setPositiveButton("OK") { dialog, _ ->
            val selectedLocation = spinner.selectedItem.toString()
            val latLng = locationCoordinates[selectedLocation]

            if (latLng != null) {
                // Now call the API to fetch restaurants based on the selected location
                searchNearbyRestaurants(latLng)
            }
            dialog.dismiss()
        }

        builder.setCancelable(false)
        builder.show()
    }

    private fun searchNearbyRestaurants(location: LatLng) {
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
                        Log.e(TAG, "Failed to fetch nearby places: ${response.code}")
                        return@use
                    }

                    val jsonResponse = response.body!!.string()
                    Log.d(TAG, "API Response: $jsonResponse")

                    val results = JSONObject(jsonResponse).getJSONArray("results")

                    withContext(Dispatchers.Main) {
                        restaurantList.clear()
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val placeId = place.getString("place_id")  // Get place ID
                            fetchPlaceDetails(placeId)  // Fetch detailed information
                        }
                        restaurantAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching nearby restaurants", e)
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
                    val priceLevel = placeDetails.optString("price_level",)
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
                        restaurantList.add(restaurant) // Add restaurant to the list
                        restaurantAdapter.notifyDataSetChanged() // Notify adapter of data changes
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching place details", e)
            }
        }
    }

    private fun showFilterDialog() {
       // val types = arrayOf("All", "Restaurant", "Cafe", "Pub")
        val priceLevels = arrayOf("All", "Inexpensive", "Moderate", "Expensive", "Very Expensive")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Filter Restaurants")

        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter, null)
        builder.setView(dialogView)

        //val cuisineSpinner: Spinner = dialogView.findViewById(R.id.cuisineSpinner)
        val priceSpinner: Spinner = dialogView.findViewById(R.id.priceSpinner)

        // Set up the spinners
        //val cuisineAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        //cuisineSpinner.adapter = cuisineAdapter

        val priceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priceLevels)
        priceSpinner.adapter = priceAdapter

        builder.setPositiveButton("Apply") { dialog, _ ->
            //val selectedCuisine = cuisineSpinner.selectedItem.toString()
            val selectedPriceLevel = priceSpinner.selectedItem.toString()

            filterRestaurants( selectedPriceLevel)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
    private fun filterRestaurants(selectedPriceLevel: String) {
        val filteredList = restaurantList.filter { restaurant ->
            // Map restaurant price levels to the filter choices
            val priceMatches = when (selectedPriceLevel) {
                "All" -> true
                "Inexpensive" -> restaurant.priceLevel == "1"
                "Moderate" -> restaurant.priceLevel == "2"
                "Expensive" -> restaurant.priceLevel == "3"
                "Very Expensive" -> restaurant.priceLevel == "4"
                else -> true
            }

            // Check if the cuisine type matches
            //val cuisineMatches = selectedCuisine == "All" || restaurant.types.contains(selectedCuisine)


             priceMatches
        }

        // Update the adapter data without creating a new instance
        restaurantAdapter = RestaurantAdapter(filteredList) { restaurant ->
            // Handle restaurant click
        }
        restaurantRecyclerView.adapter = restaurantAdapter
    }

    companion object {
        private const val TAG = "ResturantListActivity"
    }
}