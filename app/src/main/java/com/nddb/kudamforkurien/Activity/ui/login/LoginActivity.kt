package com.nddb.kudamforkurien.Activity.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.nddb.kudamforkurien.Activity.BaseActivity
import com.nddb.kudamforkurien.Activity.DrawerActivity
import com.nddb.kudamforkurien.Activity.ui.OtpValidation.OTPActivity
import com.nddb.kudamforkurien.Activity.ui.registration.RegistrationActivity
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.remote.responses.OtpValidation.OtpResponse
import com.nddb.kudamforkurien.databinding.ActivityLoginBinding
import com.nddb.kudamforkurien.ui.home.ContentState
import com.nddb.kudamforkurien.ui.home.ErrorState
import com.nddb.kudamforkurien.ui.home.LoadingState

import com.nddb.kudamforkurien.utils.gone
import com.nddb.kudamforkurien.utils.showSnack
import com.nddb.kudamforkurien.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var loginBinding: ActivityLoginBinding
    var pattern =
        "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    var m: Matcher? = null
    var selected_lang_id : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        loginBinding.progressLogin.gone()


        loginBinding.toolbar.tvToolbartitle.setText(R.string.register)
        loginBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        loginBinding.toolbar.ivBack.setVisibility(View.INVISIBLE)

        /* val bundle: Bundle? = intent.extras
         if (bundle != null) {
             selected_lang_id = bundle.getInt("lang_id")

         }*/

        loginBinding.verificationBtn.setOnClickListener {

            val r: Pattern = Pattern.compile(pattern)

            if (loginBinding.edtMobilenum.getText().toString().isEmpty()) {
                /*Toast.makeText(this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT)
                    .show()*/
                loginBinding.relMain.showSnack(getString(R.string.enter_mobile_number))
            } else if (!loginBinding.edtMobilenum.getText().toString().isEmpty()) {
                m = r.matcher(loginBinding.edtMobilenum.getText().toString().trim())
                if (m!!.find()) {
                    initObservations()
                    viewModel.loginWithOTP(loginBinding.edtMobilenum.text.toString(),MySharedPreferences.getMySharedPreferences()!!.lang_id)

                } else {
                    //Toast.makeText(this, getString(R.string.enter_valid_mobile_number), Toast.LENGTH_LONG).show()
                    loginBinding.relMain.showSnack(getString(R.string.enter_valid_mobile_number))
                }
            } else {
                /*  // call api.
                initObservations()
                viewModel.loginWithOTP(loginBinding.edtMobilenum.text.toString())
            }*/
            }
        }
    }


    fun initObservations() {
        viewModel.uiStateLiveData.observe(this) { state ->
            when (state) {
                is LoadingState -> {
                    loginBinding.progressLogin.visible()
                }

                is ContentState -> {
                    loginBinding.progressLogin.gone()
                }

                is ErrorState -> {
                    loginBinding.progressLogin.gone()
                    loginBinding.relMain.showSnack(state.message)
                    Log.e("error-->",state.message)
                }
            }
        }

        viewModel.loginResponseLiveData.observe(this) { loginResponse ->
            /*    val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("mobile",loginBinding.edtMobilenum.text.toString())
                startActivity(intent)*/
            if (loginResponse.status == 1) {
                 /* val intent = Intent(this, OTPActivity::class.java)
                  //val intent = Intent(this, DrawerActivity::class.java)
                  intent.putExtra("mobile",loginBinding.edtMobilenum.text.toString())
                 // intent.putExtra("lang_id",selected_lang_id)
                  Log.e("success",loginResponse.message!!)
                  startActivity(intent)*/
                  //finish()

                if(loginResponse.items!!.is_Registered == 1){
                    setUserData(loginResponse)
                    val intent = Intent(this, DrawerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    //  Log.e("data-->", validateOTP.items.toString())
                    MySharedPreferences.getMySharedPreferences()!!.token = loginResponse.items!!.token.toString()
                    val intent = Intent(this, RegistrationActivity::class.java)
                    intent.putExtra("mobile", loginResponse.items!!.user!!.phone_number)
                    startActivity(intent)
                    finish()
                }
            } else {
                loginResponse.message?.let {
                    loginBinding.relMain.showSnack(it)
                }
            }
        }

    }

    private fun setUserData(validateOTP : OtpResponse) {

        MySharedPreferences.getMySharedPreferences()!!.isLogin = true
        MySharedPreferences.getMySharedPreferences()!!.lang_id = MySharedPreferences.getMySharedPreferences()!!.lang_id//validateOTP.items!!.user!!.lang_id
        MySharedPreferences.getMySharedPreferences()!!.token = validateOTP.items!!.token.toString()
        MySharedPreferences.getMySharedPreferences()!!.is_Registered = validateOTP.items!!.user!!.is_Registered
        MySharedPreferences.getMySharedPreferences()!!.first_name = validateOTP.items!!.user!!.first_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.last_name = validateOTP.items!!.user!!.last_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.phone_number = validateOTP.items!!.user!!.phone_number.toString()
        MySharedPreferences.getMySharedPreferences()!!.is_facilitator = validateOTP.items!!.user!!.is_facilitator
        MySharedPreferences.getMySharedPreferences()!!.state = validateOTP.items!!.user!!.state.toString()
        MySharedPreferences.getMySharedPreferences()!!.gender = validateOTP!!.items!!.user!!.gender.toString()
        MySharedPreferences.getMySharedPreferences()!!.district = validateOTP.items!!.user!!.district.toString()
        MySharedPreferences.getMySharedPreferences()!!.user_type = validateOTP!!.items!!.user!!.user_type.toString()

    }
}