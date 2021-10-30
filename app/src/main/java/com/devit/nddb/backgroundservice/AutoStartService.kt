package com.devit.nddb.backgroundservice

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.ResultReceiver
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.BuildConfig
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.utils.StepDetector
import com.wajahatkarim3.imagine.data.room.DatabaseBuilder
import com.wajahatkarim3.imagine.data.room.DatabaseHelper
import com.wajahatkarim3.imagine.data.room.DatabaseHelperImpl
import com.wajahatkarim3.imagine.data.room.entity.Steps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AutoStartService : Service, SensorEventListener, StepListener {
    var counter = 0
    var stepsWalked = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var myResultReceiver: ResultReceiver? = null
    var stopped = false
    var TEXT_NUM_STEPS = "Number of Steps: "
    var sensorManager: SensorManager? = null
    var running = false
    private var simpleStepDetector: StepDetector? = null

    private lateinit var dbHelper: DatabaseHelper

    constructor(context: Context?) {
        Log.i(TAG, "AutoStartService: Here we Go!!!!!")
    }

    constructor() {}

    override fun onCreate() {
        super.onCreate()
        val prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", MODE_PRIVATE)
        dbHelper= DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
        stepsWalked = prfs.getString("steps", "0")!!.toInt()
        val notificationIntent = Intent(this, DrawerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
                notificationManager
            ) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setContentText("YOU TOTAL STEPS WALKED $stepsWalked")
            .build()
        startForeground(1001, notification)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector?.registerListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        val channelId = "my_service_channelid"
        val channelName = "My Foreground Service"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        // omitted the LED color
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null && intent.hasExtra("stopped")) {
            stopped = intent.extras!!.getBoolean("stopped",false)
        }
        if (stopped) {
            stop()
            return START_NOT_STICKY
        }
        stoptimertask()
        startTimer(this)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Service is destroyed :( ")
        if (stopped) {
            return
        }
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved: Service is removed :( ")
        if (stopped) {
            return
        }
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    fun startTimer(context: Context?) {
        timer = Timer()

        //initialize the TimerTask's job
        initialiseTimerTask(context)

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    fun initialiseTimerTask(context: Context?) {
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i(TAG, "Timer is running " + counter++)
//                broadcastActionBaz(context, counter.toString())
            }
        }
        running = true
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun stop() {
        stoptimertask()
        stopForeground(true)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector?.updateAccelerometer(
                event.timestamp,
                event.values[0],
                event.values[1],
                event.values[2]
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    override fun step(timeNs: Long) {
        stepsWalked++
//        Toast.makeText(this, "STEPS  $stepsWalked", Toast.LENGTH_SHORT).show()
        broadcastActionBazSteps(this, stepsWalked.toString())
        GlobalScope.launch (Dispatchers.Main) {
            var lat= MySharedPreferences.getMySharedPreferences()!!.latitude
            var lng= MySharedPreferences.getMySharedPreferences()!!.longitude
            var address= MySharedPreferences.getMySharedPreferences()!!.longitude
            dbHelper.insertSteps(Steps(Date(),stepsWalked,address,lat,lng,false))
        }

        /* ADD STEPS IN SHARED PREFERENCE */
        val preferences: SharedPreferences = this.getSharedPreferences(
            "AUTHENTICATION_FILE_NAME",
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putString("steps", stepsWalked.toString())
        editor.apply()
        /* ADD STEPS IN SHARED PREFERENCE */


        val notificationIntent = Intent(this, DrawerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
                notificationManager
            ) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setContentText("YOU TOTAL STEPS WALKED $stepsWalked")
            .build()
        startForeground(1001, notification)
    }

    private fun broadcastActionBazSteps(context: Context?, param: String?) {
        val intent = Intent(ACTION_FOO)
        intent.putExtra(EXTRA_PARAM_B, param)
        val bm = LocalBroadcastManager.getInstance(context!!)
        bm.sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "AutoService"
        const val ACTION_FOO = "${BuildConfig.APPLICATION_ID}.NDDB"

        //        const val EXTRA_PARAM_A = "com.gahlot.neverendingservice.PARAM_A"
        const val EXTRA_PARAM_B = "${BuildConfig.APPLICATION_ID}.PARAM_B"
//        fun broadcastActionBaz(context: Context?, param: String?) {
//            val intent = Intent(ACTION_FOO)
//            intent.putExtra(EXTRA_PARAM_A, param)
//            val bm = LocalBroadcastManager.getInstance(context!!)
//            bm.sendBroadcast(intent)
//        }
    }
}