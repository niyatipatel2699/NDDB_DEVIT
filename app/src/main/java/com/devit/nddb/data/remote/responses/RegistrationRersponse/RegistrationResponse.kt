package com.devit.nddb.data.remote.responses.Registration

data class RegistrationResponse (

    var items: RegistrationModel? = null,
    var status : Int? = 0,
    var status_code : Int? = 0,
    var message: String? = null

)