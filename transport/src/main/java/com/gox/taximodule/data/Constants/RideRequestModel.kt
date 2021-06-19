package com.gox.taximodule.data.Constants

import com.google.gson.annotations.SerializedName

class RideRequestModel {
    @SerializedName("message")
    var message: String? = null
    @SerializedName("request_id")
    var requestId: Int? = null

}