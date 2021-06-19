package com.gox.xubermodule.data.model

import com.google.gson.annotations.SerializedName

class ReqTips {
    @SerializedName("id")
    var requestId: Int? = null
    @SerializedName("tips")
    var tipsAmount: String? = null
    @SerializedName("card_id")
    var cardId: String? = null
}