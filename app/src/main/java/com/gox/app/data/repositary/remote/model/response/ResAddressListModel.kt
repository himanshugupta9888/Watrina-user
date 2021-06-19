package com.gox.app.data.repositary.remote.model.response

import com.google.gson.annotations.SerializedName
import com.gox.app.data.repositary.remote.model.AddressModel

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