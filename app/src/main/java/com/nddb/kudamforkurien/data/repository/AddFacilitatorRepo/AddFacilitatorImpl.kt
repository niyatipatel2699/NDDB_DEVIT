package com.nddb.kudamforkurien.data.repository.AddFacilitatorRepo

import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.remote.*
import com.nddb.kudamforkurien.data.remote.responses.BaseResponse
import com.nddb.kudamforkurien.data.remote.responses.History.HistoryResponse
import com.nddb.kudamforkurien.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class AddFacilitatorImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService : LoginApiService
) : AddFacilitatorRepository{
    override suspend fun addFacilitator(
        no_of_people: String,
        location: String
    ): Flow<DataState<BaseResponse>> {

        return flow {
            apiService.addFacilitator(no_of_people,location).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
            }.onErrorSuspend {
                emit(DataState.error<BaseResponse>(message()))
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<BaseResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<BaseResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }
}