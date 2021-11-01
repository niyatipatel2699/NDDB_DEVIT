package com.wajahatkarim3.imagine.data.room

import com.wajahatkarim3.imagine.data.room.entity.Steps
import java.util.*


interface DatabaseHelper {

    suspend fun getSteps(): List<Steps>

    suspend fun getStepsOnlyNotPass(): List<Steps>

    suspend fun getStep(date: String): Steps

    suspend fun updateSteps(id:Int)

    suspend fun insertSteps(steps:Steps):Long

    suspend fun updatess(id:Int,steps: Int,address:String, lat:String, lng:String)

}