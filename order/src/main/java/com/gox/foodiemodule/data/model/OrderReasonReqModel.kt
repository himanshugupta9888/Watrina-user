package com.gox.foodiemodule.data.model

import com.google.gson.annotations.SerializedName

class OrderReasonReqModel {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("reason")
    var reason: String? = null
}