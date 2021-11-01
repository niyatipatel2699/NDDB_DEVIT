package com.devit.nddb.Activity.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.Activity.ui.OtpValidation.OTPActivity
import com.devit.nddb.R
import com.devit.nddb.databinding.ActivityLoginBinding
import com.wajahatkarim3.imagine.ui.home.ContentState
import com.wajahatkarim3.imagine.ui.home.ErrorState
import com.wajahatkarim3.imagine.ui.home.LoadingState
import com.wajahatkarim3.imagine.utils.gone
import com.wajahatkarim3.imagine.utils.showSnack
import com.wajahatkarim3.imagine.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var loginBinding: ActivityLoginBinding
    var pattern =
        "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    var m: Matcher? = null
    var lang_id : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        loginBinding.progressLogin.gone()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            lang_id = bundle.getInt("lang_id")
        }

        loginBinding.verificationBtn.setOnClickListener {
            /*val intent = Intent(this, OTPActivity::class.java)
            startActivity(intent)*/
            /* if (loginBinding.edtMobilenum.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT)
                    .show()
            } else if (loginBinding.edtMobilenum.getText().length < 10) {
                Toast.makeText(
                    this,
                    getString(R.string.enter_valid_mobile_number),
                    Toast.LENGTH_SHORT
                ).show()*/
            val r: Pattern = Pattern.compile(pattern)

            if (loginBinding.edtMobilenum.getText().toString().isEmpty()) {
                /*Toast.makeText(this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT)
                    .show()*/
                loginBinding.relMain.showSnack(getString(R.string.enter_mobile_number))
            } else if (!loginBinding.edtMobilenum.getText().toString().isEmpty()) {
                m = r.matcher(loginBinding.edtMobilenum.getText().toString().trim())
                if (m!!.find()) {
                    initObservations()
                    viewModel.loginWithOTP(loginBinding.edtMobilenum.text.toString(),lang_id)

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
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("mobile",loginBinding.edtMobilenum.text.toString())
                intent.putExtra("lang_id",lang_id)
                Log.e("success",loginResponse.message!!)
                startActivity(intent)
                finish()
            } else {
                loginResponse.message?.let {
                    loginBinding.relMain.showSnack(it)
                }
            }
        }

    }

}