
package com.nddb.kudamforkurien.data.repository

import androidx.annotation.WorkerThread
import com.nddb.kudamforkurien.data.remote.responses.OtpValidation.OtpResponse
import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.remote.*
import com.nddb.kudamforkurien.utils.StringUtils
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


    @WorkerThread
    override suspend fun resendOTP(phoneNumber: String): Flow<DataState<OtpResponse>> {
        return flow {
            apiService.resendOtp(phoneNumber).apply {
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
