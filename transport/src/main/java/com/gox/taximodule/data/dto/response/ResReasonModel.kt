package com.gox.taximodule.data.dto.response

import com.google.gson.annotations.SerializedName
import com.gox.taximodule.data.dto.ReasonModel

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
