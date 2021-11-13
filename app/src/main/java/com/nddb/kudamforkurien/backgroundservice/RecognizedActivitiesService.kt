package com.nddb.kudamforkurien.backgroundservice

import android.app.IntentService
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionResult

class RecognizedActivitiesService : IntentService(TAG) {

    companion object {
        private val TAG = RecognizedActivitiesService::class.java.name
        val BROADCAST_ACTIVITY_RECOGNITION = "activity_recognition"
        internal val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 1000
    }

    override fun onHandleIntent(receivedIntent: Intent?) {

        val recognitionResult = ActivityRecognitionResult.extractResult(receivedIntent)

      //  val recognizedActivities = recognitionResult.probableActivities as ArrayList<*>

        for (activity in recognitionResult.probableActivities) {
           /* if (activity.type == DetectedActivity.RUNNING || activity.type == DetectedActivity.WALKING)
            {

            }*/
            val intent = Intent(BROADCAST_ACTIVITY_RECOGNITION)
            intent.putExtra("type", activity.type)
            intent.putExtra("confidence", activity.confidence)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

}