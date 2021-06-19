package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class ProviderModel {
    @SerializedName("id")
    val id: Int? = null
    @SerializedName("first_name")
    val firstName: String? = null
    @SerializedName("last_name")
    val lastName: String? = null
    @SerializedName("payment_mode")
    val paymentMode: String? = null
    @SerializedName("email")
    val email: String? = null
    @SerializedName("latitude")
    val latitude: Double? = null
    @SerializedName("longitude")
    val longitude: Double? = null
    @SerializedName("picture")
    val picture: String? = null
    @SerializedName("service")
    val service: Service? = null

    data class Service(
        val admin_service_id: Int? = 0,
        val base_fare: Int? = 0,
        val category_id: Int? = 0,
        val company_id: Int? = 0,
        val id: Int? = 0,
        val per_miles: Int? = 0,
        val per_mins: Int? = 0,
        val provider_id: Int? = 0,
        val provider_vehicle_id: Int? = 0,
        val ride_delivery_id: Int? = 0,
        val ride_vehicle: RideVehicle? = RideVehicle(),
        val service_id: Any? = Any(),
        val status: String? = "",
        val sub_category_id: Any? = Any()
    ) {
        data class RideVehicle(
            val capacity: Int? = 0,
            val company_id: Int? = 0,
            val id: Int? = 0,
            val ride_type_id: Int? = 0,
            val status: Int? = 0,
            val vehicle_image: String? = "",
            val vehicle_marker: String? = "",
            val vehicle_name: String? = "",
            val vehicle_type: String? = ""
        )
    }}
