package com.gox.xubermodule.data.model

data class SubServiceModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: List<ResponseData?>? = listOf(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val allow_after_image: Int? = 0,
            val allow_before_image: Int? = 0,
            val allow_desc: Int? = 0,
            val company_id: Int? = 0,
            val id: Int? = 0,
            val is_professional: Int? = 0,
            val picture: String? = "",
            val service_category_id: Int? = 0,
            val service_city: ServiceCity? = ServiceCity(),
            val service_name: String? = "",
            var selected: String? = "0",
            val service_status: Int? = 0,
            val service_subcategory_id: Int? = 0
    ) {
        data class ServiceCity(
                val allow_quantity: Int = 0,
                var quantity: Int = 0,
                val base_distance: String? = "",
                val base_fare: String? = "",
                val cancellation_charge: String? = "",
                val cancellation_time: Any? = Any(),
                val city_id: Int? = 0,
                val commission: String? = "",
                val company_id: Int? = 0,
                val country_id: Int? = 0,
                val fare_type: String? = "",
                val fleet_commission: String? = "",
                val id: Int? = 0,
                val max_quantity: Int? = 0,
                val minimum_fare: String? = "",
                val per_miles: String? = "",
                val per_mins: String? = "",
                val service_id: Int? = 0,
                val status: Int? = 0,
                val tax: String? = ""
        )
    }
}