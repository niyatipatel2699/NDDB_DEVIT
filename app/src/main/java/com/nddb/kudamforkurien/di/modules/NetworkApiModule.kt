
package com.nddb.kudamforkurien.di.modules

import com.nddb.kudamforkurien.BuildConfig
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.utils.RestConstant
import com.nddb.kudamforkurien.data.remote.ApiResponseCallAdapterFactory
import com.nddb.kudamforkurien.data.remote.LoginApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * The Dagger Module to provide the instances of [OkHttpClient], [Retrofit], and [WordpressApiService] classes.
 * @author Wajahat Karim
 */

@Module
@InstallIn(SingletonComponent::class)
class NetworkApiModule {


//    val BASE_API_URL = "http://localhost:3000/api/v1/users/"
   // var token : String = MySharedPreferences.getMySharedPreferences()!!.token

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                var newRequest =
                    //request.newBuilder().header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjIxIiwiaWF0IjoxNjM1MTQwMTkwfQ.18JMsiu3vR1dob20eLz4bWNzIYhgkYl8ExwPDi11pKM")
                    request.newBuilder().header("Authorization",MySharedPreferences.getMySharedPreferences()!!.token)
                    //request.newBuilder().header("Authorization",token)
                chain.proceed(newRequest.build())
            }
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RestConstant.BASE_URLS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun providesLoginApiService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }
}
