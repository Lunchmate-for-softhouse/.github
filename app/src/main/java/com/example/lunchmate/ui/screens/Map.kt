/*package com.example.lunchmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MapScreen(navController: NavController) {
    val mapView = rememberMapView()
    var isMapLoaded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    onCreate(null)
                    onResume()
                    getMapAsync { googleMap ->
                        val stockholm = LatLng(59.3293, 18.0686) // Coordinates for Stockholm, Sweden
                        googleMap.addMarker(MarkerOptions().position(stockholm).title("Marker in Stockholm"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stockholm, 10f)) // Zoom level 10

                        // Set map loaded state to true
                        isMapLoaded = true
                    }
                }
            },
            modifier = Modifier.weight(1f) // Allow map to fill the available space
        )

        // Show a message when the map is loaded
        if (isMapLoaded) {
            Text(
                text = "Map loaded successfully!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp) // Add some padding around the message
            )
        }
    }
}

@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current
    return remember { MapView(context) }
}
*/
/*package com.example.lunchmate.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController) {
    // Request location permission
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // State to hold the current location
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    // Request permission and get current location
    LaunchedEffect(Unit) {
        // Check if the permission is granted
        if (locationPermissionState.status is PermissionStatus.Granted) {
            // Get current location if permission is granted
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        isMapLoaded = true
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception if location permission is denied
                isMapLoaded = false
            }
        } else {
            // Request permission if not granted
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Display the Map
        AndroidView(
            factory = {
                rememberMapView().apply {
                    onCreate(null)
                    onResume()
                    getMapAsync { googleMap ->
                        currentLocation?.let { latLng ->
                            googleMap.addMarker(MarkerOptions().position(latLng).title("You are here!"))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // Zoom level 15
                        }
                    }
                }
            },
            modifier = Modifier.weight(1f) // Allow map to fill the available space
        )

        // Show a message when the map is loaded with the user's location
        if (isMapLoaded) {
            Text(
                text = "Map loaded successfully!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp) // Add some padding around the message
            )
        } else {
            // Show a message if location permission is not granted
            if (locationPermissionState.status is PermissionStatus.Denied) {
                Text(
                    text = "Location permission required.",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current
    return remember { MapView(context) }
}*/
package com.example.lunchmate.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController) {
    // Request location permission
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    // Check permission status and request it if not granted
    LaunchedEffect(Unit) {
        if (locationPermissionState.status is PermissionStatus.Granted) {
            // Get the last known location if permission is granted
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    isMapLoaded = true // Set the map to loaded state
                }
            }
        } else if (locationPermissionState.status is PermissionStatus.Denied) {
            // Request the location permission
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Create and display the MapView directly
        AndroidView(
            factory = {
                MapView(context).apply {
                    onCreate(null)
                    onResume()
                    getMapAsync { googleMap ->
                        // Check if current location is available
                        currentLocation?.let { latLng ->
                            googleMap.addMarker(MarkerOptions().position(latLng).title("You are here!"))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // Zoom level 15
                        } ?: run {
                            // If current location is not available, show a default location (e.g., Stockholm)
                            val stockholm = LatLng(59.3293, 18.0686) // Coordinates for Stockholm, Sweden
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stockholm, 10f)) // Zoom out to show more area
                        }
                    }
                }
            },
            modifier = Modifier.weight(1f) // Allow the map to fill the available space
        )

        // Show a message when the map is loaded with the user's location
        if (isMapLoaded) {
            Text(
                text = "Map loaded successfully!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp) // Add some padding around the message
            )
        } else if (locationPermissionState.status is PermissionStatus.Denied) {
            // Show a message if the permission is denied
            Text(
                text = "Location permission required.",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }
}

