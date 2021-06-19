package com.gox.foodiemodule.data.model

data class OrderCheckRequest(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val `data`: List<Data?>? = listOf(),
            val emergency: List<Emergency?>? = listOf(),
            val response_time: String? = "",
            val sos: String? = ""
    ) {
        data class Emergency(
                val number: String? = ""
        )

        data class Data(
                val admin_service_id: Int? = 0,
                val assigned_at: String? = "",
                val cancel_reason: Any? = Any(),
                val cancelled_by: Any? = Any(),
                val city_id: Int? = 0,
                val company_id: Int? = 0,
                val country_id: Int? = 0,
                val created_at: String? = "",
                val currency: String? = "",
                val delivery: Delivery? = Delivery(),
                val delivery_address: String? = "",
                val delivery_date: Any? = Any(),
                val deliveryaddress: Deliveryaddress? = Deliveryaddress(),
                val description: String? = "",
                val id: Int? = 0,
                val invoice: Invoice? = Invoice(),
                val note: Any? = Any(),
                val order_otp: String? = "",
                val order_ready_status: Any? = Any(),
                val order_ready_time: Any? = Any(),
                val order_type: String? = "",
                val paid: Any? = Any(),
                val pickup: Pickup? = Pickup(),
                val pickup_address: String? = "",
                val promocode_id: Int? = 0,
                val provider: Any? = Any(),
                val provider_id: Any? = Any(),
                val provider_rated: Int? = 0,
                val provider_vehicle_id: Any? = Any(),
                val rating: Rating? = Rating(),
                val request_type: String? = "",
                val route_key: String? = "",
                val schedule_datetime: Any? = Any(),
                val schedule_status: Int? = 0,
                val status: String? = "",
                val store: Store? = Store(),
                val store_id: Int? = 0,
                val store_order_invoice_id: String? = "",
                val user: User? = User(),
                val user_address_id: Int? = 0,
                val user_id: Int? = 0,
                val user_rated: Double? = 0.0
        ) {
            data class Pickup(
                    val contact_number: String? = "",
                    val id: Int? = 0,
                    val latitude: Double? = 0.0,
                    val longitude: Double? = 0.0,
                    val picture: String? = "",
                    val store_location: String? = "",
                    val store_name: String? = "",
                    val store_type_id: Int? = 0,
                    val storetype: Storetype? = Storetype()
            ) {
                data class Storetype(
                        val category: String? = "",
                        val company_id: Int? = 0,
                        val id: Int? = 0,
                        val name: String? = "",
                        val status: Int? = 0
                )
            }

            data class Rating(
                    val admin_service_id: Int? = 0,
                    val company_id: Int? = 0,
                    val created_at: String? = "",
                    val created_by: Int? = 0,
                    val created_type: String? = "",
                    val deleted_at: Any? = Any(),
                    val deleted_by: Any? = Any(),
                    val deleted_type: Any? = Any(),
                    val id: Int? = 0,
                    val modified_by: Int? = 0,
                    val modified_type: String? = "",
                    val provider_comment: String? = "",
                    val provider_id: Int? = 0,
                    val provider_rating: Double? = 0.0,
                    val request_id: Int? = 0,
                    val store_comment: Any? = Any(),
                    val store_id: Any? = Any(),
                    val store_rating: Double? = 0.0,
                    val updated_at: String? = "",
                    val user_comment: String? = "",
                    val user_id: Int? = 0,
                    val user_rating: Double? = 0.0
            )

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
                    val rating: String? = "",
                    val state_id: Int? = 0,
                    val status: Int? = 0,
                    val user_type: String? = "",
                    val wallet_balance: Double? = 0.0
            )

            data class Delivery(
                    val flat_no: String? = "",
                    val id: Int? = 0,
                    val latitude: Double? = 0.0,
                    val longitude: Double? = 0.0,
                    val map_address: String? = "",
                    val street: String? = ""
            )

            data class Store(
                    val estimated_delivery_time: String? = "",
                    val id: Int? = 0,
                    val latitude: Double? = 0.0,
                    val longitude: Double? = 0.0,
                    val store_name: String? = "",
                    val store_type_id: Int? = 0,
                    val storetype: Storetype? = Storetype()
            ) {
                data class Storetype(
                        val category: String? = "",
                        val company_id: Int? = 0,
                        val id: Int? = 0,
                        val name: String? = "",
                        val status: Int? = 0
                )
            }

            data class Invoice(
                    val cart_details: String? = "",
                    val cash: Double? = 0.0,
                    val commision_amount: Double? = 0.0,
                    val commision_per: Double? = 0.0,
                    val company_id: Int? = 0,
                    val delivery_amount: Double? = 0.0,
                    val delivery_per: Double? = 0.0,
                    val discount: Double? = 0.0,
                    val gross: Double? = 0.0,
                    val id: Int? = 0,
                    val items: List<Item?>? = listOf(),
                    val net: Double? = 0.0,
                    val payable: Double? = 0.0,
                    val payment_id: String? = "",
                    val payment_mode: String? = "",
                    val promocode_amount: Double? = 0.0,
                    val promocode_id: Int? = 0,
                    val status: Int? = 0,
                    val store_id: Int? = 0,
                    val store_order_id: Int? = 0,
                    val store_package_amount: Double? = 0.0,
                    val tax_amount: Double? = 0.0,
                    val tax_per: Double? = 0.0,
                    val total_amount: Double? = 0.0,
                    val wallet_amount: Double? = 0.0
            ) {
                data class Item(
                        val cartaddon: List<Any?>? = listOf(),
                        val company_id: Int = 0,
                        val id: Int = 0,
                        val item_price: Double = 0.0,
                        val note: Any? = Any(),
                        val product: Product? = Product(),
                        val product_data: Any? = Any(),
                        val quantity: Int? = 0,
                        val store: Store? = Store(),
                        val store_id: Int? = 0,
                        val store_item_id: Int? = 0,
                        val store_order_id: Int? = 0,
                        val tot_addon_price: Double = 0.0,
                        val total_item_price: Double = 0.0,
                        val user_id: Int? = 0
                ) {
                    data class Store(
                            val commission: Double? = 0.0,
                            val free_delivery: Int? = 0,
                            val id: Int? = 0,
                            val offer_min_amount: String? = "",
                            val offer_percent: Double? = 0.0,
                            val picture: String? = "",
                            val rating: Double? = 0.0,
                            val store_cusinie: List<StoreCusinie?>? = listOf(),
                            val store_gst: Int? = 0,
                            val store_name: String? = "",
                            val store_packing_charges: Double? = 0.0,
                            val store_type_id: Int? = 0,
                            val storetype: Storetype? = Storetype()
                    ) {
                        data class Storetype(
                                val category: String? = "",
                                val company_id: Int? = 0,
                                val id: Int? = 0,
                                val name: String? = "",
                                val status: Int? = 0
                        )

                        data class StoreCusinie(
                                val company_id: Int? = 0,
                                val cuisine: Cuisine? = Cuisine(),
                                val cuisines_id: Int? = 0,
                                val id: Int? = 0,
                                val store_id: Int? = 0,
                                val store_type_id: Int? = 0
                        ) {
                            data class Cuisine(
                                    val company_id: Int? = 0,
                                    val id: Int? = 0,
                                    val name: String? = "",
                                    val status: Int? = 0,
                                    val store_type_id: Int? = 0
                            )
                        }
                    }

                    data class Product(
                            val id: Int? = 0,
                            val is_veg: String? = "",
                            val item_discount: Double? = 0.0,
                            val item_discount_type: String? = "",
                            val item_name: String? = "",
                            val item_price: Double? = 0.0,
                            val picture: String? = ""
                    )
                }
            }

            data class Deliveryaddress(
                    val id: Int? = 0,
                    val latitude: Double? = 0.0,
                    val longitude: Double? = 0.0,
                    val map_address: String? = ""
            )
        }
    }
}