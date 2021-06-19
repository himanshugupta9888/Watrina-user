package com.gox.taximodule.data.dto

data class EstimateFareResponse(
        val error: List<Any> = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
) {
    data class ResponseData(
            val currency: String = "",
            val fare: Fare = Fare(),
            val promocodes: List<Promocode> = listOf(),
            val service: Service = Service(),
            val unit: String = ""
    ) {
        data class Fare(
                val base_price: Double = 0.0,
                val distance: Double = 0.0,
                val estimated_fare: Double = 0.0,
                val peak: Int = 0,
                val peak_percentage: String = "",
                val service: String = "",
                val service_type: Int = 0,
                val tax_price: Double = 0.0,
                val time: String = "",
                val wallet_balance: Double = 0.0
        )

        data class Promocode(
                val expiration: String = "",
                val id: Int = 0,
                val max_amount: Double = 0.0,
                val percentage: Double = 0.0,
                val picture: String = "",
                val promo_code: String = "",
                val promo_description: String = "",
                val service: String = "",
                val status: String = ""
        )

        data class Service(
                val capacity: Int = 0,
                val company_id: Int = 0,
                val id: Int = 0,
                val ride_type_id: Int = 0,
                val status: Int = 0,
                val vehicle_image: String = "",
                val vehicle_marker: Any? = Any(),
                val vehicle_name: String = "",
                val vehicle_type: String = ""
        )
    }
}