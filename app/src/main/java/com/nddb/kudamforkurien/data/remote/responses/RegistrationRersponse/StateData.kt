package com.nddb.kudamforkurien.data.remote.responses.Registration

data class StateData(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var status: Int = 0
){
    override fun toString()=name?:""
}