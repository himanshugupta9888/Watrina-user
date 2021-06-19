package com.gox.basemodule.model

import com.google.gson.annotations.SerializedName

open class ResAddressListModel {
    @SerializedName("statusCode")
    var statusCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("responseData")
    var addressList: List<AddressModel>? = null
}