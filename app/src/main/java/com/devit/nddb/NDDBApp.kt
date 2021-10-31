package com.devit.nddb

import android.app.Application
import com.devit.nddb.utils.AlarmUtils
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class NDDBApp : Application() {

    override fun onCreate() {
        super.onCreate()
        nddbApp = this
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val alarmUtils = AlarmUtils(this)
        alarmUtils.initRepeatingAlarm(calendar)
    }

    companion object {
        lateinit var nddbApp: NDDBApp
            private set
    }
}
