package com.gox.app.data.repositary.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.gox.app.data.repositary.remote.model.PaymentLog


class TransactionDatum {@SerializedName("id")
@Expose
private var id: Int? = null
    @SerializedName("transaction_id")
    @Expose
    private var transactionId: Int? = null
    @SerializedName("transaction_desc")
    @Expose
    private var transactionDesc: Any? = null
    @SerializedName("type")
    @Expose
    private var type: String? = null
    @SerializedName("amount")
    @Expose
    private var amount: Int? = null
    @SerializedName("created_at")
    @Expose
    private var createdAt: String? = null
    @SerializedName("payment_log")
    @Expose
    private var paymentLog: PaymentLog? = null
}