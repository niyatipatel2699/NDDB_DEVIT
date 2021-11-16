
package com.nddb.kudamforkurien.data.remote

import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.data.remote.responses.History.HistoryResponse
import com.nddb.kudamforkurien.data.remote.responses.Language.LanguageResponse
import com.nddb.kudamforkurien.data.remote.responses.OtpValidation.OtpResponse
import com.nddb.kudamforkurien.data.remote.responses.RankResponse.RankResponseModel
import com.nddb.kudamforkurien.data.remote.responses.Registration.*
import com.nddb.kudamforkurien.data.remote.responses.StepCountResponse
import com.nddb.kudamforkurien.data.room.entity.Steps
import com.nddb.kudamforkurien.model.DataSteps
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
        @Field("lang_id") lang_id: Int?,
        @Field("device_id") device_id: String=MySharedPreferences.getMySharedPreferences()!!.firebaseToken
    ): ApiResponse<OtpResponse>


    @FormUrlEncoded
    @POST("api/v1/users/otp-validate")
    suspend fun otpValidation(
        @Field("otp") otp: String,
        @Field("phone_number") mobile_number: String
    ): ApiResponse<OtpResponse>

    @FormUrlEncoded
    @POST("api/v1/users//resend-otp")
    suspend fun resendOtp(
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
        @Field("is_Registered") is_registered : Int,
        @Field("lang_id") lang_id : Int=MySharedPreferences.getMySharedPreferences()!!.lang_id
    ) : ApiResponse<RegistrationResponse>


    @FormUrlEncoded
    @POST("api/v1/userLanguage")
    suspend fun updateLanguage(
        @Field("lang_id") lang_id:Int) : ApiResponse<LanguageResponse>


    @FormUrlEncoded
    @POST("api/v1/stepscount/create")
    suspend fun stepCount(
        @Field("data") stepsList : List<DataSteps>,

        ) : ApiResponse<StepCountResponse>


    @GET("api/v1/stepscount/getWalkHistory")
    suspend fun getWalkHistory() : ApiResponse<HistoryResponse>



    @GET("api/v1/stepscount/getRank")
    suspend fun getRank() : ApiResponse<RankResponseModel>

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
