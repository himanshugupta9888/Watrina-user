package com.gox.foodiemodule.data.model

data class CusineListModel(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: List<CusineResponseData?>? = listOf(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class CusineResponseData(
            val company_id: Int? = 0,
            val id: Int? = 0,
            val name: String? = "",
            val status: Int? = 0,
            val store_type_id: Int? = 0
    )
}