package com.gox.xubermodule.ui.activity.xubersubserviceactivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.SubServiceModel

class XuberSubServiceViewModel : BaseViewModel<XuberSubServiceNavigator>() {

    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var subServiceResponse = MutableLiveData<SubServiceModel>()

    fun onClickBookNow() {
        navigator.bookNowService()
    }

    fun logout(isTrue:Boolean){
       navigator.logoit(isTrue)
    }

    fun onClickScheduleNow(){
        navigator.scheduleService()
    }

    fun getServiceCategory(mServiceID: Int,cityID:String) {
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .getSubServiceList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , mServiceID,cityID))

    }
}
