package com.devit.nddb.data.remote.responses.RankResponse

data class RankData (

    var rnk: Int,
    var userId: Int? = null,
    var totalSteps: Int? = null
)