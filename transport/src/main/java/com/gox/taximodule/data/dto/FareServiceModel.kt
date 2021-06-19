package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class FareServiceModel {
    @SerializedName("fare")
    var faremodel: FareModel? = null
    @SerializedName("service")
    var service: ServiceModel? = null
    @SerializedName("promocodes")
    var promoCode: List<PromocodeModel>? = null
}