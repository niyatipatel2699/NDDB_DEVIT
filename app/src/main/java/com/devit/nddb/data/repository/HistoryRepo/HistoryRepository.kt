package com.devit.nddb.data.repository.HistoryRepo

import com.devit.nddb.data.remote.responses.History.HistoryResponse
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.wajahatkarim3.imagine.data.DataState
import kotlinx.coroutines.flow.Flow

interface HistoryRepository
{
    suspend fun getWalkHistory(): Flow<DataState<HistoryResponse>>
}