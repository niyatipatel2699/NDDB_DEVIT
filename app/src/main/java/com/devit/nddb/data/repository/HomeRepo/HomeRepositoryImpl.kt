package com.devit.nddb.data.repository.HomeRepo

import com.devit.nddb.data.remote.responses.RankResponse.RankResponseModel
import com.devit.nddb.data.remote.responses.StepCountResponse
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.remote.*
import com.wajahatkarim3.imagine.data.room.entity.Steps
import com.wajahatkarim3.imagine.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: LoginApiService
) : HomeRepository {

    override suspend fun stepCount(
        stepsList : List<Steps>
    ): Flow<DataState<StepCountResponse>> {

        return flow {
            apiService.stepCount(stepsList
            ).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<StepCountResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<StepCountResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<StepCountResponse>(stringUtils.somethingWentWrong()))
                }
            }
        } as Flow<DataState<StepCountResponse>>

    }



    override suspend fun getRank(): Flow<DataState<RankResponseModel>> {
        return flow {
            apiService.getRank().apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<com.devit.nddb.data.remote.responses.RankResponse.RankResponseModel>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<RankResponseModel>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<RankResponseModel>(stringUtils.somethingWentWrong()))
                }
            }
        } as Flow<DataState<RankResponseModel>>

    }
}