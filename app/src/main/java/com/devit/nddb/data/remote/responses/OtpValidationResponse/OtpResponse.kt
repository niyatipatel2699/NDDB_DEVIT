package com.devit.nddb.data.remote.responses.OtpValidation

import com.google.gson.annotations.SerializedName

data class OtpResponse (

    @SerializedName("items")
    var items: OtpData? = null,

    @SerializedName("status")
    var status : Int = 0,

    @SerializedName("status_code")
    var status_code : Int = 0,

    @SerializedName("message")
    var message: String? = null


)