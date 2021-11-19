package com.nddb.kudamforkurien

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.nddb.kudamforkurien.utils.AlarmUtils
import com.nddb.kudamforkurien.utils.LocaleManager
import dagger.hilt.android.HiltAndroidApp
import java.util.*


@HiltAndroidApp
class NDDBApp : Application() {


    override fun onCreate() {
        super.onCreate()
        nddbApp = this

    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(getLocaleManager(base)!!.setLocale(base!!))
        Log.d("LANGUAGE", "attachBaseContext")
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        getLocaleManager(this)!!.setLocale(this)
        Log.d("LANGUAGE", "onConfigurationChanged: " + newConfig.locale.getLanguage())
    }

    companion object {
        var localeManager: LocaleManager? = null
        fun getLocaleManager(context: Context?): LocaleManager? {
            if (localeManager == null) {
                localeManager = context?.let { LocaleManager.getLocaleManager(it) }
            }
            return localeManager
        }

        lateinit var nddbApp: NDDBApp
            private set
    }
}
