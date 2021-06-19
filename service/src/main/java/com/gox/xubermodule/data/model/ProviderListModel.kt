package com.gox.xubermodule.data.model

data class ProviderListModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val currency: String = "",
            val provider_service: List<ProviderService>? = listOf()
    ) {
        data class ProviderService(
                val base_fare: String = "",
                val city_id: String = "",
                val distance: String = "",
                val fare_type: String = "",
                val first_name: String = "",
                val id: String = "",
                val latitude: String = "",
                val longitude: String = "",
                val per_miles: String = "",
                val per_mins: String = "",
                val picture: String = "",
                val price_choose: String = "",
                val rating: String = ""
        )
    }
}
