package com.devit.nddb.utils


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.R
import com.devit.nddb.backgroundservice.MotionService


object Utility {

    // Notification ID.
    private const val NOTIFICATION_ID = 0

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {


        val contentIntent = Intent(applicationContext, DrawerActivity::class.java)

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val builder = NotificationCompat.Builder(
            applicationContext,
            MotionService.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Deliver the notification
        notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * Cancels all notifications.
     *
     */
    fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

}