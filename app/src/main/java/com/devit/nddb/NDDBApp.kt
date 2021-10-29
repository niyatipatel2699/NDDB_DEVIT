package com.devit.nddb

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NDDBApp : Application() {

    override fun onCreate() {
        super.onCreate()
        nddbApp = this
    }

    companion object {
        lateinit var nddbApp: NDDBApp
            private set
    }
}
