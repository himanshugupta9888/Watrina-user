package com.gox.basemodule.common.payment.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AddCardModel {
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
    private var responseData: CardResponseModel? = null
    @SerializedName("error")
    @Expose
    private var error: List<Any>? = null

    fun getStatusCode(): String? {
        return statusCode
    }

    fun setStatusCode(statusCode: String) {
        this.statusCode = statusCode
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getResponseData(): CardResponseModel? {
        return responseData
    }

    fun getError(): List<Any>? {
        return error
    }

    fun setError(error: List<Any>) {
        this.error = error
    }

}