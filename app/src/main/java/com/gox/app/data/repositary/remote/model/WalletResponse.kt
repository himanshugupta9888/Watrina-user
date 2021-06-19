package com.gox.app.data.repositary.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class WalletResponse {
    @SerializedName("statusCode")
    @Expose
    private var statusCode: String? = null
    @SerializedName("title")
    @Expose
    private var title: String? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("responseData")
    @Expose
    private var responseData: WalletModel? = null
    @SerializedName("error")
    @Expose
    private var error: List<Any>? = null
}