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
package com.wajahatkarim3.imagine.data.repository

import androidx.annotation.WorkerThread
import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.remote.*
import com.wajahatkarim3.imagine.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * This is an implementation of [ImagineRepository] to handle communication with [UnsplashApiService] server.
 * @author Wajahat Karim
 */
class LoginRepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: LoginApiService
) : LoginRepository {

    @WorkerThread
    override suspend fun loginWithOTP(phoneNumber: String,lang_id : Int): Flow<DataState<OtpResponse>> {
        return flow {
            apiService.loginWithOTP(phoneNumber,lang_id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(DataState.error<OtpResponse>(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<OtpResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<OtpResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }


}
