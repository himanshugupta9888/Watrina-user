package com.gox.basemodule.model

data class WalletTransferModel(
        val error: List<Any> = listOf(),
        val message: String = "",
        val responseData: List<Any> = listOf(),
        val statusCode: String = "",
        val title: String = ""
)