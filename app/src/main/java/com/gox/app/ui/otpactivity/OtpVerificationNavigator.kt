package com.gox.app.ui.otpactivity

interface OtpVerificationNavigator{

    fun checkConfrimPassword(newPwd: String?, confrimPwd: String?): Boolean
}