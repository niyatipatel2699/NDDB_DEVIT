
package com.nddb.kudamforkurien.di.modules

import android.app.Application
import com.nddb.kudamforkurien.data.repository.HistoryRepo.HistoryRepository
import com.nddb.kudamforkurien.data.repository.HistoryRepo.HistoryRepositoryImpl
import com.nddb.kudamforkurien.data.repository.HomeRepo.HomeRepository
import com.nddb.kudamforkurien.data.repository.HomeRepo.HomeRepositoryImpl
import com.nddb.kudamforkurien.data.repository.Language.LanguageRepository
import com.nddb.kudamforkurien.data.repository.Language.LanguageRepositoryImpl
import com.nddb.kudamforkurien.data.repository.OtpValidation.OtpRepository
import com.nddb.kudamforkurien.data.repository.OtpValidation.OtpRepositoryImpl
import com.nddb.kudamforkurien.data.repository.Registration.RegistrationRepository
import com.nddb.kudamforkurien.data.repository.Registration.RegistrationRepositoryImpl
import com.nddb.kudamforkurien.data.remote.LoginApiService
import com.nddb.kudamforkurien.data.repository.LoginRepository
import com.nddb.kudamforkurien.data.repository.LoginRepositoryImpl
import com.nddb.kudamforkurien.utils.StringUtils
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
    fun provideHistoryRepository(stringUtils: StringUtils, apiService: LoginApiService): HistoryRepository {
        return HistoryRepositoryImpl(stringUtils, apiService)
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
