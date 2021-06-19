package com.gox.taximodule.data.dto.request

import com.google.gson.annotations.SerializedName

class ReqRatingModel {
    @SerializedName("id")
    var requesterId: String? = null
    @SerializedName("rating")
    var rating: Int? = null
    @SerializedName("comment")
    var comment: String? = null
    @SerializedName("admin_service")
    var adminService: String? = null

}