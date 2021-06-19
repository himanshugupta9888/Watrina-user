package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class FareModel {
    @SerializedName("estimated_fare")
    var estimatedFare: String? = null
    @SerializedName("distance")
    var distance: String? = null
    @SerializedName("time")
    var time: String? = null
    @SerializedName("tax_price")
    var taxPrice: String? = null
    @SerializedName("base_price")
    var basePrice: String? = null
    @SerializedName("service_type")
    var serviceType: String? = null
    @SerializedName("service")
    var service: String? = null
    @SerializedName("peak")
    var peak: Int? = null
    @SerializedName("peak_percentage")
    var peakPercentage: String? = null
}