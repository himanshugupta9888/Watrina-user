package com.gox.xubermodule.data.model

data class SendRequestModel(
        val error: List<Any?>? = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
) {
    data class ResponseData(
            val message: String = "",
            val request_id: String = ""
    )
}
