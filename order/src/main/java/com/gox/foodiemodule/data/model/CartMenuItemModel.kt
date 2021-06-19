package com.gox.foodiemodule.data.model

data class CartMenuItemModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String = "",
        val title: String? = ""
) {
    data class ResponseData(
            val carts: List<Cart?>? = listOf(),
            val delivery_charges: Double = 0.0,
            val delivery_free_minimum: Int? = 0,
            val net: Double = 0.0,
            val payable: Double = 0.0,
            val promocode_amount: Double = 0.0,
            val promocode_id: Int? = 0,
            val rating: String = "0.0",
            val shop_cusines: String? = "",
            val shop_discount: Double = 0.0,
            val shop_gst: Double = 0.0,
            val shop_gst_amount: Double = 0.0,
            val shop_package_charge: Double = 0.0,
            val store_commision_per: Double = 0.0,
            val store_id: Int = 0,
            val store_type: String? = "",
            val tax_percentage: Double = 0.0,
            val total_cart: Double = 0.0,
            val total_price: Double = 0.0,
            val wallet_balance: Double = 0.0,
            val total_item_price: Double = 0.0,
            val user_currency: String? = ""
    ) {
        data class Cart(
                val cartaddon: List<Any?>? = listOf(),
                val company_id: Int = 0,
                val id: Int? = 0,
                val item_price: Double = 0.0,
                val note: String = "",
                val product: ResturantDetailsModel.ResponseData.Product? = ResturantDetailsModel.ResponseData.Product(),
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
                    val commission: Int? = 0,
                    val free_delivery: Int? = 0,
                    val picture: String? = null,
                    val id: Int? = 0,
                    val offer_min_amount: String? = "",
                    val offer_percent: Int? = 0,
                    val store_cusinie: List<StoreCusinie?>? = listOf(),
                    val store_gst: Int? = 0,
                    val store_name: String? = "",
                    val store_packing_charges: String? = "",
                    val store_type_id: Int? = 0,
                    val storetype: Storetype? = Storetype()
            ) {
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

                data class Storetype(
                        val category: String? = "",
                        val company_id: Int? = 0,
                        val id: Int? = 0,
                        val name: String? = "",
                        val status: Int? = 0
                )
            }
        }
    }
}