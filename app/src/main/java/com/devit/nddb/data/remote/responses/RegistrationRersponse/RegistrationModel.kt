package com.devit.nddb.data.remote.responses.Registration

data class RegistrationModel (

    var user: RegistrationData? = null,
    var is_Registered : Int? = 0,
    var token: String? = null
)