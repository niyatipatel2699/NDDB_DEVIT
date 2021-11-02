package com.devit.nddb.Activity

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.NDDBApp

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { NDDBApp.getLocaleManager(base)?.setLocale(it) })
        Log.d("Language", "attachBaseContext")
    }
}