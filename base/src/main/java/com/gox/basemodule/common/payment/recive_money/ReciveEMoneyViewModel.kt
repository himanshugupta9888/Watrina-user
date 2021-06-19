package com.gox.basemodule.common.payment.recive_money

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.model.ProfileResponse
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.repositary.BaseModuleRepository

class  ReciveEMoneyViewModel: BaseViewModel<ReciveEMoneyNavigator>(){
    val appRepository = BaseModuleRepository.instance()
    var showLoading = MutableLiveData<Boolean>()
    var mProfileResponse = MutableLiveData<ProfileResponse>()
    var errorResponse = MutableLiveData<String>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    fun getUserProfile(){
        getCompositeDisposable().add(appRepository.getProfile(object : ApiListener {
            override fun onSuccess(successData: Any) {
                mProfileResponse.value = successData as ProfileResponse
                showLoading.postValue(false)
            }

            override fun onError(failData: Throwable) {
                errorResponse.value = getErrorMessage(failData)
                showLoading.postValue(false)
            }
        }))
    }

}