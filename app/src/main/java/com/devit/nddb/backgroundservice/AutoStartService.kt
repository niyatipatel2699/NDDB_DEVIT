package com.devit.nddb.backgroundservice

import android.app.*
import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.ResultReceiver
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.BuildConfig
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.utils.Converters.Companion.FORMATTER
import com.devit.nddb.utils.StepDetector
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.tasks.Task
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
    private var stepsTaken = 0
    private var reportedSteps = 0
    private val stepDetector = 0

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var myResultReceiver: ResultReceiver? = null
    var stopped = false
    var TEXT_NUM_STEPS = "Number of Steps: "
    var sensorManager: SensorManager? = null
    var running = false
    private var simpleStepDetector: StepDetector? = null

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var mIntentService: Intent
    private lateinit var mPendingIntent: PendingIntent
    private lateinit var mActivityRecognitionClient: ActivityRecognitionClient
    private lateinit var broadcastReceiver: BroadcastReceiver

    var iswal = false

    var actvityType :Int=0
    var confidence :Int=0

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
        /*simpleStepDetector = StepDetector()
        simpleStepDetector?.registerListener(this)*/

        simpleStepDetector = StepDetector(object : StepDetector.StepListener {
            override fun step(timeNs: Long) {
                //handleEvent(mCurrentSteps + 1)
            }
        })



        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, RecognizedActivitiesService::class.java)
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT)
        requestActivityUpdatesButtonHandler()

        broadcastReceiver  = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == RecognizedActivitiesService.BROADCAST_ACTIVITY_RECOGNITION) {
                    //true
                  /*  addStep()*/
                   actvityType = intent.getIntExtra("type", -1)
                    confidence = intent.getIntExtra("confidence", 0)
                    handleUserActivity(actvityType, confidence)
                    //handleUserActivity()addStep()
                  /*  val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    //handleUserActivity(type, confidence)
                    Log.e("123",type.toString())
                    Log.e("123111",confidence.toString())*/
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(RecognizedActivitiesService.BROADCAST_ACTIVITY_RECOGNITION))
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
        //activityIntent=intent!!
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
        instance = null
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
        removeActivityUpdatesButtonHandler()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved: Service is removed :( ")
        if (stopped) {
            return
        }
        instance = null
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
      /*  senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        senStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);*/

        running = true
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )

        /*sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL);*/
    }

    fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    fun stop() {
        instance= null
        stoptimertask()
        stopForeground(true)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            simpleStepDetector?.updateAccel(
                event.timestamp,
                event.values[0],
                event.values[1],
                event.values[2]
            )


        }


    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}


    fun addStep()
    {
        stepsWalked++
        broadcastActionBazSteps(this, stepsWalked.toString())
        GlobalScope.launch (Dispatchers.Main) {
            var lat= MySharedPreferences.getMySharedPreferences()!!.latitude
            var lng= MySharedPreferences.getMySharedPreferences()!!.longitude
            var address= MySharedPreferences.getMySharedPreferences()!!.longitude
            val currentDate = FORMATTER.format(Date())
            var step=dbHelper.getStep(currentDate)
            if(step!=null)
            {
                dbHelper.updateSteps(step.id,stepsWalked,address, lat, lng,step.ispass)
            }
            else
            {
                dbHelper.insertSteps(Steps(currentDate, stepsWalked, address, lat, lng, false))
            }
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


    override fun step(timeNs: Long) {
        //addStep()
       /* if(iswal)
        {
            addStep()
        }*/



       /* if (ActivityRecognitionResult.hasResult(activityIntent)) {
            val result = ActivityRecognitionResult.extractResult(activityIntent)
            //handleDetectedActivities()
            for (activity in result.probableActivities) {
                if (activity.type == DetectedActivity.RUNNING || activity.type == DetectedActivity.WALKING) {
                    addStep()
                }
            }
        }*/
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


        private var instance:AutoStartService? = null
        fun getInstance(): AutoStartService? {
            return instance
        }

//        fun broadcastActionBaz(context: Context?, param: String?) {
//            val intent = Intent(ACTION_FOO)
//            intent.putExtra(EXTRA_PARAM_A, param)
//            val bm = LocalBroadcastManager.getInstance(context!!)
//            bm.sendBroadcast(intent)
//        }
    }

    fun requestActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient?.requestActivityUpdates(
            RecognizedActivitiesService.DETECTION_INTERVAL_IN_MILLISECONDS,
            mPendingIntent)

        task?.addOnSuccessListener {
            /*Toast.makeText(applicationContext,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT)
                .show()*/
        }

        task?.addOnFailureListener {
            /*Toast.makeText(applicationContext,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT)
                .show()*/
        }
    }

    fun removeActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient?.removeActivityUpdates(
            mPendingIntent)
        task?.addOnSuccessListener {
          /*  Toast.makeText(applicationContext,
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT)
                .show()*/
        }

        task?.addOnFailureListener {
            /*Toast.makeText(applicationContext, "Failed to remove activity updates!",
                Toast.LENGTH_SHORT).show()*/
        }
    }

    private fun handleUserActivity(type: Int, confidence: Int) {
        var activityType = "Activity Unknown"

        when (type) {
            DetectedActivity.STILL -> {
                activityType = "Still"
                iswal=false
            }
            DetectedActivity.ON_FOOT -> {
                activityType = "On Foot"
                iswal=true
            }
            DetectedActivity.WALKING -> {
                activityType = "Walking"
                iswal=true
            }
            DetectedActivity.RUNNING -> {
                activityType = "Running"
                iswal=true
            }
            DetectedActivity.IN_VEHICLE -> {
                iswal=false
            }
            DetectedActivity.ON_BICYCLE -> {
                iswal=false
            }
            DetectedActivity.TILTING -> {
                iswal=false
            }
            DetectedActivity.UNKNOWN -> {
                iswal=false
            }
        }

        Log.e("Activity Type:", activityType)
        Log.e("Activity Confidence:", confidence.toString())



        if (confidence > 70 ) {

           /* txt_type?.text = activityType
            txt_confidence?.text = "Confidence: " + confidence*/
        }
    }
}