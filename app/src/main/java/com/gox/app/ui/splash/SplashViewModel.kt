package com.gox.app.ui.splash

import androidx.lifecycle.MutableLiveData
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.BaseApiResponse
import com.gox.basemodule.base.BaseViewModel

class SplashViewModel : BaseViewModel<SplashNavigator>() {

    var baseApiResponse = MutableLiveData<BaseApiResponse>()
    var errorResponse = MutableLiveData<String>()

    fun getBaseConfig() {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["salt_key"] = "MQ=="
        getCompositeDisposable().add(AppRepository.instance().getBaseConfig(this, hashMap))
    }
}