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

import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.devit.nddb.data.remote.responses.Registration.DistrictResponse
import com.devit.nddb.data.remote.responses.Registration.RegistrationResponse
import com.devit.nddb.data.remote.responses.Registration.StateResponse
import com.devit.nddb.data.remote.responses.Registration.UserTypeResponse
import com.devit.nddb.data.remote.responses.StepCountResponse
import com.wajahatkarim3.imagine.data.DataState
import com.wajahatkarim3.imagine.data.room.entity.Steps
import kotlinx.coroutines.flow.Flow

/**
 * ImagineRepository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [ImagineRepositoryImpl] for implementation of this class to utilize Unsplash API.
 * @author Wajahat Karim
 */
interface RegistrationRepository {
    suspend fun getUserType(): Flow<DataState<UserTypeResponse>>
    suspend fun getState(): Flow<DataState<StateResponse>>
    suspend fun getDistrict(stateId: Int): Flow<DataState<DistrictResponse>>
    suspend fun registerUser(
        fname: String,
        lname: String,
        userType: String,
        state: String,
        district: String,
        gender: String,
        phone_number: String,
        isRegistered: Int
    ): Flow<DataState<RegistrationResponse>>
//    suspend fun searchPhotos(query: String, pageNumber: Int, pageSize: Int): Flow<DataState<List<PhotoModel>>>
}
