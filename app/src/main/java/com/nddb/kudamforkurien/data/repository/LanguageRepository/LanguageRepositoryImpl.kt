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
package com.nddb.kudamforkurien.data.repository.Language

import com.nddb.kudamforkurien.data.remote.responses.Language.LanguageResponse
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

class LanguageRepositoryImpl @Inject constructor(
    private val stringUtils: StringUtils,
    private val apiService: LoginApiService
) : LanguageRepository {
    override suspend fun getLanguage(): Flow<DataState<LanguageResponse>> {

        return flow {
            apiService.getLanguage().apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
            }.onErrorSuspend {
                emit(DataState.error<LanguageResponse>(message()))
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<LanguageResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<LanguageResponse>(stringUtils.somethingWentWrong()))
                }
            }
        }
    }

    override suspend fun updateLanguage(lang_id: Int): Flow<DataState<LanguageResponse>> {

        return flow {
            apiService.updateLanguage(lang_id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(DataState.success(it))
                    }
                }
            }.onErrorSuspend {
                emit(DataState.error<LanguageResponse>(message()))
            }.onExceptionSuspend {
                if (this.exception is IOException) {
                    emit(DataState.error<LanguageResponse>(stringUtils.noNetworkErrorMessage()))
                } else {
                    emit(DataState.error<LanguageResponse>(stringUtils.somethingWentWrong()))
                }
            }
            }
        }
    }