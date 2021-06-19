package com.gox.foodiemodule.data.model

data class ResOrderDetail(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val admin_service_id: Int? = 0,
            val cancel_reason: Any? = Any(),
            val cancelled_by: Any? = Any(),
            val city_id: Int? = 0,
            val company_id: Int? = 0,
            val country_id: Int? = 0,
            val created_at: String? = "",
            val currency: String? = "",
            val delivery: Delivery? = Delivery(),
            val delivery_address: String? = "",
            val delivery_date: String? = "",
            val delivery_time: String? = "",
            val deliveryaddress: Deliveryaddress? = Deliveryaddress(),
            val id: Int? = 0,
            val invoice: Invoice? = Invoice(),
            val note: Any? = Any(),
            val order_otp: String? = "",
            val order_ready_status: Int? = 0,
            val order_ready_time: Int? = 0,
            val order_type: String? = "",
            val paid: Any? = Any(),
            val pickup: Pickup? = Pickup(),
            val pickup_address: String? = "",
            val promocode_id: Int? = 0,
            val provider: Provider? = Provider(),
            val provider_id: Int? = 0,
            val provider_rated: Double = 0.0,
            val provider_vehicle_id: Any? = Any(),
            val request_type: String? = "",
            val route_key: String? = "",
            val schedule_datetime: Any? = Any(),
            val schedule_status: Int? = 0,
            val status: String? = "",
            val store: Store? = Store(),
            val store_id: Int? = 0,
            val store_order_invoice_id: String = "",
            val user: User? = User(),
            val user_address_id: Int? = 0,
            val user_id: Int? = 0,
            val user_rated: Double = 0.0
    ) {
        data class Pickup(
                val contact_number: String? = "",
                val country_code: Int? = 0,
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
                    val category: String = "",
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val name: String? = "",
                    val status: Int? = 0
            )
        }

        data class Deliveryaddress(
                val id: Int? = 0,
                val latitude: Double? = 0.0,
                val longitude: Double? = 0.0,
                val map_address: Any? = Any()
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
                val picture: String? = "",
                val rating: String? = "",
                val state_id: Int? = 0,
                val status: Int? = 0,
                val user_type: String? = "",
                val wallet_balance: Double? = 0.0
        )

        data class Invoice(
                val cart_details: String? = "",
                val cash: String? = "",
                val commision_amount: String? = "",
                val commision_per: String? = "",
                val company_id: Int? = 0,
                val delivery_amount: String? = "",
                val delivery_per: String? = "",
                val discount: String? = "",
                val gross: String? = "",
                val id: Int? = 0,
                val items: List<Item?>? = listOf(),
                val net: String? = "",
                val payable: String? = "",
                val payment_id: String? = "",
                val payment_mode: String? = "",
                val promocode_amount: String? = "",
                val promocode_id: String? = "",
                val status: Int? = 0,
                val store_id: Int? = 0,
                val store_order_id: Int? = 0,
                val store_package_amount: String? = "",
                val tax_amount: String? = "",
                val tax_per: String? = "",
                val total_amount: String? = "",
                val wallet_amount: String? = ""
        ) {
            data class Item(
                    val cartaddon: List<Any?>? = listOf(),
                    val company_id: Int? = 0,
                    val id: Int? = 0,
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
                        val commission: Double = 0.0,
                        val free_delivery: Int? = 0,
                        val id: Int? = 0,
                        val offer_min_amount: String? = "",
                        val offer_percent: Double = 0.0,
                        val picture: String? = "",
                        val rating: String? = "",
                        val store_cusinie: List<StoreCusinie?>? = listOf(),
                        val store_gst: Double = 0.0,
                        val store_name: String? = "",
                        val store_packing_charges: String? = "",
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
                        val item_discount: String? = "",
                        val item_discount_type: String? = "",
                        val item_name: String? = "",
                        val item_price: String? = "",
                        val picture: String? = ""
                )
            }
        }

        data class Delivery(
                val flat_no: String? = "",
                val id: Int? = 0,
                val latitude: Double? = 0.0,
                val longitude: Double? = 0.0,
                val map_address: Any? = Any(),
                val street: String? = ""
        )

        data class Store(
                val id: Int? = 0,
                val latitude: Double? = 0.0,
                val longitude: Double? = 0.0,
                val store_name: String? = "",
                val store_type_id: Int? = 0,
                val estimated_delivery_time: String? = "",
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

        data class Provider(
                val country_code: String? = "",
                val first_name: String? = "",
                val id: Int? = 0,
                val last_name: String? = "",
                val latitude: Double? = 0.0,
                val longitude: Double? = 0.0,
                val mobile: String? = "",
                val picture: String? = "",
                val rating: String? = ""
        )
    }
}