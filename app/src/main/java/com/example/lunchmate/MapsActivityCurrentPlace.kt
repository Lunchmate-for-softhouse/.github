package com.example.lunchmate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.lunchmate.ui.screens.chaneloc
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MapsActivityCurrentPlace : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private lateinit var placesClient: PlacesClient


    private val customMarker by lazy {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.restaurants_icon)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 110, 110, false) // Adjust the size as needed
        BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    private val locationCoordinates = mapOf(
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
        showLocationSelectionDialog()
    }

    private fun showLocationSelectionDialog() {
        val selectedLocation = chaneloc
        val latLng = locationCoordinates[selectedLocation]

        if (latLng != null) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
            drawCircle(latLng, 1000)
            zoomToFitCircle(latLng, 1000)
            searchNearbyRestaurants(latLng)
        }
    }

    private fun openRestaurantList() {
        val intent = Intent(this@MapsActivityCurrentPlace, ResturantListActivity::class.java)
        startActivity(intent)
    }

    private fun drawCircle(location: LatLng, radius: Int) {
        map?.addCircle(
            CircleOptions()
                .center(location)
                .radius(radius.toDouble())
                .strokeColor(0x220000FF)
                .fillColor(0x220000FF)
                .strokeWidth(2f)
        )
    }

    private fun zoomToFitCircle(location: LatLng, radius: Int) {
        val southwest = LatLng(location.latitude - radius / 111000.0, location.longitude - radius / (111000.0 * Math.cos(Math.toRadians(location.latitude))))
        val northeast = LatLng(location.latitude + radius / 111000.0, location.longitude + radius / (111000.0 * Math.cos(Math.toRadians(location.latitude))))
        val bounds = LatLngBounds(southwest, northeast)
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
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
                        Log.e(TAG, "Failed to fetch nearby places")
                        return@use
                    }

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
                            val placeAddress = place.optString("vicinity", "No address available")

                            // Add a marker with the scaled custom icon
                            map?.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(placeName)
                                    .snippet(placeAddress)
                                    .icon(customMarker)
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
