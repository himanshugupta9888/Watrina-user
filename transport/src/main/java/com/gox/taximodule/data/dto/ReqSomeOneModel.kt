package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class ReqSomeOneModel {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("phoneNumber")
    var phoneNumber: String? = null
    @SerializedName("email")
    var email: String? = null
}