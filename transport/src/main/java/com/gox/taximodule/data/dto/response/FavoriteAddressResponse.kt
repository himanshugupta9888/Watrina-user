package com.gox.taximodule.data.dto.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FavoriteAddressResponse(
        val error: List<Any>,
        val message: String,
        @SerializedName("responseData")
        val addresses: List<Addresses>,
        val statusCode: String,
        val title: String) : Serializable

data class Addresses(
        val address_type: String,
        val city: String,
        val company_id: Int,
        val county: String,
        val flat_no: String,
        val id: Int,
        val landmark: String,
        val latitude: Double,
        val longitude: Double,
        val state: String,
        val street: String,
        val title: String,
        val user_id: String,
        val zipcode: Any) : Serializable