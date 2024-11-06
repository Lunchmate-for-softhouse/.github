/*
package com.example.lunchmate.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.os.Build
import com.example.lunchmate.R

/*class ReviewNotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Create and show the notification
        sendNotification("Review your order", "Don't forget to leave a review for your recent order!")
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "review_channel"

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Review Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for order review reminders"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build and issue the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications) // Ensure your app icon is correct
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}*/
import android.app.PendingIntent
import android.content.Intent
import com.example.lunchmate.MainActivity

class ReviewNotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Create and show the notification
        sendNotification("Review your order", "Don't forget to leave a review for your recent order!")
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "review_channel"

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Review Notifications",
                NotificationManager.IMPORTANCE_HIGH //Sets high priority to show the notification
            ).apply {
                description = "Channel for order review reminders"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent to navigate to the ReviewPage when the notification is clicked
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("navigate_to", "review") // Pass the extra to navigate to the review page
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the task stack
        }
        //allowing the user to navigate to the app’s review page later
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security best practices
        )

        // Build and issue the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent) // Set the intent to be triggered on click
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
*/