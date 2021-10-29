package com.devit.nddb.data.remote.responses.OtpValidation

data class UserData(

    var first_name: String? = null,
    var last_name: String? = null,
    var phone_number: String? = null,
    var is_facilitator: Int = 0,
    var lang_id: Int= 0,
    var is_Registered: Int = 0,
    var state: String? = null,
    var district: String? = null
)