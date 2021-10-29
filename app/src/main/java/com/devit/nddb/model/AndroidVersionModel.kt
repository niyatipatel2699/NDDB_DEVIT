package com.devit.nddb.model

class  AndroidVersionModel{
    var imgResId : Int? = 0
    var codeName: String? = null
    var versionName: String? = null
    var apiLevel: String? = null

    constructor(imgResId: Int, codeName: String, versionName: String, apiLevel: String) {
        this.imgResId = imgResId
        this.codeName = codeName
        this.versionName = versionName
        this.apiLevel = apiLevel
    }
}

class StateCity {
    var id : String? = null
    var name : String? = null
    var state : String? = null
}