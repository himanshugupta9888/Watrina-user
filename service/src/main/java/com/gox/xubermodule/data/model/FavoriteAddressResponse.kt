package com.gox.xubermodule.data.model

import java.io.Serializable

data class FavoriteAddressResponse(
        val message: String,
        val responseData: List<Addresses>,
        val statusCode: String,
        val title: String) : Serializable

data class Addresses(
        val address_type: String,
        val city: String,
        val company_id: String,
        val county: String,
        val flat_no: String,
        val id: String,
        val landmark: String,
        val latitude: String,
        val longitude: String,
        val state: String,
        val street: String,
        val title: String,
        val user_id: String,
        val zipcode: String) : Serializable
