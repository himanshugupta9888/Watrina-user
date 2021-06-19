package com.gox.app.data.repositary.remote.model.response

import com.google.gson.annotations.SerializedName

class ResProfileUpdate {
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("message")
    var message: String? = null

}