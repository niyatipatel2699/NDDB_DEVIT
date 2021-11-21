package com.nddb.kudamforkurien

import android.content.Context
import android.content.SharedPreferences
import com.nddb.kudamforkurien.utils.RestConstant
import com.google.gson.Gson


class MySharedPreferences internal constructor(context: Context, gson: Gson) {

    private val SP_NAME = "NDDBPrefs"
    private val editor: SharedPreferences.Editor
    var gson: Gson
    private lateinit var sharedPreferences: SharedPreferences

    fun clearPreferences() {
        editor.clear().apply()
    }

    var isLogin: Boolean
        get() = sharedPreferences.getBoolean(RestConstant.IS_LOGIN, false)
        set(isLogin) {
            editor.putBoolean(RestConstant.IS_LOGIN, isLogin).apply()
        }

    var isLanguageSelected: Boolean
        get() = sharedPreferences.getBoolean(RestConstant.IS_LANG_SELECTED, false)
        set(isLanguageSelected) {
            editor.putBoolean(RestConstant.IS_LANG_SELECTED, isLanguageSelected).apply()
        }

    var is_Registered: Int
        get() = sharedPreferences.getInt(RestConstant.IS_REGISTERED, 0)
        set(is_Registered) {
            editor.putInt(RestConstant.IS_REGISTERED, is_Registered).apply()
        }

    var token: String
        get() = sharedPreferences.getString(RestConstant.TOKEN, " ")!!
        set(token) {
            editor.putString(
                RestConstant.TOKEN, token).apply()
        }

    var first_name: String
        get() = sharedPreferences.getString(RestConstant.FIRST_NAME, " ")!!
        set(first_name) {
            editor.putString(RestConstant.FIRST_NAME, first_name).apply()
        }

    var user_type: String
        get() = sharedPreferences.getString(RestConstant.USER_TYPE, " ")!!
        set(user_type) {
            editor.putString(RestConstant.USER_TYPE, user_type).apply()
        }

    var last_name: String
        get() = sharedPreferences.getString(RestConstant.LAST_NAME, " ")!!
        set(last_name) {
            editor.putString(RestConstant.LAST_NAME, last_name).apply()
        }

    var gender : String
        get() = sharedPreferences.getString(RestConstant.GENDER," ")!!
        set(gender){
            editor.putString(RestConstant.GENDER,gender).apply()
        }

    var phone_number: String
        get() = sharedPreferences.getString(RestConstant.PHONE_NUMBER, " ")!!
        set(phone_number) {
            editor.putString(RestConstant.PHONE_NUMBER, phone_number).apply()
        }

    var is_facilitator: Int
        get() = sharedPreferences.getInt(RestConstant.IS_FACILITATOR, 0)
        set(is_facilitator) {
            editor.putInt(RestConstant.IS_FACILITATOR, is_facilitator).apply()
        }

    var lang_id: Int
        get() = sharedPreferences.getInt(RestConstant.LANG_ID, 0)
        set(lang_id) {
            editor.putInt(RestConstant.LANG_ID, lang_id).apply()
        }

    var keyDate: Long
        get() = sharedPreferences.getLong(RestConstant.KEY_DATE, com.nddb.kudamforkurien.utils.Util.calendar.timeInMillis)
        set(keyDate) {
            editor.putLong(RestConstant.KEY_DATE, keyDate).apply()
        }


    var keySteps: Int
        get() = sharedPreferences.getInt(RestConstant.KEY_STEPS, 0)
        set(keySteps) {
            editor.putInt(RestConstant.KEY_STEPS, keySteps).apply()
        }

    var keyStepsHome: Int
        get() = sharedPreferences.getInt(RestConstant.KEY_STEPS_HOME, 0)
        set(keySteps) {
            editor.putInt(RestConstant.KEY_STEPS_HOME, keySteps).apply()
        }

    /*var lang_name: String
        get() = sharedPreferences.getString(RestConstant.LANG_NAME, " ")!!
        set(lang_name) {
            editor.putString(RestConstant.LANG_NAME, lang_name).apply()
        }*/

    var state: String
        get() = sharedPreferences.getString(RestConstant.STATE, "")!!
        set(state) {
            editor.putString(RestConstant.STATE, state).apply()
        }

    var district: String
        get() = sharedPreferences.getString(RestConstant.DISTRICT, " ")!!
        set(district) {
            editor.putString(RestConstant.DISTRICT, district).apply()
        }

    var latitude: String
        get() = sharedPreferences.getString(RestConstant.LATITUDE, "")!!
        set(district) {
            editor.putString(RestConstant.LATITUDE, district).apply()
        }

    var longitude: String
        get() = sharedPreferences.getString(RestConstant.LONGITUDE, "")!!
        set(district) {
            editor.putString(RestConstant.LONGITUDE, district).apply()
        }

    var location: String
        get() = sharedPreferences.getString(RestConstant.LOCATION, "")!!
        set(district) {
            editor.putString(RestConstant.LOCATION, district).apply()
        }

    var firebaseToken: String
        get() = sharedPreferences.getString(RestConstant.FIRBASETOKEN, "")!!
        set(district) {
            editor.putString(RestConstant.FIRBASETOKEN, district).apply()
        }

    var user_rank: Int
        get() = sharedPreferences.getInt(RestConstant.USER_RANK, 0)
        set(user_rank) {
            editor.putInt(RestConstant.USER_RANK, user_rank).apply()
        }

    var total_steps: Int
        get() = sharedPreferences.getInt(RestConstant.TOTAL_STEPS, 0)
        set(total_steps) {
            editor.putInt(RestConstant.TOTAL_STEPS, total_steps).apply()
        }
    companion object {
        private var mySharedPreferences: MySharedPreferences? = null

        @JvmName("getMySharedPreferences1")
        fun getMySharedPreferences(): MySharedPreferences? {
            if (mySharedPreferences == null) {
                mySharedPreferences = MySharedPreferences(NDDBApp.nddbApp!!, Gson())
            }
            return mySharedPreferences
        }
    }

    init {
        sharedPreferences = context!!.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        this.gson = gson
    }
}