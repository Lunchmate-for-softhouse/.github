package com.example.lunchmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.navigation.NavController
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MapsActivityCurrentPlace() : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private lateinit var placesClient: PlacesClient

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
        setContentView(R.layout.activity_maps)

        // Initialize Places
        Places.initialize(applicationContext, "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08")
        placesClient = Places.createClient(this)

        // Load the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val listViewButton: Button = findViewById(R.id.btnListView)
        listViewButton.setOnClickListener {
            openRestaurantList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        // Show the location selection dialog when the map is ready
        showLocationSelectionDialog()
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
                // Move the camera to the selected location
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))

                // Draw a 1km radius circle around the selected location
                drawCircle(latLng, 1000)

                // Adjust the camera zoom to fit the circle
                zoomToFitCircle(latLng, 1000)

                // Search for nearby restaurants at the selected location
                searchNearbyRestaurants(latLng)
            }
            dialog.dismiss()
        }

        builder.setCancelable(false)
        builder.show()
    }
    private fun openRestaurantList() {
        val intent = Intent(this@MapsActivityCurrentPlace, ResturantListActivity::class.java)
        startActivity(intent)
    }

    // Method to draw a circle of 1km radius around the location
    private fun drawCircle(location: LatLng, radius: Int) {
        map?.addCircle(
            CircleOptions()
                .center(location)
                .radius(radius.toDouble()) // Radius in meters
                .strokeColor(0x220000FF)   // Blue outline with transparency
                .fillColor(0x220000FF)     // Blue fill with transparency
                .strokeWidth(2f)
        )
    }

    // Method to adjust camera zoom to fit the circle
    private fun zoomToFitCircle(location: LatLng, radius: Int) {
        // Calculate the southwest and northeast bounds based on the radius
        val southwest = LatLng(location.latitude - radius / 111000.0, location.longitude - radius / (111000.0 * Math.cos(Math.toRadians(location.latitude))))
        val northeast = LatLng(location.latitude + radius / 111000.0, location.longitude + radius / (111000.0 * Math.cos(Math.toRadians(location.latitude))))

        // Build LatLngBounds using the southwest and northeast corners
        val bounds = LatLngBounds(southwest, northeast)

        // Move the camera to fit the bounds
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))  // Padding of 100 for a better fit
    }

    // Search for restaurants nearby within a 1km radius
    private fun searchNearbyRestaurants(location: LatLng) {
        val radius = 1000 // 1km
        val type = "restaurant"
        val apiKey = "AIzaSyBywwGx414Zvd7GIoP7TKh8BTN8DPYpt08"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${location.latitude},${location.longitude}&radius=$radius&type=$type&key=$apiKey"

        // Run network request in a background thread using a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Failed to fetch nearby places")
                        return@use
                    }

                    val jsonResponse = JSONObject(response.body!!.string())
                    val results = jsonResponse.getJSONArray("results")

                    // Switch to the Main thread for UI updates
                    withContext(Dispatchers.Main) {
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val latLng = LatLng(
                                place.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                            )
                            val placeName = place.getString("name")
                            val placeAddress = place.optString("vicinity", "No address available")

                            // Add a marker for each restaurant found
                            map?.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(placeName)
                                    .snippet(placeAddress)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching nearby restaurants", e)
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
        private const val DEFAULT_ZOOM = 15
    }
}
