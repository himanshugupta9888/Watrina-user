package com.gox.basemodule.common.payment.model

data class WalletTransactionList(
        val error: List<Any?>? = listOf(),
        val message: String? = "",
        val responseData: ResponseData? = ResponseData(),
        val statusCode: String? = "",
        val title: String? = ""
) {
    data class ResponseData(
            val `data`: List<Data>? = listOf(),
            val total_records: Int? = 0
    ) {
        data class Data(
                val amount: Double? = 0.0,
                val created_at: String? = "",
                val id: Int? = 0,
                val payment_log: PaymentLog? = PaymentLog(),
                val transaction_alias: String? = "",
                val transaction_desc: String? = "",
                val transaction_id: Int? = 0,
                val type: String? = ""
        ) {
            data class PaymentLog(
                    val amount: Double? =0.0,
                    val company_id: Int? = 0,
                    val id: Int? = 0,
                    val is_wallet: Int? = 0,
                    val payment_mode: String? = "",
                    val transaction_code: String? = "",
                    val user_id: Int? = 0,
                    val user_type: String? = ""
            )
        }
    }
}