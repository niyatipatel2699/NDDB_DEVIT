package com.nddb.kudamforkurien.brodcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.data.remote.LoginApiService
import com.nddb.kudamforkurien.data.repository.HomeRepo.HomeRepository
import com.nddb.kudamforkurien.data.repository.HomeRepo.HomeRepositoryImpl
import com.nddb.kudamforkurien.data.room.DatabaseBuilder
import com.nddb.kudamforkurien.data.room.DatabaseHelper
import com.nddb.kudamforkurien.data.room.DatabaseHelperImpl
import com.nddb.kudamforkurien.model.DataSteps
import com.nddb.kudamforkurien.utils.AlarmUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver(){
    @set:Inject
    lateinit var apiService: LoginApiService

    private lateinit var dbHelper: DatabaseHelper
    var context:Context?=null
     override fun onReceive(context: Context, intent: Intent) {
      this.context=context

        dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(context))
        sendFitDataToServer()
        var nextTime= Calendar.getInstance()
        nextTime.add(Calendar.MINUTE, 10);
        val alarmUtils = AlarmUtils(context)
        alarmUtils.initRepeatingAlarm(nextTime)
    }

    fun sendFitDataToServer() {
        var steps=MySharedPreferences.getMySharedPreferences()!!.keySteps
        //var sdf = SimpleDateFormat("dd MMM yyyy")
        var sdf = SimpleDateFormat("dd MMM yyyy" , Locale.US)
        var currentDate = sdf.format(Date())
        var location= MySharedPreferences.getMySharedPreferences()!!.district
        var tempList: ArrayList<DataSteps> = ArrayList()
        var dataSteps = DataSteps(currentDate, steps, location)
        tempList.add(dataSteps)
        val jsonObject = JsonObject()
        val toJson = Gson().toJsonTree(tempList) //Only one line to covert array JsonElement
        jsonObject.add("data", toJson)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.stepCount(jsonObject)
        }

    }
}