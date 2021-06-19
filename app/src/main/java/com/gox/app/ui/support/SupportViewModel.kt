package com.gox.app.ui.support

import com.gox.basemodule.base.BaseViewModel

public class SupportViewModel : BaseViewModel<SupportNavigator>() {
    fun makeCall() {
        navigator.goToPhoneCall()
    }

    fun sendMail() {
        navigator.goToMail()
    }

    fun openWebsite() {
        navigator.goToWebsite()

    }
}