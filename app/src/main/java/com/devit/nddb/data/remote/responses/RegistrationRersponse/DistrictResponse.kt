package com.devit.nddb.data.remote.responses.Registration

import android.content.ClipData.Item


data class DistrictResponse (

    var items: List<DistrictData>? = null,
    var totalCount : Int = 0,
    var status : Int = 0,
    var status_code : Int = 0,
    var message: String? = null
)