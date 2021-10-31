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

import android.app.Application
import com.devit.nddb.data.repository.HomeRepo.HomeRepository
import com.devit.nddb.data.repository.HomeRepo.HomeRepositoryImpl
import com.devit.nddb.data.repository.Language.LanguageRepository
import com.devit.nddb.data.repository.Language.LanguageRepositoryImpl
import com.devit.nddb.data.repository.OtpValidation.OtpRepository
import com.devit.nddb.data.repository.OtpValidation.OtpRepositoryImpl
import com.devit.nddb.data.repository.Registration.RegistrationRepository
import com.devit.nddb.data.repository.Registration.RegistrationRepositoryImpl
import com.wajahatkarim3.imagine.data.remote.LoginApiService
import com.wajahatkarim3.imagine.data.repository.LoginRepository
import com.wajahatkarim3.imagine.data.repository.LoginRepositoryImpl
import com.wajahatkarim3.imagine.utils.StringUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The Dagger Module for providing repository instances.
 * @author Wajahat Karim
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideStringUtils(app: Application): StringUtils {
        return StringUtils(app)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(stringUtils: StringUtils, apiService: LoginApiService): LoginRepository {
        return LoginRepositoryImpl(stringUtils, apiService)
    }

    @Singleton
    @Provides
    fun provideOtpRepository(stringUtils: StringUtils, apiService: LoginApiService): OtpRepository {
        return OtpRepositoryImpl(stringUtils, apiService)
    }

    @Singleton
    @Provides
    fun provideLanguageRepository(stringUtils: StringUtils, apiService: LoginApiService): LanguageRepository {
        return LanguageRepositoryImpl(stringUtils, apiService)
    }

    @Singleton
    @Provides
    fun provideRegistrationRepository(stringUtils: StringUtils, apiService: LoginApiService): RegistrationRepository {
        return RegistrationRepositoryImpl(stringUtils, apiService)
    }

    @Singleton
    @Provides
    fun provideHomeRepository(stringUtils: StringUtils, apiService: LoginApiService): HomeRepository {
        return HomeRepositoryImpl(stringUtils, apiService)
    }
}
