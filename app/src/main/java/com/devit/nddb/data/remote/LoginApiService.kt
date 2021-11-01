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
package com.wajahatkarim3.imagine.data.remote

import com.devit.nddb.data.remote.responses.BaseResponse
import com.devit.nddb.data.remote.responses.Language.LanguageResponse
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.devit.nddb.data.remote.responses.Registration.*
import com.devit.nddb.data.remote.responses.StepCountResponse
import com.google.android.gms.common.api.Api
import com.wajahatkarim3.imagine.data.room.entity.Steps
import retrofit2.http.*

interface LoginApiService {

  /*  @FormUrlEncoded
    @POST("api/v1/users/login-with-mobile-no")
    suspend fun loginWithOTP(
        @Field("phone_number") mobileNumber: String,
        @Field("lang_id") lang_id: Int?
    ): ApiResponse<BaseResponse>
*/

    @FormUrlEncoded
    @POST("api/v1/users/login-with-mobile-no")
    suspend fun loginWithOTP(
        @Field("phone_number") mobileNumber: String,
        @Field("lang_id") lang_id: Int?
    ): ApiResponse<OtpResponse>


    @FormUrlEncoded
    @POST("api/v1/users/otp-validate")
    suspend fun otpValidation(
        @Field("otp") otp: String,
        @Field("phone_number") mobile_number: String
    ): ApiResponse<OtpResponse>

    @GET("api/v1/languages/getLanguage")
    suspend fun getLanguage() : ApiResponse<LanguageResponse>

    @GET("api/v1/userType/getUserType")
    suspend fun getUserType() : ApiResponse<UserTypeResponse>

    @GET("api/v1/state/getState")
    suspend fun getState() : ApiResponse<StateResponse>

    @GET("api/v1/district/getDistrict/{stateId}")
    suspend fun getDistrict(@Path("stateId")  stateId:Int) : ApiResponse<DistrictResponse>

    @FormUrlEncoded
    @POST("api/v1/users/update-profile")
    suspend fun registerUser(
        @Field("first_name") first_name : String,
        @Field("last_name") last_name : String,
        @Field("user_type") user_type : String,
        @Field("state") state : String,
        @Field("district") district : String,
        @Field("gender") gender : String,
        @Field("phone_number") phone_number : String,
        @Field("is_Registered") is_registered : Int
    ) : ApiResponse<RegistrationResponse>


    @FormUrlEncoded
    @POST("api/v1/userLanguage")
    suspend fun updateLanguage(
        @Field("lang_id") lang_id:Int) : ApiResponse<LanguageResponse>


    @FormUrlEncoded
    @POST("api/v1/stepscount/create")
    suspend fun stepCount(
        @Field("data") stepsList : List<Steps>,

        ) : ApiResponse<StepCountResponse>



//    @GET("photos")
//    suspend fun loadPhotos(
//        @Query("page") page: Int = 1,
//        @Query("per_page") numOfPhotos: Int = 10,
//        @Query("order_by") orderBy: String = "popular"
//    ): ApiResponse<BaseResponse>
//
//    @GET("search/photos")
//    suspend fun searchPhotos(
//        @Query("query") query: String,
//        @Query("page") page: Int = 1,
//        @Query("per_page") numOfPhotos: Int = 10,
//    ): ApiResponse<BaseResponse>

/*    companion object {
        const val BASE_API_URL = "http://localhost:3000/api/v1/users/"
    }*/
}
