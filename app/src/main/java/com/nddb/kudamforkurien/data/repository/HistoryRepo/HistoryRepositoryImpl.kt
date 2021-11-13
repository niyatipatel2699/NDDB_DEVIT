package com.nddb.kudamforkurien.data.repository.HistoryRepo

import com.nddb.kudamforkurien.data.remote.responses.History.HistoryResponse
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.remote.*
import com.nddb.kudamforkurien.utils.StringUtils
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