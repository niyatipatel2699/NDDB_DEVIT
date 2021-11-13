package com.nddb.kudamforkurien.data.room

import com.nddb.kudamforkurien.data.room.entity.Steps
import java.util.*


interface DatabaseHelper {

    suspend fun getSteps(): List<Steps>

    suspend fun getStepsOnlyNotPass(): List<Steps>

    suspend fun getStep(date: String): Steps

    suspend fun deleteSteps()

    suspend fun insertSteps(steps:Steps):Long

    suspend fun updateSteps(id:Int,steps: Int,address:String, lat:String, lng:String,ispass:Boolean)

    suspend fun totalSteps():Int

}