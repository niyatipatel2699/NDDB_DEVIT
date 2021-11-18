package com.nddb.kudamforkurien.data.remote.responses.RankResponse

data class RankData (

    var rnk: Int,
    var userId: Int? = null,
    var total_steps: Int
)