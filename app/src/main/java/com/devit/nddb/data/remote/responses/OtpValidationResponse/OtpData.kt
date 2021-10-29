package com.devit.nddb.data.remote.responses.OtpValidation

import com.google.gson.annotations.SerializedName

data class OtpData (

    @SerializedName("user")
    var user: UserData? = null,

    @SerializedName("is_Registered")
    var is_Registered : Int? = null,

    @SerializedName("token")
    var token: String? = null
)