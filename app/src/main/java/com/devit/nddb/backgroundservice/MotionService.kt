package com.devit.nddb.backgroundservice


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import android.text.format.DateUtils
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.bumptech.glide.util.Util
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.Activity.ui.home.HomeFragment
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.utils.Converters

import com.devit.nddb.utils.StepDetector
import com.wajahatkarim3.imagine.data.room.DatabaseBuilder
import com.wajahatkarim3.imagine.data.room.DatabaseHelper
import com.wajahatkarim3.imagine.data.room.DatabaseHelperImpl
import com.wajahatkarim3.imagine.data.room.entity.Steps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


/**
 *
 *
 * Created by sinku on 10.11.2021.
 */
internal class MotionService : Service(), SensorEventListener {
    private lateinit var sharedPreferences: SharedPreferences

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
    var stopped = false

    private lateinit var dbHelper: DatabaseHelper

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "Creating MotionService")
        dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
        startService()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // get last saved date
        mCurrentDate =
            sharedPreferences.getLong(KEY_DATE, com.devit.nddb.utils.Util.calendar.timeInMillis)
        // get last steps
        mTodaysSteps = sharedPreferences.getInt(KEY_STEPS, 0)

        val manager = packageManager

        // connect sensor
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        } else {
            stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            simpleStepDetector = StepDetector(object : StepDetector.StepListener {
                override fun step(timeNs: Long) {
                    Log.d(TAG, "using fallback sensor accelerometer" + mCurrentSteps + 1)
                    handleEvent(mCurrentSteps + 1)
                }
            })
        }

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

        GlobalScope.launch(Dispatchers.Main) {
            var lat = MySharedPreferences.getMySharedPreferences()!!.latitude
            var lng = MySharedPreferences.getMySharedPreferences()!!.longitude
            var address = MySharedPreferences.getMySharedPreferences()!!.longitude
            val currentDate = Converters.FORMATTER.format(Date())
            var step = dbHelper.getStep(currentDate)
            if (step != null) {
                sharedPreferences.edit().putInt(KEY_STEPS, mTodaysSteps).apply()
                dbHelper.updateSteps(step.id, mTodaysSteps, address, lat, lng, step.ispass)
            } else {
                mTodaysSteps = 0
                mCurrentDate = com.devit.nddb.utils.Util.calendar.timeInMillis
                sharedPreferences.edit().putLong(KEY_DATE, mCurrentDate).apply()
                dbHelper.insertSteps(Steps(currentDate, mTodaysSteps, address, lat, lng, false))
            }
        }

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
        receiver?.let {
            val bundle = Bundle()
            bundle.putInt(KEY_STEPS, mTodaysSteps)
            /* val activities = ArrayList<Bundle>()
             for (i in 0 until motionActivities.size()) {
                 val motionActivity = motionActivities.valueAt(i)
                 val activityBundle = Bundle()
                 activityBundle.putInt(KEY_ID, motionActivity.id)
                 activityBundle.putInt(KEY_STEPS, motionActivity.steps)
                 activityBundle.putBoolean(KEY_ACTIVE, motionActivity.active)
                 activities.add(activityBundle)
             }
             bundle.putParcelableArrayList(KEY_ACTIVITIES, activities)*/
            Log.e("sensor", "sendUpdate1")
            it.send(0, bundle)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Received start id $startId: $intent")

        if (intent != null && intent.hasExtra("stopped")) {
            stopped = intent.extras!!.getBoolean("stopped", false)
        }
        if (stopped) {
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent != null) {
            when {
                ACTION_SUBSCRIBE == intent.action -> receiver =
                    intent.getParcelableExtra(HomeFragment.TAG)
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
        mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: throw IllegalStateException("could not get notification service")
        val notificationIntent = Intent(this, DrawerActivity::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager.unregisterListener(this, stepDetector)
    }
}