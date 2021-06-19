package com.gox.taximodule.data.dto.response

data class ResWalletModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val city_id: Int? = 0,
            val country: Country? = Country(),
            val country_code: String? = "",
            val country_id: Int? = 0,
            val created_at: String? = "",
            val currency_symbol: String? = "",
            val email: String? = "",
            val first_name: String? = "",
            val gender: String? = "",
            val id: Int? = 0,
            val language: String? = "",
            val last_name: String? = "",
            val latitude: Any? = Any(),
            val login_by: String? = "",
            val longitude: Any? = Any(),
            val mobile: String? = "",
            val payment_mode: String? = "",
            val picture: String? = "",
            val rating: String? = "",
            val state_id: Int? = 0,
            val status: Int? = 0,
            val user_type: String? = "",
            val wallet_balance: Double? = 0.0
    ) {
        data class Country(
                val country_code: String? = "",
                val country_currency: Any? = Any(),
                val country_name: String? = "",
                val country_phonecode: String? = "",
                val country_symbol: Any? = Any(),
                val id: Int? = 0,
                val status: Any? = Any(),
                val timezone: Any? = Any()
        )
    }
}