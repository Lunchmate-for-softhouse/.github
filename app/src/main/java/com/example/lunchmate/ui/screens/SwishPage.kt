package com.example.lunchmate.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SwishPage(navController: NavHostController, context: Context) {
    val swishPackageName = "se.swish.swish"
    val launchIntent = context.packageManager.getLaunchIntentForPackage(swishPackageName)

    if (launchIntent != null) {
        Log.d("SwishApp", "Launching Swish app")
        context.startActivity(launchIntent)
    } else {
        // If the explicit intent doesn't work, try a more general approach
        val uri = Uri.parse("swish://") // Example of a deep link
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        deepLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(deepLinkIntent)
        } catch (e: Exception) {
            Log.d("SwishApp", "Swish app not found. Redirecting to Play Store.")
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$swishPackageName"))
            context.startActivity(playStoreIntent)
            Toast.makeText(context, "Swish app is not installed. Redirecting to Play Store.", Toast.LENGTH_SHORT).show()
        }
    }
}

/*private fun launchSwishApp(context: Context) {
    val swishPackageName = "se.swish.swish"
    val launchIntent = context.packageManager.getLaunchIntentForPackage(swishPackageName)

    if (launchIntent != null) {
        Log.d("SwishApp", "Launching Swish app")
        context.startActivity(launchIntent)
    } else {
        // If the explicit intent doesn't work, try a more general approach
        val uri = Uri.parse("swish://") // Example of a deep link
        val deepLinkIntent = Intent(Intent.ACTION_VIEW, uri)
        deepLinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(deepLinkIntent)
        } catch (e: Exception) {
            Log.d("SwishApp", "Swish app not found. Redirecting to Play Store.")
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$swishPackageName"))
            context.startActivity(playStoreIntent)
            Toast.makeText(context, "Swish app is not installed. Redirecting to Play Store.", Toast.LENGTH_SHORT).show()
        }
    }
}
*/