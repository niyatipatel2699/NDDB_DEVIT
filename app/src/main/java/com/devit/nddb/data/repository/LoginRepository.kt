
package com.wajahatkarim3.imagine.data.repository

import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.wajahatkarim3.imagine.data.DataState
import kotlinx.coroutines.flow.Flow

/**
 * ImagineRepository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [ImagineRepositoryImpl] for implementation of this class to utilize Unsplash API.
 * @author Wajahat Karim
 */
interface LoginRepository {
    //suspend fun loginWithOTP(phoneNumber: String,lang_id : Int): Flow<DataState<BaseResponse>>
    suspend fun loginWithOTP(phoneNumber: String,lang_id : Int): Flow<DataState<OtpResponse>>
//    suspend fun searchPhotos(query: String, pageNumber: Int, pageSize: Int): Flow<DataState<List<PhotoModel>>>
}
