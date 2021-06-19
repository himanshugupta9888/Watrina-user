package com.gox.foodiemodule.data.model

data class PromoCodeListModel(
        val error: List<Any?>? = listOf(),
        val message: String = "",
        val responseData: ArrayList<PromocodeModel>,
        val statusCode: String = "",
        val title: String = ""
)
