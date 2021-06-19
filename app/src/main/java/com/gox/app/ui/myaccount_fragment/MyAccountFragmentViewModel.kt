package com.gox.app.ui.myaccount_fragment

import androidx.lifecycle.MutableLiveData
import com.gox.app.data.repositary.AppRepository
import com.gox.basemodule.model.ResCommonSuccussModel
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue

class MyAccountFragmentViewModel : BaseViewModel<MyAccountFragmentNavigator>() {
    private val appRepository = AppRepository.instance()
    val successResponse = MutableLiveData<ResCommonSuccussModel>()
    val errorResponse = MutableLiveData<String>()
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    fun openProfile() {
        navigator.goToProfile()
    }

    fun openManageAddress() {
        navigator.goToManageAddress()
    }

    fun openPayment() {
        navigator.goToPayment()
    }

    fun openInviteRefferals() {
        navigator.goToInviteRefferals()
    }

    fun openPrivacyPolicy() {
        navigator.gToprivacyPolicy()
    }

    fun openSupport() {
        navigator.goToSupport()
    }

    fun openLanguage() {
        navigator.openLanguage()
    }

    fun openCardPage() {
        navigator.goToCardList()
    }

    fun Logout() {
        getCompositeDisposable().add(appRepository
                .logout(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

}