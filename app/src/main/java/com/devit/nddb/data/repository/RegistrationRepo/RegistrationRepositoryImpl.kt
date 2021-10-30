/*
* Copyright 2021 Wajahat Karim (https://wajahatkarim.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.devit.nddb.data.repository.Registration

import androidx.annotation.WorkerThread
import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.devit.nddb.data.remote.responses.Registration.DistrictResponse
import com.devit.nddb.data.remote.responses.Registration.RegistrationResponse
import com.devit.nddb.data.remote.responses.Registration.StateResponse
import com.devit.nddb.data.remote.responses.Registration.UserTypeResponse
import com.devit.nddb.data.remote.responses.StepCountResponse
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.remote.*
import com.wajahatkarim3.imagine.data.room.entity.Steps
import com.wajahatkarim3.imagine.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * This is an implementation of [ImagineRepository] to handle communication with [UnsplashApiService] server.
 * @author Wajahat Karim
 */
class RegistrationRepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: LoginApiService
) : RegistrationRepository {


    override suspend fun getUserType(): Flow<DataState<UserTypeResponse>> {
        return flow {
            apiService.getUserType().apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<UserTypeResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<UserTypeResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<UserTypeResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }

    override suspend fun getState(): Flow<DataState<StateResponse>> {

        return flow {
            apiService.getState().apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<StateResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<StateResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<StateResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }

    override suspend fun getDistrict(stateId: Int): Flow<DataState<DistrictResponse>> {

        return flow {
            apiService.getDistrict(stateId).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<DistrictResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<DistrictResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<DistrictResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }

    override suspend fun registerUser(
        fname: String,
        lname: String,
        userType: String,
        state: String,
        district: String,
        gender: String,
        phone_number: String,
        isRegistered: Int
    ): Flow<DataState<RegistrationResponse>> {

        return flow {
            apiService.registerUser(fname,
                lname,
                userType,
                state,
                district,
                gender,
                phone_number,
                isRegistered).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<RegistrationResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<RegistrationResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<RegistrationResponse>(stringUtils.somethingWentWrong()))
                }
            }
        } as Flow<DataState<RegistrationResponse>>

    }


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
}
