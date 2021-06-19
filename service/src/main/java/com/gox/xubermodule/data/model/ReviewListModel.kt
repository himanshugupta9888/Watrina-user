package com.gox.xubermodule.data.model

data class ReviewListModel(
        val error: List<Any?>? = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
) {
    data class ResponseData(
            val total_records: String = "",
            val review: List<Review>? = listOf()
    ) {
        data class Review(
                val id: String = "",
                val admin_service_id: String = "",
                val user_id: String = "",
                val provider_id: String = "",
                val provider_rating: String = "",
                val provider_comment: String = "",
                val created_at: String = "",
                val user: User = User()
        )

        data class User(
                val id: String = "",
                val first_name: String = "",
                val last_name: String = "",
                val picture: String = ""
        )
    }
}
