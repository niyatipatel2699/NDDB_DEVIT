package com.nddb.kudamforkurien.Activity.ui.registration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nddb.kudamforkurien.Activity.BaseActivity
import com.nddb.kudamforkurien.Activity.DrawerActivity
import com.nddb.kudamforkurien.MySharedPreferences
import com.nddb.kudamforkurien.R
import com.nddb.kudamforkurien.data.remote.responses.Registration.DistrictData
import com.nddb.kudamforkurien.data.remote.responses.Registration.RegistrationResponse
import com.nddb.kudamforkurien.data.remote.responses.Registration.StateData
import com.nddb.kudamforkurien.data.remote.responses.Registration.UserTypeData
import com.nddb.kudamforkurien.databinding.ActivityRegistrationBinding
import com.nddb.kudamforkurien.model.StateCity

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.nddb.kudamforkurien.utils.capitalized
import com.nddb.kudamforkurien.utils.gone
import com.nddb.kudamforkurien.utils.showSnack
import com.nddb.kudamforkurien.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher

@AndroidEntryPoint
class RegistrationActivity : BaseActivity() {

    private var gender: String? = null
    lateinit var regBinding: ActivityRegistrationBinding
    private val registrationViewModel by viewModels<RegistrationModel>()
    var persons: List<StateCity>? = null
    var pattern =
        "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    var m: Matcher? = null
    var mobileNumber: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var state: String? = null
    var city: String? = null
    var user_type: String? = null
    var user_type_name: String? = null

    var user_type_id: String? = null
    var state_id: String? = null
    var city_id: String? = null

    private val userTypeList = mutableListOf<UserTypeData>()
    private val stateList = mutableListOf<StateData>()
    private val cityList = mutableListOf<DistrictData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(regBinding.root)
        regBinding.progressReg.gone()

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mobileNumber = bundle.getString("mobile")!!
        }

        initObservations()
        setEventListener()

        regBinding.tvRegMobile.setText(mobileNumber)

        regBinding.toolbar.tvToolbartitle.setText(R.string.registration)
        regBinding.toolbar.ivHome.setVisibility(View.INVISIBLE)
        regBinding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        regBinding.btnSubmit.setOnClickListener {

            if (registrationValidation()) {
//                var rg1=regBinding.group.checkedChipId
//                var rg2=regBinding.group1.checkedChipId
//                if(rg1 == R.id.option_1){
//                    gender = "Male"
//                }else if(rg1 == R.id.option_3){
//                    gender = "Transgender"
//                }else if(rg2 == R.id.option_2){
//                    gender = "Female"
//                }else if(rg2 == R.id.option_4){
//                    gender = "Don't Disclose"
//                }
                registrationViewModel.registerUser(
                    first_name?.capitalized() ?: "",
                    last_name?.capitalized() ?: "",
                    user_type!!,
                    state!!,
                    city!!,
                    gender,
                    mobileNumber
                )
            }
        }

        regBinding.group.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i ->

            if (chipGroup.findViewById<Chip>(i) != null) {
                val chip: Chip = chipGroup.findViewById(i)
                regBinding.group1.clearCheck()
                if(chip.id == R.id.option_1){
                    gender = "Male"
                }else if(chip.id == R.id.option_3){
                    gender = "Transgender"
                }
//                gender = chip.text as String?
                /*  Toast.makeText(
                      applicationContext,
                      "Chip is " + chip.text,
                      Toast.LENGTH_SHORT
                  ).show()*/
            }
        })

        regBinding.group1.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i ->

            if (chipGroup.findViewById<Chip>(i) != null) {
                val chip: Chip = chipGroup.findViewById(i)
                regBinding.group.clearCheck()
//                gender = chip.text as String?
                if(chip.id == R.id.option_2){
                    gender = "Female"
                }else if(chip.id == R.id.option_4){
                    gender = "Don't Disclose"
                }
                /* Toast.makeText(
                     applicationContext,
                     "Chip is " + chip.text,
                     Toast.LENGTH_SHORT
                 ).show()*/
            }
        })
    }

    private fun registrationValidation(): Boolean {
        first_name = regBinding.edtFname.text.toString()
        last_name = regBinding.edtLname.text.toString()
        user_type = user_type_id
        state = state_id
        city = city_id
        return when {
            TextUtils.isEmpty(first_name) -> {
                regBinding.relRegistration.showSnack(getString(R.string.validation_first_name))
                return false
            }
            TextUtils.isEmpty(last_name) -> {
                regBinding.relRegistration.showSnack(getString(R.string.validation_last_name))
                return false
            }
            user_type == null -> {
                regBinding.relRegistration.showSnack(getString(R.string.select_usertype))
                return false
            }
            state == null -> {
                regBinding.relRegistration.showSnack(getString(R.string.select_state))
                return false
            }
            city == null -> {
                regBinding.relRegistration.showSnack(getString(R.string.select_city))
                return false
            }
            gender == null -> {
                regBinding.relRegistration.showSnack(getString(R.string.select_gender))
                return false
            }
            else -> {
                true
            }
        }
    }


    private fun setEventListener() {

        regBinding.autoUsertype.setOnItemClickListener { adapterView, view, position, id ->
            val list: UserTypeData = userTypeList.get(position)
            user_type_id = java.lang.String.valueOf(list.id)
            user_type_name = java.lang.String.valueOf(list.name)
            Log.e("utype-->", user_type_id.toString())
            Log.d("Register Activity", "Register Activity User Type ${userTypeList[position]}")

            hideKeyboard(view)
        }

        regBinding.autoState.setOnItemClickListener { adapterView, view, position, id ->
            val list: StateData = stateList.get(position)
            state_id = java.lang.String.valueOf(list.id)
            Log.e("utype-->", state_id.toString())
            Log.d("Register Activity", "Register Activity State ${stateList[position]}")
            registrationViewModel.fetchCityList(stateList[position].id)

            hideKeyboard(view)
        }

        regBinding.autoCity.setOnItemClickListener { adapterView, view, position, id ->
            val list: DistrictData = cityList.get(position)
            city_id = java.lang.String.valueOf(list.id)
            Log.e("utype-->", city_id.toString())
            Log.d("Register Activity", "Register Activity City ${cityList[position]}")

            if (view != null) {
                val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

            hideKeyboard(view)
        }
    }

    private fun initObservations() {

        registrationViewModel.uiStateLiveData.observe(this) { state ->
            when (state) {
                is LoadingState -> {
                    regBinding.progressReg.visible()
                }

                is ContentState -> {
                    regBinding.progressReg.gone()
                }

                is ErrorState -> {
                    regBinding.progressReg.gone()
                    regBinding.relRegistration.showSnack(state.message)
                }
            }
        }

        registrationViewModel.userTypeResponseLiveData.observe(this) { userTypeResponse ->

            val status = userTypeResponse.status
            if (status == 1) {
                val userTypeLists = userTypeResponse.items
                if (userTypeLists?.isNotEmpty() == true) {
                    userTypeList.clear()
                    userTypeList.addAll(userTypeLists)
                    val userTypeArrayAdapter =
                        ArrayAdapter(this, R.layout.dropdown_menu, R.id.textView, userTypeList)
                    regBinding.autoUsertype.setAdapter(userTypeArrayAdapter)
                }
            }

        }

        registrationViewModel.stateResponseLiveData.observe(this) { stateResponse ->
            val status = stateResponse.status
            if (status == 1) {
                val stateLists = stateResponse.items
                if (stateLists?.isNotEmpty() == true) {
                    stateList.clear()
                    stateList.addAll(stateLists)
                    val stateArrayAdapter =
                        ArrayAdapter(this, R.layout.dropdown_menu, R.id.textView, stateList)
                    regBinding.autoState.setAdapter(stateArrayAdapter)
                }
            }

        }

        registrationViewModel.cityResponseLiveData.observe(this) { cityResponse ->
            val status = cityResponse.status
            if (status == 1) {
                val cityLists = cityResponse.items
                if (cityLists?.isNotEmpty() == true) {
                    cityList.clear()
                    cityList.addAll(cityLists)
                    val cityArrayAdapter =
                        ArrayAdapter(this, R.layout.dropdown_menu, R.id.textView, cityList)
                    regBinding.autoCity.setAdapter(cityArrayAdapter)
                }
            } else {
                regBinding.relRegistration.showSnack(getString(R.string.no_city_msg))
            }

        }

        registrationViewModel.registerResponseLiveData.observe(this) { registerResponse ->

            if (registerResponse.status == 1) {
                //Log.e("success",loginResponse.message!!)
                regBinding.relRegistration.showSnack(registerResponse.message!!)
                setUserData(registerResponse)
                /*val i = Intent(this, DrawerActivity::class.java)
                startActivity(i)
                finish()*/

                val i = Intent(this, DrawerActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                finish()
            } else {
                registerResponse.message?.let {
                    regBinding.relRegistration.showSnack(it)
                }
            }
        }
    }

    /* private fun setUserData(items: RegistrationData) {

     }*/

    private fun setUserData(registrationResponse: RegistrationResponse) {

        MySharedPreferences.getMySharedPreferences()!!.isLogin = true
        MySharedPreferences.getMySharedPreferences()!!.is_Registered = 1
        MySharedPreferences.getMySharedPreferences()!!.first_name =
            registrationResponse.items!!.user!!.first_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.last_name =
            registrationResponse.items!!.user!!.last_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.phone_number =
            registrationResponse.items!!.user!!.phone_number.toString()
        MySharedPreferences.getMySharedPreferences()!!.user_type =  registrationResponse.items!!.user!!.user_type.toString()
        MySharedPreferences.getMySharedPreferences()!!.gender = registrationResponse.items!!.user!!.gender.toString()
        MySharedPreferences.getMySharedPreferences()!!.state =
            registrationResponse.items!!.user!!.state.toString()
        MySharedPreferences.getMySharedPreferences()!!.district =
            registrationResponse.items!!.user!!.district.toString()


    }
    fun Context.hideKeyboard(view: View) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}