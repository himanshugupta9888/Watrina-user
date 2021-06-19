package com.gox.app.data.repositary.remote.model

data class NotificationNewResponse(
        val error: List<Any> = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
) {
    data class ResponseData(
            val notification: List<Notification> = listOf(),
            val total_records: Int = 0
    ) {
        data class Notification(
                val company_id: Int = 0,
                val created_at: String = "",
                val created_by: Int = 0,
                val created_type: String = "",
                val deleted_by: Any? = Any(),
                val deleted_type: Any? = Any(),
                val descriptions: String = "",
                val expiry_date: String = "",
                val id: Int = 0,
                val image: String = "",
                val modified_by: Int = 0,
                val modified_type: String = "",
                val notify_type: String = "",
                val service: String = "",
                val status: String = "",
                val title: String = "",
                val updated_at: String = ""
        )
    }
}