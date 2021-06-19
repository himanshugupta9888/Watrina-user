package com.gox.foodiemodule.data.model

data class ResCheckCartModel(
    val error: List<Any?>? = listOf(),
    val message: String? = "",
    val responseData: ResponseData? = ResponseData(),
    val statusCode: String? = "",
    val title: String? = ""
) {
    data class ResponseData(
        val admin_service_id: Int? = 0,
        val cancel_reason: Any? = Any(),
        val cancelled_by: Any? = Any(),
        val company_id: Int? = 0,
        val delivery: Delivery? = Delivery(),
        val delivery_address: String? = "",
        val delivery_date: Any? = Any(),
        val deliveryaddress: Deliveryaddress? = Deliveryaddress(),
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
        val provider_id: Any? = Any(),
        val provider_rated: Double = 0.0,
        val provider_vehicle_id: Any? = Any(),
        val request_type: String? = "",
        val route_key: String? = "",
        val schedule_status: Int? = 0,
        val status: String? = "",
        val store: Store? = Store(),
        val store_id: Int? = 0,
        val store_order_invoice_id: String? = "",
        val user_address_id: Int? = 0,
        val user_id: Int? = 0,
        val user_rated: Double = 0.0
    ) {
        data class Pickup(
            val id: Int? = 0,
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val store_location: String? = "",
            val store_name: String? = ""
        )

        data class Deliveryaddress(
            val id: Int? = 0,
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val map_address: String? = ""
        )

        data class Store(
            val id: Int? = 0,
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val store_name: String? = ""
        )

        data class Delivery(
            val flat_no: String? = "",
            val id: Int? = 0,
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val map_address: String? = "",
            val street: String? = ""
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
            val payment_id: Any? = Any(),
            val payment_mode: String? = "",
            val promocode_amount: String? = "",
            val promocode_id: String? = "",
            val status: Int? = 0,
            val store_order_id: Int? = 0,
            val store_package_amount: String? = "",
            val tax_amount: String? = "",
            val tax_per: String? = "",
            val total_amount: String? = "",
            val wallet_amount: String? = ""
        ) {
            data class Item(
                val cartaddon: List<Any?>? = listOf(),
                val company_id: Int = 0,
                val id: Int? = 0,
                val item_price: Double = 0.0,
                val note: Any? = Any(),
                val product: Product? = Product(),
                val product_data: Any? = Any(),
                val quantity: Int? = 0,
                val store: Store? = Store(),
                val store_id: Int? = 0,
                val store_item_id: Int? = 0,
                val store_user_order_id: Int? = 0,
                val tot_addon_price: Double = 0.0,
                val total_item_price: Double = 0.0,
                val user_id: Int? = 0
            ) {
                data class Product(
                    val id: Int? = 0,
                    val item_name: String? = "",
                    val item_price: String? = ""
                )

                data class Store(
                    val commission: Double = 0.0,
                    val free_delivery: Int? = 0,
                    val id: Int? = 0,
                    val offer_min_amount: String? = "",
                    val offer_percent: Double = 0.0,
                    val store_gst: Double = 0.0,
                    val store_name: String? = "",
                    val store_packing_charges: String? = ""
                )
            }
        }
    }
}