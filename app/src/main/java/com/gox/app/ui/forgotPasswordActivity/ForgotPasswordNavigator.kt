package com.gox.app.ui.forgotPasswordActivity

interface ForgotPasswordNavigator {

    fun goToRestPasswordActivity()
    fun changeOtpVerifyViaPhone()
    fun changeOtpVerifyViaMail()
    fun goToCountryCodePickerActivity()
    fun forgotpasswordInValidation(accountType: String, accountype: String): Boolean



}
