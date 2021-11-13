package com.nddb.kudamforkurien.data.remote.responses.RankResponse

data class RankResponseModel (

       var  items: ArrayList<RankData>,
     var  totalCount : Int? = null,
    var  status : Int? = null,
    var  statusCode : Int? = null,
    var  message : String? = null

)