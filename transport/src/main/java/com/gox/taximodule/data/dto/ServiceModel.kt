package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ServiceModel : Serializable {
    @SerializedName("id")
    val id: Int? = null
    @SerializedName("company_id")
    val companyId: Int? = null
    @SerializedName("ride_type_id")
    val rideTypeId: Int? = null
    @SerializedName("vehicle_type")
    val vehicleType: String? = null
    @SerializedName("vehicle_name")
    val vehicleName: String? = null
    @SerializedName("vehicle_image")
    val vehicleImage: String? = null
    @SerializedName("vehicle_marker")
    val vehicleMarker: String? = null
    @SerializedName("capacity")
    val capcity: Int? = 0
    @SerializedName("status")
    val status: Int? = null
    @SerializedName("price_details")
    val priceDetailModel: PriceDetailModel? = null
    @SerializedName("estimated_time")
    val estimatedTime: String? = null
    @SerializedName("isSelected")
    var isSelected: Boolean = false
}