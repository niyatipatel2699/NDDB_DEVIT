package com.nddb.kudamforkurien.backgroundservice


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.util.Log
import android.util.SparseArray
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nddb.kudamforkurien.Activity.DrawerActivity
import com.nddb.kudamforkurien.Activity.ui.event.EventFragment
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.room.DatabaseBuilder
import com.nddb.kudamforkurien.data.room.DatabaseHelper
import com.nddb.kudamforkurien.data.room.DatabaseHelperImpl
import com.nddb.kudamforkurien.utils.StepDetector
import java.util.*
import java.util.concurrent.TimeUnit


/**
 *
 *
 * Created by sinku on 10.11.2021.
 */
internal class MotionService : Service(), SensorEventListener {
    //private lateinit var sharedPreferences: SharedPreferences

    // steps at the current day
    private var mTodaysSteps: Int = 0

    // steps reported from sensor
    private var mCurrentSteps: Int = 0

    // steps reported from sensor STEP_COUNTER in previous event
    private var mLastSteps = -1

    // current date of counting
    private var mCurrentDate: Long = 0
    private var receiver: ResultReceiver? = null
    private lateinit var stepDetector: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var simpleStepDetector: StepDetector
    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mBuilder: NotificationCompat.Builder
    private var motionActivities: SparseArray<MotionActivity> = SparseArray()
    private var motionActivityId = 0

    private var mHandler: Handler? = null

    private var timeInSeconds = 0L

    var TIMER_INTERVAL = 1000

    var stopped = false

    private lateinit var dbHelper: DatabaseHelper

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "Creating MotionService")
        dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
      //  sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        startService()



        // get last saved date
        mCurrentDate =MySharedPreferences.getMySharedPreferences()!!.keyDate

        // get last steps
        mTodaysSteps = MySharedPreferences.getMySharedPreferences()!!.keySteps

        val manager = packageManager

        // connect sensor
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        /*if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        } else {

        }*/

        stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        simpleStepDetector = StepDetector(object : StepDetector.StepListener {
            override fun step(timeNs: Long) {
                Log.d(TAG, "using fallback sensor accelerometer" + mCurrentSteps + 1)
                handleEvent(mCurrentSteps + 1)
            }
        })

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            // androids built in step counter
            Log.e(TAG, "using sensor step counter")
            mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            mListener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent) {
                    Log.e(TAG, "using sensor step counter"+event.values[0].toInt())
                    handleEvent(event.values[0].toInt())
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // no-op
                }
            }
        }  else if (manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            // fallback sensor
            Log.e(TAG, "using fallback sensor accelerometer")
            mStepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            //simpleStepDetector = StepDetector(object : StepDetector.listener {
            simpleStepDetector = StepDetector(object : StepDetector.StepListener {
                override fun step(timeNs: Long) {
                    Log.d(TAG, "using fallback sensor accelerometer"+mCurrentSteps + 1)
                    handleEvent(mCurrentSteps + 1)
                }
            })
            mListener = object : SensorEventListener {

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        simpleStepDetector.updateAccel(
                            event.timestamp, event.values[0], event.values[1], event.values[2])
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // no-op
                }
            }
        }*/

        mSensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST)

    }

    private fun handleEvent(value: Int) {
        Log.e("each step before cal", value.toString())
        mCurrentSteps = value
        if (mLastSteps == -1) {
            mLastSteps = value
        }
        mTodaysSteps += value - mLastSteps
        mLastSteps = value
        Log.e("each step after cal", mTodaysSteps.toString())
        handleEvent()
    }

    private fun handleEvent() {
        // Check if new day started
        MySharedPreferences.getMySharedPreferences()!!.keySteps = mTodaysSteps
       /* GlobalScope.launch(Dispatchers.Main) {
            var lat = MySharedPreferences.getMySharedPreferences()!!.latitude
            var lng = MySharedPreferences.getMySharedPreferences()!!.longitude
            var address = MySharedPreferences.getMySharedPreferences()!!.district
            val currentDate = Converters.FORMATTER.format(Date())
            var step = dbHelper.getStep(currentDate)
            if (step != null) {
               // sharedPreferences.edit().putInt(KEY_STEPS, mTodaysSteps).apply()
                MySharedPreferences.getMySharedPreferences()!!.keySteps = mTodaysSteps
                dbHelper.updateSteps(step.id, mTodaysSteps, address, lat, lng, false)
            } else {
                mTodaysSteps = 0
                mCurrentDate = com.nddb.kudamforkurien.utils.Util.calendar.timeInMillis
                //sharedPreferences.edit().putLong(KEY_DATE, mCurrentDate).apply()
                MySharedPreferences.getMySharedPreferences()!!.keyDate = mCurrentDate
                dbHelper.insertSteps(Steps(currentDate, mTodaysSteps, address, lat, lng, false))
            }
        }*/

        /* if (!DateUtils.isToday(mCurrentDate)) {

             //(timestamp long primary key, isPass TEXT CHECK( isPass IN ('1','0') )   NOT NULL DEFAULT '0', steps int not null);"
             // Add record for the day to the database
             var lat= MySharedPreferences.getMySharedPreferences()!!.latitude
             var lng= MySharedPreferences.getMySharedPreferences()!!.longitude
             var address= MySharedPreferences.getMySharedPreferences()!!.longitude

             Database.getInstance(this).addEntry(mCurrentDate, mTodaysSteps,lat,lng,address)

             // Start counting for the new day
             mTodaysSteps = 0
             mCurrentDate = com.devit.nddb.utils.Util.calendar.timeInMillis
             sharedPreferences.edit().putLong(KEY_DATE, mCurrentDate).apply()
         }
         sharedPreferences.edit().putInt(KEY_STEPS, mTodaysSteps).apply()
         for (i in 0 until motionActivities.size()) {
             motionActivities.valueAt(i).update(mCurrentSteps)
         }*/
        Log.e("before sending to home", "" + mTodaysSteps)
        sendUpdate()
    }

    private fun sendUpdate() {
        mBuilder.setContentText(
            String.format(
                Locale.getDefault(),
                "YOU TOTAL STEPS WALKED %d"/* com.devit.nddb.utils.Util.stepsToMeters(mTodaysSteps)*/,
                mTodaysSteps
            )
        )
        mNotificationManager.notify(FOREGROUND_ID, mBuilder.build())
        Log.e("sensor", "sendUpdate")
        /*receiver?.let {
            val bundle = Bundle()
            bundle.putInt(KEY_STEPS, mTodaysSteps)
            *//* val activities = ArrayList<Bundle>()
             for (i in 0 until motionActivities.size()) {
                 val motionActivity = motionActivities.valueAt(i)
                 val activityBundle = Bundle()
                 activityBundle.putInt(KEY_ID, motionActivity.id)
                 activityBundle.putInt(KEY_STEPS, motionActivity.steps)
                 activityBundle.putBoolean(KEY_ACTIVE, motionActivity.active)
                 activities.add(activityBundle)
             }
             bundle.putParcelableArrayList(KEY_ACTIVITIES, activities)*//*
            Log.e("sensor", "sendUpdate1")
            it.send(0, bundle)
        }*/
        val intent = Intent("Motion-Service")
        intent.putExtra(KEY_STEPS, mTodaysSteps)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Received start id $startId: $intent")

        if (intent != null && intent.hasExtra("stopped")) {
            stopped = intent.extras!!.getBoolean("stopped", false)
        }
        if (stopped) {
            stopForeground(true)
            stopSelf()
            //timer reset
            stopTimer()
            return START_NOT_STICKY
        }

        if (intent != null) {
            when {
                ACTION_SUBSCRIBE == intent.action -> receiver =
                    intent.getParcelableExtra(EventFragment.TAG)
                ACTION_START_ACTIVITY == intent.action -> {
                    val id = motionActivityId++
                    motionActivities.put(id, MotionActivity(id, mCurrentSteps))
                }
                ACTION_STOP_ACTIVITY == intent.action -> motionActivities.remove(
                    intent.getIntExtra(
                        KEY_ID,
                        0
                    )
                )
                ACTION_TOGGLE_ACTIVITY == intent.action -> motionActivities.get(
                    intent.getIntExtra(
                        KEY_ID,
                        0
                    )
                ).toggle()
            }
            sendUpdate()
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY
    }

    private fun startService() {
        //sharedPreferences.edit().putInt(KEY_STEPS, 0).apply()
       // MySharedPreferences.getMySharedPreferences()!!.keySteps = 0
       // mTodaysSteps = 0
        startTimer()
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: throw IllegalStateException("could not get notification service")
        val notificationIntent = Intent(this, DrawerActivity::class.java)
        notificationIntent.putExtra( "open" , "event" ) ;
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentIntent(pendingIntent)
        startForeground(FOREGROUND_ID, mBuilder.build())
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_NONE
            )

            notificationChannel.description = "steps"

            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {

        private val TAG = MotionService::class.java.simpleName
        internal const val ACTION_SUBSCRIBE = "ACTION_SUBSCRIBE"
        internal const val ACTION_START_ACTIVITY = "ACTION_START_ACTIVITY"
        internal const val ACTION_STOP_ACTIVITY = "ACTION_STOP_ACTIVITY"
        internal const val ACTION_TOGGLE_ACTIVITY = "ACTION_TOGGLE_ACTIVITY"
        internal const val KEY_ID = "ID"
        internal const val KEY_STEPS = "STEPS"
        internal const val KEY_ACTIVE = "ACTIVE"
        internal const val KEY_ACTIVITIES = "ACTIVITIES"
        internal const val KEY_DATE = "DATE"
        private const val FOREGROUND_ID = 3843
        internal const val KEY_TIMER = "TIMER"
        const val CHANNEL_ID = "com.tiefensuche.motionmate.CHANNEL_ID"
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor!!.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                p0.timestamp, p0.values[0], p0.values[1], p0.values[2])
        } else if (p0?.sensor == stepDetector) {
            mTodaysSteps = (p0.values[0].toInt())
            handleEvent()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }


    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                Log.e("timeInSeconds", timeInSeconds.toString())
                updateStopWatchView(timeInSeconds)
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler!!.postDelayed(this, TIMER_INTERVAL.toLong())
            }
        }
    }


    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        Log.e("formattedTime", formattedTime)
        val intent = Intent("Motion-Service")
        intent.putExtra(KEY_TIMER, formattedTime)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        /*receiver?.let {
            val bundle = Bundle()
            bundle.putString(KEY_TIMER, formattedTime)
            it.send(1, bundle)
        }*/
       // binding?.textViewStopWatch?.text = formattedTime
    }


    fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager.unregisterListener(this, stepDetector)
    }
}