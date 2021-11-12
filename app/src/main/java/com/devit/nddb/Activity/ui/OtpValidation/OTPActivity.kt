package com.devit.nddb.Activity.ui.OtpValidation

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.Activity.ui.registration.RegistrationActivity
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.brodcastreceiver.SmsBroadcastReceiver
import com.devit.nddb.data.remote.responses.OtpValidation.OtpResponse
import com.devit.nddb.databinding.ActivityOtpactivityBinding
import com.devit.nddb.utils.onTextChanged
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.wajahatkarim3.imagine.utils.gone
import com.wajahatkarim3.imagine.utils.showSnack
import com.wajahatkarim3.imagine.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPActivity : BaseActivity() {

    lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    lateinit var otpBinding: ActivityOtpactivityBinding
    private val otpViewModel by viewModels<OtpViewModel>()
    //private val viewModel: LoginViewModel by viewModels()

    private var code: String? = null
    var mobileNumber: String? = null
   // var selected_langid : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        otpBinding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(otpBinding.root)
        otpBinding.progressOTP.gone()


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mobileNumber = bundle.getString("mobile")
           // selected_langid = bundle.getInt("lang_id")

        }
        initObservations()

        otpBinding.toolbar.tvToolbartitle.setText(R.string.otp_screen)
        otpBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        otpBinding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        otpBinding.textMobilenumber.setText(R.string.otp_sent_on_mobile_number)
        otpBinding.textMobilenumber.append(" " + mobileNumber)

        otpBinding.otpEditBox1.onTextChanged {
            if (it.isNotEmpty()) {
                otpBinding.otpEditBox2.requestFocus()
            }
        }

        otpBinding.otpEditBox2.onTextChanged {
            if (it.isNotEmpty()) {
                otpBinding.otpEditBox3.requestFocus()
            }
        }

        otpBinding.otpEditBox3.onTextChanged {
            if (it.isNotEmpty()) {
                otpBinding.otpEditBox4.requestFocus()
            }
        }

        startSmsUserConsent()

        otpBinding.verifyOtpBtn.setOnClickListener {
            if(otpValidation()) {
                otpViewModel.otpValidation(mobileNumber.toString())
               /* MySharedPreferences.getMySharedPreferences()!!.isLogin = true
                val intent = Intent(this, RegistrationActivity::class.java)
                //val intent = Intent(this, DrawerActivity::class.java)
                intent.putExtra("mobile",mobileNumber)
                startActivity(intent)*/
            }
            /*val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("mobile",mobileNumber)
            startActivity(intent)
*/        }

        otpBinding.textResend.setOnClickListener {
            startCountDown()
            otpViewModel.loginWithOTP(mobileNumber.toString(),MySharedPreferences.getMySharedPreferences()!!.lang_id)
            /* Toast.makeText(this, getString(R.string.otp_will_be_send_to_mobile_number), Toast.LENGTH_SHORT)
                 .show()*/
        }

        startCountDown()
    }

    private fun otpValidation(): Boolean {

        return when {
            TextUtils.isEmpty(otpBinding.otpEditBox1.getText().toString()) -> {
                otpBinding.relOTP.showSnack(getString(R.string.please_enter_otp))
                return false
            }
            TextUtils.isEmpty(otpBinding.otpEditBox2.getText().toString()) -> {
                otpBinding.relOTP.showSnack(getString(R.string.please_enter_otp))
                return false
            }
            TextUtils.isEmpty(otpBinding.otpEditBox3.getText().toString()) -> {
                otpBinding.relOTP.showSnack(getString(R.string.please_enter_otp))
                return false
            }
            TextUtils.isEmpty(otpBinding.otpEditBox4.getText().toString()) -> {
                otpBinding.relOTP.showSnack(getString(R.string.please_enter_otp))
                return false
            }
            else -> {
                true
            }
        }
    }

    private fun initObservations() {

        otpViewModel.uiStateLiveData.observe(this) { state ->
            when (state) {
                is LoadingState -> {
                    otpBinding.progressOTP.visible()
                }

                is ContentState -> {
                    otpBinding.progressOTP.gone()
                }

                is ErrorState -> {
                    otpBinding.progressOTP.gone()
                    otpBinding.relOTP.showSnack(state.message)
                }
            }
        }

       /* otpViewModel.loginResponseLiveData.observe(this) { otpResponse ->

            if (otpResponse.status == 1) {
                //Log.e("success",loginResponse.message!!)
                otpBinding.relOTP.showSnack(otpResponse.message!!)
            } else {
                otpResponse.message?.let {
                    otpBinding.relOTP.showSnack(it)
                }
            }
        }*/

        otpViewModel.otpResponseLiveData.observe(this) { validateOTP ->

            if (validateOTP.status == 1) {
                validateOTP.message?.let {
                    otpBinding.relOTP.showSnack(it)
                }
                if(validateOTP.items!!.is_Registered == 1){
                    setUserData(validateOTP)
                    val intent = Intent(this, DrawerActivity::class.java)
                    startActivity(intent)
                    //finish()
                }
                else
                {
                    //  Log.e("data-->", validateOTP.items.toString())
                    MySharedPreferences.getMySharedPreferences()!!.token = validateOTP.items!!.token.toString()
                    val intent = Intent(this, RegistrationActivity::class.java)
                    intent.putExtra("mobile", validateOTP.items!!.user!!.phone_number)
                    startActivity(intent)
                    //finish()
                }
            } else {
                validateOTP.message?.let { otpBinding.relOTP.showSnack(it) }
            }
        }
    }

    private fun setUserData(validateOTP : OtpResponse) {

        MySharedPreferences.getMySharedPreferences()!!.isLogin = true
        MySharedPreferences.getMySharedPreferences()!!.lang_id = validateOTP.items!!.user!!.lang_id
        MySharedPreferences.getMySharedPreferences()!!.token = validateOTP.items!!.token.toString()
        MySharedPreferences.getMySharedPreferences()!!.is_Registered = validateOTP.items!!.user!!.is_Registered
        MySharedPreferences.getMySharedPreferences()!!.first_name = validateOTP.items!!.user!!.first_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.last_name = validateOTP.items!!.user!!.last_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.phone_number = validateOTP.items!!.user!!.phone_number.toString()
        MySharedPreferences.getMySharedPreferences()!!.is_facilitator = validateOTP.items!!.user!!.is_facilitator
        MySharedPreferences.getMySharedPreferences()!!.state = validateOTP.items!!.user!!.state.toString()
        MySharedPreferences.getMySharedPreferences()!!.district = validateOTP.items!!.user!!.district.toString()

    }


    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(this).also {
            //We can add user phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_USER_CONSENT -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    code = message?.let { fetchVerificationCode(it) }
                    val toCharArray = code?.toCharArray()
                    toCharArray?.forEachIndexed { index, c ->
//                        Log.e("OTP------", c.toString())
                        when (index) {
                            0 -> otpBinding.otpEditBox1.setText(c.toString())
                            1 -> otpBinding.otpEditBox2.setText(c.toString())
                            2 -> otpBinding.otpEditBox3.setText(c.toString())
                            3 -> otpBinding.otpEditBox4.setText(c.toString())
                        }
                    }
                }
            }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver().also {
            it.smsBroadcastReceiverListener =
                object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                    override fun onSuccess(intent: Intent?) {
                        intent?.let { context -> startActivityForResult(context, REQ_USER_CONSENT) }
                    }

                    override fun onFailure() {
                    }
                }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{4})").find(message)?.value ?: ""
    }

    companion object {
        const val TAG = "SMS_USER_CONSENT"

        const val REQ_USER_CONSENT = 100
    }


    private fun startCountDown() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                otpBinding.textOtpSec.setText(
                    "0:" + (millisUntilFinished / 1000).toString() + " " + "Remaining"
                )
            }

            override fun onFinish() {
                otpBinding.textOtpResendOtp.setTextColor(resources.getColor(R.color.black))
                otpBinding.textOtpResendOtp.isEnabled = true
            }
        }.start()
    }
}

