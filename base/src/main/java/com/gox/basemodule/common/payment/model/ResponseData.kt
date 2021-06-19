package com.gox.basemodule.common.payment.model

import com.google.gson.annotations.SerializedName

class ResponseData {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("request_id")
    var requestId: Int? = null
    @SerializedName("admin_service_id")
    var adminServiceId: Int? = null
    @SerializedName("data")
    var ChatSocketResponseModel: List<ChatSocketResponseModel>? = null

}