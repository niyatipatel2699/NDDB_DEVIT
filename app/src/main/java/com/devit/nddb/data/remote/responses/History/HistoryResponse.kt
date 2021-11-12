package com.devit.nddb.data.remote.responses.History

data class HistoryResponse(
    val items: List<HistoryData>,
    val message: String,
    val status: Int,
    val status_code: Int,
    val totalCount: Int
)