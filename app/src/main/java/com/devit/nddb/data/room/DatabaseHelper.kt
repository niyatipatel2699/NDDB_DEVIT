package com.wajahatkarim3.imagine.data.room

import com.wajahatkarim3.imagine.data.room.entity.Steps


interface DatabaseHelper {

    suspend fun getSteps(): List<Steps>

    suspend fun insertSteps(steps:Steps)

}