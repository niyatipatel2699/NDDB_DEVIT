package com.nddb.kudamforkurien.data.room

import com.nddb.kudamforkurien.data.room.entity.Steps
import java.util.*

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun getSteps(): List<Steps> = appDatabase.stepsDao().getSteps()

    override suspend fun getStepsOnlyNotPass(): List<Steps> = appDatabase.stepsDao().getStepsOnlyNotPass()
    override suspend fun getStep(date: String): Steps = appDatabase.stepsDao().getStep(date)

    override suspend fun deleteSteps() = appDatabase.stepsDao().deleteSteps()

   /* override suspend fun updateSteps(id: Int) = appDatabase.stepsDao().updateItem(id)*/

    override suspend fun insertSteps(steps: Steps):Long = appDatabase.stepsDao().insert(steps)

    override suspend fun updateSteps(id: Int,steps: Int,address:String, lat:String, lng:String,ispass:Boolean)= appDatabase.stepsDao().updateStep(id,steps,address,lat,lng,ispass)
    override suspend fun totalSteps() = appDatabase.stepsDao().getTotalSteps()

    override suspend fun getLastRow(): Steps = appDatabase.stepsDao().getLastStep()


}