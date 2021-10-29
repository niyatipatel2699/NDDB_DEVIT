package com.devit.nddb.backgroundservice

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class ServiceAdmin {
    private fun setServiceIntent(context: Context) {
        if (serviceIntent == null) {
            serviceIntent = Intent(context, AutoStartService::class.java)
        }
        serviceIntent!!.putExtra("stopped", false)
    }

    fun stopService(context: Context?) {
        serviceIntent!!.putExtra("stopped", true)
        Log.d(TAG, "launchService:  Service is stopping....")
        ContextCompat.startForegroundService(context!!, serviceIntent!!)
    }

    fun launchService(context: Context?) {
        if (context == null) {
            return
        }
        setServiceIntent(context)

        // depending on the version of Android we either launch the simple service (version<O)
        // or we start a foreground service
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }*/ContextCompat.startForegroundService(context, serviceIntent!!)
        Log.d(TAG, "launchService:  Service is starting....")
    }

    companion object {
        private const val TAG = "ServiceAdmin"
        private var serviceIntent: Intent? = null
    }
}