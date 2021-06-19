package com.gox.taximodule.data.dto.response

import com.google.gson.annotations.SerializedName
import com.gox.taximodule.data.dto.ServiceData

class ServiceResponse {
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("responseData")
    var serviceData: ServiceData? = null


}