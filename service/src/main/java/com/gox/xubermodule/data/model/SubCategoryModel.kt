package com.gox.xubermodule.data.model

data class SubCategoryModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: List<SubCategoryData?>? = listOf(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class SubCategoryData(
            val company_id: Int? = 0,
            val id: Int? = 0,
            val picture: String? = "",
            val service_category_id: Int? = 0,
            val service_subcategory_name: String? = "",
            val service_subcategory_order: Int? = 0,
            val service_subcategory_status: Int? = 0
    )
}