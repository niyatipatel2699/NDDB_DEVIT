package com.nddb.kudamforkurien.data.remote.responses.Registration

data class StateResponse (

    var items: List<StateData>? = null,
    var totalCount : Int = 0,
    var status : Int = 0,
    var status_code : Int = 0,
    var message: String? = null
)