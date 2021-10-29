package com.devit.nddb.data.remote.responses.Registration

data class UserTypeResponse (

    var items: List<UserTypeData>? = null,
    var totalCount : Int = 0,
    var status : Int = 0,
    var status_code : Int = 0,
    var message: String? = null
)