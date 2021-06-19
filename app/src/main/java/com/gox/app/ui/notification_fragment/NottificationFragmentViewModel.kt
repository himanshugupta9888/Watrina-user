package com.gox.app.ui.notification_fragment

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.NotificationNewResponse

class NotificationFragmentViewModel : BaseViewModel<NotificationFragmentNavigator>() {

    private val appRepository = AppRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var notificationResponse = MutableLiveData<NotificationNewResponse>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()


    fun moveToDetailPage() {
        navigator.goToDetailPage()
    }

    fun getNotificationList() {
        loadingProgress.value = true
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["limit"] = "100"
        hashMap["offset"] = "0"
        getCompositeDisposable().add(appRepository
                .getNotification(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),hashMap))
    }

}