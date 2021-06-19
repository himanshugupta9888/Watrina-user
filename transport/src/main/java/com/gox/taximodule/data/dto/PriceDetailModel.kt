package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class PriceDetailModel {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("city_id")
    var cityId: Int? = null
    @SerializedName("vehicle_service_id")
    var vehicle_service_id: String = ""
    @SerializedName("ride_category_id")
    var ride_category_id: String = ""
    @SerializedName("ride_delivery_vehicle_id")
    var ride_delivery_vehicle_id: Int? = null
    @SerializedName("company_id")
    var company_id: Int? = null
    @SerializedName("calculator")
    var calculator: String = ""
    @SerializedName("fixed")
    var fixed: String = ""
    @SerializedName("price")
    var price: String = ""
    @SerializedName("minute")
    var minute: String = ""
    @SerializedName("hour")
    var hour: String = ""
    @SerializedName("distance")
    var distance: String = ""
    @SerializedName("package_name")
    var package_name: String = ""
    @SerializedName("base_price")
    var base_price: String = ""
    @SerializedName("base_unit")
    var base_unit: String = ""
    @SerializedName("base_hour")
    var base_hour: String = ""
    @SerializedName("per_unit_price")
    var per_unit_price: String = ""
    @SerializedName("per_minute_price")
    var per_minute_price: String = ""
    @SerializedName("per_km_price")
    var per_km_price: String = ""
    @SerializedName("per_hour")
    var per_hour: String = ""
    @SerializedName("per_hour_distance")
    var per_hour_distance: String = ""
    @SerializedName("waiting_free_mins")
    var waiting_free_mins: String = ""
    @SerializedName("waiting_min_charge")
    var waiting_min_charge: String = ""
    @SerializedName("status")
    var status: Int? = null

}