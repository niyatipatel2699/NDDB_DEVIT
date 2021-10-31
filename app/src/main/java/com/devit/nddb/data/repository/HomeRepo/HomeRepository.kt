package com.devit.nddb.data.repository.HomeRepo

import com.devit.nddb.data.remote.responses.StepCountResponse
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.room.entity.Steps
import kotlinx.coroutines.flow.Flow

interface HomeRepository
{
    suspend fun stepCount(stepsList : List<Steps>): Flow<DataState<StepCountResponse>>
}