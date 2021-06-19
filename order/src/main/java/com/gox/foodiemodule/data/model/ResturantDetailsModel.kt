package com.gox.foodiemodule.data.model

data class ResturantDetailsModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val categories: List<Category?>? = listOf(),
            val company_id: Int? = 0,
            val estimated_delivery_time: String? = "",
            val free_delivery: Int? = 0,
            val id: Int? = 0,
            val is_veg: String? = "",
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val offer_min_amount: String? = "",
            val offer_percent: Int? = 0,
            val picture: Any? = Any(),
            val products: List<Product?>? = listOf(),
            val rating: Double = 0.0,
            val shopstatus: String? = "",
            val store_location: String? = "",
            val store_name: String? = "",
            val store_type_id: Int? = 0,
            val storetype: Storetype? = Storetype(),
            val totalstorecart: Int? = 0,
            val usercart: Int? = 0,
            val totalcartprice: Double? = 0.0

    ) {
        data class Category(
                val company_id: Int? = 0,
                val id: Int? = 0,
                val picture: String? = "",
                val store_category_description: String? = "",
                val store_category_name: String? = "",
                val store_category_status: Int? = 0,
                val store_id: Int? = 0
        )

        data class Storetype(
                val category: String? = "",
                val company_id: Int? = 0,
                val id: Int? = 0,
                val name: String? = "",
                val status: Int? = 0
        )

        data class Product(
                val company_id: Int? = 0,
                val id: Int? = 0,
                val is_veg: String? = "",
                val item_description: String? = "",
                val item_discount: Double = 0.0,
                val shop_status: String? = "",
                val item_discount_type: String? = "",
                val item_name: String? = "",
                val quantity: Int? = null,
                val unit_id: Int? = null,
                val item_price: Double = 0.0,
                val product_offer: Double = 0.0,
                val itemcart: List<ItemCart?>? = listOf(),
                val itemsaddon: List<Itemsaddon?>? = listOf(),
                val picture: String = "",
                var total_item_price: Double = 0.0,
                val status: Int = 0,
                val offer: Int = 0,
                val store_category_id: Int? = 0,
                val store_id: Int? = 0,
                var tot_quantity: Int? = 0,
                var cartId: Int? = 0,
                var unit: Units? = Units()

        ) {
            data class Itemsaddon(
                    val addon_name: String? = "",
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val price: Int? = 0,
                    val store_addon_id: Int? = 0,
                    val store_id: Int? = 0,
                    val store_item_id: Int? = 0
            )

            data class Units(
                    val id: Int? = 0,
                    val name: String? = ""
            )
        }

        data class ItemCart(
                val company_id: Int? = 0,
                val id: Int? = 0,
                val item_price: Double = 0.0,
                val note: Any? = Any(),
                val product_data: Any? = Any(),
                val quantity: Int? = 0,
                val store_id: Int? = 0,
                val store_item_id: Int? = 0,
                val store_order_id: Int? = 0,
                val tot_addon_price: Double = 0.0,
                val total_item_price: Double = 0.0,
                val user_id: Int? = 0
        )

    }
}