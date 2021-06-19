package com.gox.basemodule.model

import com.google.gson.annotations.SerializedName

class ResCommonSuccussModel {
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("message")
    var message: String? = null
}