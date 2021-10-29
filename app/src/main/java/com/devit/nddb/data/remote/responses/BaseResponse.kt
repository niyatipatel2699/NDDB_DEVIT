package com.devit.nddb.data.remote.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class BaseResponse(
    @SerializedName("items")
    val items: Items? = Items(),
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: Int? = 0,
    @SerializedName("status_code")
    val statusCode: Int? = 0
) {
    @Keep
    class Items
}