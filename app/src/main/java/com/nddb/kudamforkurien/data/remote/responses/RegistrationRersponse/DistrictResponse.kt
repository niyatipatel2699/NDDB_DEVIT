package com.nddb.kudamforkurien.data.remote.responses.Registration


data class DistrictResponse (

    var items: List<DistrictData>? = null,
    var totalCount : Int = 0,
    var status : Int = 0,
    var status_code : Int = 0,
    var message: String? = null
)