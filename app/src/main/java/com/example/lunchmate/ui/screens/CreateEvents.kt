package com.example.lunchmate.ui.screens

import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.lunchmate.MapsActivityCurrentPlace

@Composable
fun CreateEventScreen() {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(context, MapsActivityCurrentPlace::class.java)
        context.startActivity(intent)
    }) {
        Text("Open Map")
    }
}