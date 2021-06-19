package com.gox.app.data.repositary.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HomeMenuResponse(
        val error: List<Any> = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
):Serializable {
    data class ResponseData(
            val promocodes: List<Promocode> = listOf(),
            val services: List<Service> = listOf(),
         @SerializedName("recent_requests")  val recentRequests: List<Service> = listOf()
    ):Serializable {
        data class Service(
                val admin_service_id: Int = 0,
                val bg_color: String = "",
                val featured_image: String = "",
                val icon: String = "",
                val id: Int = 0,
                val is_featured: Int = 0,
                val admin_service: String,
                val menu_type_id: Int = 0,
                val service: Service = Service(),
                val sort_order: Int = 0,
                val status: Int = 0,
                val title: String = ""
        ):Serializable {
            data class Service(
                    val admin_service: String = "",
                    val base_url: String = "",
                    val display_name: String = "",
                    val id: Int = 0,
                    val status: Int = 0
            ):Serializable
        }

        data class Promocode(
                val expiration: String = "",
                val id: Int = 0,
                val max_amount: Int = 0,
                val percentage: Int = 0,
                val picture: String = "",
                val promo_code: String = "",
                val promo_description: String = "",
                val service: String = "",
                val status: String = ""
        ):Serializable
    }
}