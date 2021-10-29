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
package com.wajahatkarim3.imagine.di.modules

import com.devit.nddb.BuildConfig
import com.devit.nddb.utils.RestConstant
import com.wajahatkarim3.imagine.data.remote.ApiResponseCallAdapterFactory
import com.wajahatkarim3.imagine.data.remote.LoginApiService
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
                    request.newBuilder().header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjIxIiwiaWF0IjoxNjM1MTQwMTkwfQ.18JMsiu3vR1dob20eLz4bWNzIYhgkYl8ExwPDi11pKM")
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
