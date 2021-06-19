package com.gox.xubermodule.data.model

import java.io.Serializable

data class ServiceCheckReqModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val data: List<Data?>? = listOf(),
            val emergency: List<Emergency?>? = listOf(),
            val sos: String? = ""
    ) : Serializable {
        data class Emergency(
                val number: String? = ""
        ) : Serializable

        data class Data(
                val admin_id: Any? = Any(),
                val admin_service_id: Int?,
                val after_comment: Any? = Any(),
                val after_image: Any? = Any(),
                val allow_description: Any? = Any(),
                val allow_image: Any? = Any(),
                val assigned_at: String? = "",
                val before_image: Any? = Any(),
                val booking_id: String? = "",
                val cancel_reason: Any? = Any(),
                val cancelled_by: Any? = Any(),
                val category: Category? = Category(),
                val city_id: Int? = 0,
                val company_id: Int? = 0,
                val country_id: Any? = Any(),
                val currency: String? = "",
                val distance: Int? = 0,
                val finished_at: String? = "",
                val id: Int? = 0,
                val is_scheduled: String? = "",
                val otp: String? = "",
                val paid: Int? = 0,
                val payment: Payment? = Payment(),
                val payment_mode: String? = "",
                val price: Any? = Any(),
                val promocode_id: Int? = 0,
                val provider: Provider? = Provider(),
                val provider_id: Int? = 0,
                val provider_rated: Double? = 0.0,
                val quantity: Any? = Any(),
                val reasons: List<Reason?>? = listOf(),
                val request_type: String? = "",
                val route_key: String? = "",
                val s_address: String? = "",
                val s_latitude: Double? = 0.0,
                val s_longitude: Double? = 0.0,
                val schedule_at: Any? = Any(),
                val service: Service? = Service(),
                val service_id: Int? = 0,
                val started_at: String? = "",
                val status: String? = "",
                val subcategory: Subcategory? = Subcategory(),
                val surge: Double? = 0.0,
                val timezone: String? = "",
                val track_distance: Double? = 0.0,
                val track_latitude: Double? = 0.0,
                val track_longitude: Double? = 0.0,
                val travel_time: String? = "",
                val unit: String? = "",
                val use_wallet: Double? = 0.0,
                val user: User? = User(),
                val user_id: Int? = 0,
                val user_rated: Int? = 0
        ) : Serializable {
            data class Provider(
                    val activation_status: Int? = 0,
                    val admin_id: Any? = Any(),
                    val city_id: Int? = 0,
                    val country_code: String? = "",
                    val country_id: Int? = 0,
                    val currency: Any? = Any(),
                    val currency_symbol: String? = "",
                    val current_location: Any? = Any(),
                    val device_id: Any? = Any(),
                    val device_token: Any? = Any(),
                    val device_type: Any? = Any(),
                    val email: String? = "",
                    val first_name: String? = "",
                    val gender: String? = "",
                    val id: Int? = 0,
                    val is_assigned: Int? = 0,
                    val is_bankdetail: Int? = 0,
                    val is_document: Int? = 0,
                    val is_online: Int? = 0,
                    val is_service: Int? = 0,
                    val language: String? = "",
                    val last_name: String? = "",
                    val latitude: Double? = 0.0,
                    val login_by: String? = "",
                    val longitude: Double? = 0.0,
                    val mobile: String? = "",
                    val otp: Any? = Any(),
                    val payment_gateway_id: Any? = Any(),
                    val payment_mode: String? = "",
                    val picture: Any? = Any(),
                    val qrcode_url: String? = "",
                    val rating: Double? = 0.0,
                    val referal_count: Int? = 0,
                    val referral_unique_id: String? = "",
                    val social_unique_id: Any? = Any(),
                    val state_id: Int? = 0,
                    val status: String? = "",
                    val stripe_cust_id: Any? = Any(),
                    val wallet_balance: Double? = 0.0,
                    val zone_id: Any? = Any()
            ) : Serializable

            data class Subcategory(
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val picture: String? = "",
                    val service_category_id: Int? = 0,
                    val service_subcategory_name: String? = "",
                    val service_subcategory_order: Int? = 0,
                    val service_subcategory_status: Int? = 0
            ) : Serializable

            data class Payment(
                    val card: Double? = 0.0,
                    val cash: Double? = 0.0,
                    val commision: Double? = 0.0,
                    val commision_percent: Double? = 0.0,
                    val company_id: Int? = 0,
                    val discount: Double? = 0.0,
                    val discount_percent: Double? = 0.0,
                    val distance: Double? = 0.0,
                    val extra_charges: Double? = 0.0,
                    val extra_charges_notes: String? = "",
                    val fixed: Double? = 0.0,
                    val fleet: Double? = 0.0,
                    val fleet_id: Int? = 0,
                    val fleet_percent: Double? = 0.0,
                    val hour: Int? = 0,
                    val id: Int? = 0,
                    val is_partial: Any? = Any(),
                    val minute: Int? = 0,
                    val payable: Double? = 0.0,
                    val payment_id: Any? = Any(),
                    val payment_mode: Any? = Any(),
                    val promocode_id: Int? = 0,
                    val provider_id: Int? = 0,
                    val provider_pay: Double? = 0.0,
                    val service_request_id: Int? = 0,
                    val surge: Double? = 0.0,
                    val tax: Double? = 0.0,
                    val tax_percent: Double? = 0.0,
                    val tips: Double? = 0.0,
                    val total: Double? = 0.0,
                    val user_id: Int? = 0,
                    val wallet: Double? = 0.0
            ) : Serializable

            data class User(
                    val city_id: Int? = 0,
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
                    val picture: Any? = Any(),
                    val rating: Double? = 0.0,
                    val state_id: Int? = 0,
                    val status: Int? = 0,
                    val user_type: String? = "",
                    val wallet_balance: Double? = 0.0
            ) : Serializable

            data class Service(
                    val allow_after_image: Int? = 0,
                    val allow_before_image: Int? = 0,
                    val allow_desc: Int? = 0,
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val is_professional: Int? = 0,
                    val picture: String? = "",
                    val service_category_id: Int? = 0,
                    val service_name: String? = "",
                    val service_status: Int? = 0,
                    val service_subcategory_id: Int? = 0
            ) : Serializable

            data class Category(
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val picture: String? = "",
                    val price_choose: String? = "",
                    val service_category_name: String? = "",
                    val service_category_order: Int? = 0,
                    val service_category_status: Int? = 0
            ) : Serializable

            data class Reason(
                    val created_by: Int? = 0,
                    val created_type: String? = "",
                    val deleted_by: Any? = Any(),
                    val deleted_type: Any? = Any(),
                    val id: Int? = 0,
                    val modified_by: Int? = 0,
                    val modified_type: String? = "",
                    val reason: String? = "",
                    val service: String? = "",
                    val status: String? = "",
                    val type: String? = ""
            ) : Serializable
        }
    }
}