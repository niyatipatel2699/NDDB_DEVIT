package com.devit.nddb.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.Activity.ui.LanguageList.ChooseLanguageActivity
import com.devit.nddb.Activity.ui.login.LoginActivity
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.databinding.ActivityRegistrationBinding
import com.devit.nddb.databinding.ActivitySplashScreenBinding

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
            if(MySharedPreferences.getMySharedPreferences()!!.isLogin == true) {
                if (MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected == true) {
                    startActivity(Intent(this, DrawerActivity ::class.java))
                    finish()
                }
                else
                {
                    startActivity(Intent(this, ChooseLanguageActivity::class.java))
                    finish()
                }
            }
            else
            {
                if (MySharedPreferences.getMySharedPreferences()!!.isLanguageSelected == true)
                {
                    startActivity(Intent(this, LoginActivity ::class.java))
                    finish()
                }
                else
                {
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
}