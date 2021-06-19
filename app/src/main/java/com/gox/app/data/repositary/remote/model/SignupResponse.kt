package com.gox.app.data.repositary.remote.model

import java.io.Serializable


data class SignupResponse(
    val error: List<Any>,
    val message: String,
    val responseData: SignupResponseData,
    val statusCode: String,
    val title: String
):Serializable

data class SignupResponseData(
    val access_token: String,
    val contact_number: List<ContactNumber>,
    val currency: String,
    val expires_in: Int,
    val measurement: String,
    val sos: String,
    val token_type: String,
    val user: User
):Serializable

data class ContactNumber(
    val number: String
):Serializable

