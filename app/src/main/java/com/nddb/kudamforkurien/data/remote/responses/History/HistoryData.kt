package com.nddb.kudamforkurien.data.remote.responses.History

data class HistoryData(
    val created_at: String,
    val date: String,
    val location: String,
    val steps: Int
)