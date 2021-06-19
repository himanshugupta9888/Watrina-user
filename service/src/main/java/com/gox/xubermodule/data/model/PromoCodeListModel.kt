package com.gox.xubermodule.data.model

data class PromoCodeListModel(
        val error: List<Any?>? = listOf(),
        val message: String = "",
        val responseData: List<PromocodeModel>,
        val statusCode: String = "",
        val title: String = ""
)
