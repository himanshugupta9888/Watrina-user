package com.gox.taximodule.data.dto.request

import com.google.gson.annotations.SerializedName

class ReqExtendTripModel {
    @SerializedName("id")
    var requestId: String? = null
    @SerializedName("latitude")
    var latitude: String? = null
    @SerializedName("longitude")
    var longitude: String? = null
    @SerializedName("address")
    var address: String? = null

}