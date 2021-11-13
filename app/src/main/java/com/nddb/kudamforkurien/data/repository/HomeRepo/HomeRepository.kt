package com.nddb.kudamforkurien.data.repository.HomeRepo

import com.nddb.kudamforkurien.data.remote.responses.RankResponse.RankResponseModel
import com.nddb.kudamforkurien.data.remote.responses.StepCountResponse
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.room.entity.Steps
import kotlinx.coroutines.flow.Flow

interface HomeRepository
{
    suspend fun stepCount(stepsList : List<Steps>): Flow<DataState<StepCountResponse>>
    suspend fun getRank(): Flow<DataState<RankResponseModel>>
}