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
        val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08" // Replace with your actual API key
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
                            val location = place.getJSONObject("geometry").getJSONObject("location")
                            val latLng = LatLng(location.getDouble("lat"), location.getDouble("lng"))

                            val placeName = place.getString("name")
                            val placeAddress = place.optString("vicinity", "No address available")

                            // Add restaurant to the list
                            restaurantList.add(Restaurant(placeName, placeAddress))
                        }
                        restaurantAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching nearby restaurants", e)
            }
        }
    }

    private fun showFilterDialog() {
        val cuisines = arrayOf("Italian", "Chinese", "Indian", "Mexican")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Cuisine")
        builder.setItems(cuisines) { _, which ->
            val selectedCuisine = cuisines[which]
            // Apply filter based on selectedCuisine (you can implement filtering logic here)
        }
        builder.show()
    }

    companion object {
        private const val TAG = "ResturantListActivity"
    }
}