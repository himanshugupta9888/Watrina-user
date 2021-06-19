package com.gox.taximodule.data.dto

data class CityListModel(
    val error: List<Any?>? = listOf(),
    val message: String? = "",
    val responseData: List<ResponseData?>? = listOf(),
    val statusCode: String? = "",
    val title: String? = ""
) {
    data class ResponseData(
        val city_name: String? = "",
        val country_id: Int? = 0,
        val id: Int? = 0,
        val state_id: Int? = 0,
        val status: Int? = 0
    )
}