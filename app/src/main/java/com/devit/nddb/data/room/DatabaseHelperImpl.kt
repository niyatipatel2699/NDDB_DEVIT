package com.wajahatkarim3.imagine.data.room

import com.wajahatkarim3.imagine.data.room.entity.Steps

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun getSteps(): List<Steps> = appDatabase.stepsDao().getSteps()

    override suspend fun getStepsOnlyNotPass(): List<Steps> = appDatabase.stepsDao().getSteps()

    override suspend fun updateSteps(id: Int) = appDatabase.stepsDao().updateItem(id)




    override suspend fun insertSteps(steps: Steps) = appDatabase.stepsDao().insert(steps)

}