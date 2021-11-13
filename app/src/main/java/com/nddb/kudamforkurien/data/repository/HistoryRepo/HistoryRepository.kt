package com.nddb.kudamforkurien.data.repository.HistoryRepo

import com.nddb.kudamforkurien.data.remote.responses.History.HistoryResponse
import com.nddb.kudamforkurien.data.DataState
import kotlinx.coroutines.flow.Flow

interface HistoryRepository
{
    suspend fun getWalkHistory(): Flow<DataState<HistoryResponse>>
}