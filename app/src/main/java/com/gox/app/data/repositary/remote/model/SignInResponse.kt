package com.gox.app.data.repositary.remote.model

import java.io.Serializable

data class SignInResponse(
    val error: List<Any>,
    val message: String,
    val responseData: ResponseData,
    val statusCode: String,
    val title: String
):Serializable

data class ResponseData(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val user: User
):Serializable

data class User(
        val city_id: Int,
        val country_code: String,
        val country_id: Int,
        val created_at: String,
        val email: String,
        val first_name: String,
        val gender: String,
        val id: Int,
        val language: Any,
        val last_name: String,
        val latitude: Any,
        val longitude: Any,
        val mobile: String,
        val payment_mode: String,
        val picture: Any,
        val rating: String,
        val state_id: Int,
        val user_type: String,
        val wallet_balance: Double? = 0.0
):Serializable