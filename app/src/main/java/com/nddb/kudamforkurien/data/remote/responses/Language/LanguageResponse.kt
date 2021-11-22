package com.nddb.kudamforkurien.data.remote.responses.Language

import com.nddb.kudamforkurien.data.remote.responses.BaseResponse

data class LanguageResponse (

    var items: BaseResponse.Items? = null,
    var status: Int? = null,
    var statusCode: Int? = null,
    var message: String? = null,
    var eventStatus: String? = null

)