
package com.nddb.kudamforkurien.data.repository.Language

import com.nddb.kudamforkurien.data.remote.responses.Language.LanguageResponse
import com.nddb.kudamforkurien.data.DataState
import kotlinx.coroutines.flow.Flow

/**
 * ImagineRepository is an interface data layer to handle communication with any data source such as Server or local database.
 * @see [ImagineRepositoryImpl] for implementation of this class to utilize Unsplash API.
 * @author Wajahat Karim
 */
interface LanguageRepository {
    suspend fun getLanguage(): Flow<DataState<LanguageResponse>>
    suspend fun updateLanguage(lang_id : Int) : Flow<DataState<LanguageResponse>>
//    suspend fun searchPhotos(query: String, pageNumber: Int, pageSize: Int): Flow<DataState<List<PhotoModel>>>
}
