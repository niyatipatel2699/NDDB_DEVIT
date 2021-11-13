package com.nddb.kudamforkurien.data.remote.responses.Language

data class LanguageResponse (

    var items: List<LanguageData>? = null,
    var totalCount : Int = 0,
    var status : Int = 0,
    var status_code : Int = 0,
    var message: String? = null

)