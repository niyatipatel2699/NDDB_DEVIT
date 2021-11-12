package com.devit.nddb.data.repository.HistoryRepo

import com.devit.nddb.data.remote.responses.History.HistoryResponse
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.devit.nddb.data.repository.Language.LanguageRepository
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.remote.*
import com.wajahatkarim3.imagine.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject


class HistoryRepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: LoginApiService
) : HistoryRepository {
    override suspend fun getWalkHistory(): Flow<DataState<HistoryResponse>> {

        return flow {
            apiService.getWalkHistory().apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
            }.onErrorSuspend {
                emit(DataState.error<HistoryResponse>(message()))
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<HistoryResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<HistoryResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }

}