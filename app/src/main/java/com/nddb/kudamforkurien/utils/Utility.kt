package com.nddb.kudamforkurien.utils


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nddb.kudamforkurien.Activity.DrawerActivity
import com.nddb.kudamforkurien.Activity.SplashActivity
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.backgroundservice.MotionService


object Utility {

    // Notification ID.
    private const val NOTIFICATION_ID = 0

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {


        val contentIntent = Intent(applicationContext, SplashActivity::class.java)

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
            .setSmallIcon(R.drawable.app_icon)
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