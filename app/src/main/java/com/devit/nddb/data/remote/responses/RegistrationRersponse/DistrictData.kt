package com.devit.nddb.data.remote.responses.Registration

data class DistrictData(

    var id: Int = 0,
    var name: String? = null,
    var state_id: Int = 0,
    var description: String? = null,
    var status: Int = 0
) {
    override fun toString() = name ?: ""
}