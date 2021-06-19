package com.gox.foodiemodule.data.model

data class ResturantListModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: List<ResponseData?>? = listOf(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val categories: List<Category?>? = listOf(),
            val company_id: Int? = 0,
            val estimated_delivery_time: String? = "",
            val free_delivery: Int? = 0,
            val id: Int? = 0,
            val store_id: Int? = 0,
            val is_veg: String? = "",
            val latitude: Double? = 0.0,
            val longitude: Double? = 0.0,
            val offer_min_amount: String? = "",
            val cusine_list: String? = "",
            val offer_percent: Int? = 0,
            val item_discount: Int? = 0,
            val picture: String? = "",
            val rating: Double = 0.0,
            val shopstatus: String? = "",
            val store_location: String? = "",
            val store_name: String? = "",
            val item_name: String? = "",
            val category: String? = "",
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

        data class Category(
                val company_id: Int? = 0,
                val id: Int? = 0,
                val picture: String? = "",
                val store_category_description: String? = "",
                val store_category_name: String? = "",
                val store_category_status: Int? = 0,
                val store_id: Int? = 0
        )
    }
}