package com.gox.xubermodule.ui.activity.provierlistactivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.ProviderListModel
import com.gox.xubermodule.data.model.XuberServiceClass

class ProvidersViewModel : BaseViewModel<ProvidersNavigator>() {
    var providerListData = MutableLiveData<ProviderListModel.ResponseData>()

    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var providerListResponse = MutableLiveData<ProviderListModel>()
    var loaderLiveData=MutableLiveData<Boolean>()

    fun logOut(isTrue:Boolean){
        navigator.logout(isTrue)
    }

    fun getProviderList() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("lat", XuberServiceClass.sourceLat)
        hashMap.put("long", XuberServiceClass.sourceLng)
        hashMap.put("id", XuberServiceClass.subServiceID.toString())
        loaderLiveData.value=true
        getCompositeDisposable().add(mRepository.getProviderList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
    }

    fun getProviderResponse(): MutableLiveData<ProviderListModel> {
        return providerListResponse
    }
}
