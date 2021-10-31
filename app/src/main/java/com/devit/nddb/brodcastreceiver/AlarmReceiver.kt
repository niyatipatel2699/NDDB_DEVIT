package com.devit.nddb.brodcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devit.nddb.data.repository.HomeRepo.HomeRepositoryImpl
import com.devit.nddb.utils.AlarmUtils
import java.util.*

class AlarmReceiver: BroadcastReceiver(){

   // lateinit var homeRepositoryImpl: HomeRepositoryImpl

    var context:Context?=null
     override fun onReceive(context: Context, intent: Intent) {
        this.context=context

        // homeRepositoryImpl.stepCount()

        var nextTime= Calendar.getInstance()
        nextTime.add(Calendar.DATE, 1);
        val alarmUtils = AlarmUtils(context)
        alarmUtils.initRepeatingAlarm(nextTime)
    }
}