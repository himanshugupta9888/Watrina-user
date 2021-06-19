package com.gox.app.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.base.BaseViewModel
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.basemodule.data.Constant


class UserDashboardViewModel : BaseViewModel<UserDashboardViewNavigator>() {

    var menuResponse = MutableLiveData<HomeMenuResponse>()
    var errorResponse = MutableLiveData<String>()
    var selectedFilterService = MutableLiveData<String>()

    fun moveToHome() {
        navigator.gotoHomeFragment()
    }

    fun moveToOrder() {
        navigator.gotOrderFragment()
    }

    fun moveToNotification() {
        navigator.goToNotificationFragment()
    }

    fun moveToAccount() {
        navigator.goToAccountFragment()
    }

    fun getMenu() {
        getCompositeDisposable().add(AppRepository.instance()
                .getMenus(this, Constant.M_TOKEN))
    }

    fun getMenuResponse(): LiveData<HomeMenuResponse> {
        return menuResponse
    }
}