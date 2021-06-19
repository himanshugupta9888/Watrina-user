package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class EstimateResponse {
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("responseData")
    var responseData: FareServiceModel? = null

}