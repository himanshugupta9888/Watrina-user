package com.gox.basemodule.model

import com.google.gson.annotations.SerializedName

class ReqPaymentUpdateModel {
    @SerializedName("id")
    var requestId: Int? = null
    @SerializedName("payment_mode")
    var paymentMode: String? = null
    @SerializedName("card_id")
    var cardId: String? = null
    @SerializedName("reference")
    var reference: String? = null
}