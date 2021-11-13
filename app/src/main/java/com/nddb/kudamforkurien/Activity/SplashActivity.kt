package com.nddb.kudamforkurien.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.nddb.kudamforkurien.Activity.ui.LanguageList.ChooseLanguageActivity
import com.nddb.kudamforkurien.Activity.ui.login.LoginActivity
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.NDDBApp
import com.nddb.kudamforkurien.databinding.ActivitySplashScreenBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        /*Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ChooseLanguageActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)*/


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (MySharedPreferences.getMySharedPreferences()!!.isLogin == true) {
                if (MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected == true) {
                    startActivity(Intent(this, DrawerActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, ChooseLanguageActivity::class.java))
                    finish()
                }
            } else {
                if (MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected == true) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, ChooseLanguageActivity::class.java))
                    finish()
                }

            }
            /* if (MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected == true) {
                     startActivity(Intent(this,LoginActivity ::class.java))
                     finish()
             }
             if(MySharedPreferences.getMySharedPreferences()!!.isLogin == true) {
                 startActivity(Intent(this,DrawerActivity ::class.java))
                 finish()
             }else{
                 startActivity(Intent(this, ChooseLanguageActivity::class.java))
                 finish()

             }*/

        }, 2000)

    }

    override fun onResume() {
        super.onResume()
        val selectedLanguageCode = NDDBApp.getLocaleManager(this)?.language
        if (selectedLanguageCode != null && selectedLanguageCode.isNotEmpty()) {
            NDDBApp.getLocaleManager(this)?.setNewLocale(this, selectedLanguageCode)
        }
    }
}