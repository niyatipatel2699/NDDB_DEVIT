
package com.nddb.kudamforkurien.data.repository.Registration

import com.nddb.kudamforkurien.data.remote.responses.Registration.DistrictResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.RegistrationResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.StateResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.UserTypeResponse
import com.nddb.kudamforkurien.data.DataState
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
