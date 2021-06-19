package com.gox.app.data.repositary.remote.model

import java.io.Serializable

data class ForgotPasswordResponse(
        val error: List<Any>,
        val message: String,
        val responseData: ForgotPasswordResponseData,
        val statusCode: String,
        val title: String
) : Serializable

data class ForgotPasswordResponseData(
        val account_type: String,
        val country_code: String,
        val otp: Int,
        val username: String
) : Serializable