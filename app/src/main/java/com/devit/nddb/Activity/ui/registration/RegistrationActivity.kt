package com.devit.nddb.Activity.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devit.nddb.Activity.BaseActivity
import com.devit.nddb.Activity.DrawerActivity
import com.devit.nddb.MySharedPreferences
import com.devit.nddb.R
import com.devit.nddb.data.remote.responses.Registration.DistrictData
import com.devit.nddb.data.remote.responses.Registration.RegistrationResponse
import com.devit.nddb.data.remote.responses.Registration.StateData
import com.devit.nddb.data.remote.responses.Registration.UserTypeData
import com.devit.nddb.databinding.ActivityRegistrationBinding
import com.devit.nddb.model.StateCity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.wajahatkarim3.imagine.utils.capitalized
import com.wajahatkarim3.imagine.utils.gone
import com.wajahatkarim3.imagine.utils.showSnack
import com.wajahatkarim3.imagine.utils.visible
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
                gender = chip.text as String?
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
                gender = chip.text as String?
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
        }

        regBinding.autoState.setOnItemClickListener { adapterView, view, position, id ->
            val list: StateData = stateList.get(position)
            state_id = java.lang.String.valueOf(list.id)
            Log.e("utype-->", state_id.toString())
            Log.d("Register Activity", "Register Activity State ${stateList[position]}")
            registrationViewModel.fetchCityList(stateList[position].id)
        }

        regBinding.autoCity.setOnItemClickListener { adapterView, view, position, id ->
            val list: DistrictData = cityList.get(position)
            city_id = java.lang.String.valueOf(list.id)
            Log.e("utype-->", city_id.toString())
            Log.d("Register Activity", "Register Activity City ${cityList[position]}")
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
                val i = Intent(this, DrawerActivity::class.java)
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
        MySharedPreferences.getMySharedPreferences()!!.user_type = user_type_name.toString()
        MySharedPreferences.getMySharedPreferences()!!.gender = gender.toString()
        MySharedPreferences.getMySharedPreferences()!!.state =
            registrationResponse.items!!.user!!.state.toString()
        MySharedPreferences.getMySharedPreferences()!!.district =
            registrationResponse.items!!.user!!.district.toString()


    }

//    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
//        val jsonString: String
//        try {
//            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//            return null
//        }
//        return jsonString
//    }
}