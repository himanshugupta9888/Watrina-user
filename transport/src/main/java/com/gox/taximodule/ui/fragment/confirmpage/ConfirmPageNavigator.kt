package com.gox.taximodule.ui.fragment.confirmpage

interface ConfirmPageNavigator{
    fun searchProviders()
    fun openBookSomeOneUI()
    fun openScheduleUI()
    fun goToHome()
    fun viewCoupons()
    fun isNeedToLogout(isTrue:Boolean)
}