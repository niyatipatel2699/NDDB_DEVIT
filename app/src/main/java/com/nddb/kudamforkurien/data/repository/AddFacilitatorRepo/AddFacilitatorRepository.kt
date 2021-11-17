package com.nddb.kudamforkurien.data.repository.AddFacilitatorRepo

import com.nddb.kudamforkurien.data.DataState
import com.nddb.kudamforkurien.data.remote.responses.BaseResponse
import kotlinx.coroutines.flow.Flow


interface AddFacilitatorRepository {

    suspend fun addFacilitator(no_of_people:String,location:String) : Flow<DataState<BaseResponse>>
}