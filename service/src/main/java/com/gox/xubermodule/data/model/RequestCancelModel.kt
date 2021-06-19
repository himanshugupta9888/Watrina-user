package com.gox.xubermodule.data.model

import com.google.gson.annotations.SerializedName

class RequestCancelModel {
    @SerializedName("id")
    var requestId: Int? = null
    @SerializedName("cancel")
    var reason: String? = null

}