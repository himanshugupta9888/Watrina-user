package com.gox.app.data.repositary.remote.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromocodeModel() : Serializable {
    @SerializedName("id")
    val id: Int? = null
    @SerializedName("promo_code")
    val promoCode: String? = null
    @SerializedName("service")
    val service: String? = null
    @SerializedName("picture")
    val picture: String? = null
    @SerializedName("percentage")
    val percentage: Int? = null
    @SerializedName("max_amount")
    val maxAmount: Int? = null
    @SerializedName("promo_description")
    val promoDescription: String? = null
    @SerializedName("status")
    val status: String? = null
    @SerializedName("expiration")
    val promoExpiration: String? = null

}