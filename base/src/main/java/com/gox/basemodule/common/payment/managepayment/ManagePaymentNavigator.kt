package com.gox.basemodule.common.payment.managepayment

interface ManagePaymentNavigator {
    fun addCard()
    fun showErrorMsg(error: String)
    fun validate(): Boolean
}