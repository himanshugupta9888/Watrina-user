package com.gox.foodiemodule.data.model

import com.google.gson.annotations.SerializedName

class ResFoodieCommonSuccussModel{
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("message")
    var message: String? = null
}