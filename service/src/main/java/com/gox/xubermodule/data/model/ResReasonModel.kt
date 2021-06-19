package com.gox.xubermodule.data.model

import com.google.gson.annotations.SerializedName

class ResReasonModel {
    @SerializedName("statusCode")
    val statusCode: String? = null
    @SerializedName("title")
    val title: String? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("responseData")
    val reasonModel: List<ReasonModel>? = null
}
