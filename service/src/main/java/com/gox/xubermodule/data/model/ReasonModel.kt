package com.gox.xubermodule.data.model

import com.google.gson.annotations.SerializedName

class ReasonModel {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("service")
    var service: String? = null
    @SerializedName("type")
    var type: String? = null
    @SerializedName("reason")
    var reason: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("created_type")
    var createdType: String? = null
    @SerializedName("created_by")
    var createdBy: Int? = null
    @SerializedName("modified_type")
    var modifiedType: String? = null
    @SerializedName("modified_by")
    var modifiedBy: String? = null


}