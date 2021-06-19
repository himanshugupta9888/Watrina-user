package com.gox.taximodule.data.dto.request

import com.google.gson.annotations.SerializedName

class ReqTips {
    @SerializedName("id")
    var requestId: Int? = null
    @SerializedName("tips")
    var tipsAmount: String? = null
    @SerializedName("card_id")
    var cardId: String? = null
}