
package com.nddb.kudamforkurien.data.repository

import com.nddb.kudamforkurien.data.remote.responses.OtpValidation.OtpResponse
import com.nddb.kudamforkurien.data.DataState
import kotlinx.coroutines.flow.Flow

/**
 * ImagineRepository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [ImagineRepositoryImpl] for implementation of this class to utilize Unsplash API.
 * @author Wajahat Karim
 */
interface LoginRepository {
    //suspend fun loginWithOTP(phoneNumber: String,lang_id : Int): Flow<DataState<BaseResponse>>
    suspend fun loginWithOTP(phoneNumber: String,lang_id : Int): Flow<DataState<OtpResponse>>
    suspend fun resendOTP(phoneNumber: String): Flow<DataState<OtpResponse>>

//    suspend fun searchPhotos(query: String, pageNumber: Int, pageSize: Int): Flow<DataState<List<PhotoModel>>>
}
