package com.devit.nddb.data.remote.responses.Registration

data class UserTypeData (

    var id : Int = 0,
    var name: String? = null,
    var status : Int  = 0

){
    override fun toString()=name?:""
}