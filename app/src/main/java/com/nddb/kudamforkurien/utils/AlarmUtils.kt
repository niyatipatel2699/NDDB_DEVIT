package com.nddb.kudamforkurien.utils


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nddb.kudamforkurien.brodcastreceiver.AlarmReceiver
import java.util.*


class AlarmUtils(context: Context) {
    private var mContext = context
    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent

    init {
        alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            // if you want more than one notification use different requestCode
            // every notification need different requestCode
            if(mContext.javaClass.simpleName.equals("NDDBApp"))
            mIntent.putExtra("notify","sendData")

            PendingIntent.getBroadcast(mContext, 100, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun initRepeatingAlarm(calendar: Calendar){

         val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmMgr?.set(AlarmManager.RTC_WAKEUP,   calendar.timeInMillis, alarmIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarmMgr?.setExact(AlarmManager.RTC_WAKEUP,   calendar.timeInMillis, alarmIntent)
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,   calendar.timeInMillis, alarmIntent)
        }
    }

}