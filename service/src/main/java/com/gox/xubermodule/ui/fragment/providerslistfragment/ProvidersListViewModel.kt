package com.gox.xubermodule.ui.fragment.providerslistfragment

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.extensions.default
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.ProviderListModel

class ProvidersListViewModel : BaseViewModel<ProviderListNavigator>() {

    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var providerListResponse = MutableLiveData<ProviderListModel>()
    var loaderLiveData=MutableLiveData<Boolean>().default(false)

   /* fun getProviderList() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("lat", XuberServiceClass.sourceLat)
        hashMap.put("long", XuberServiceClass.sourceLng)
        hashMap.put("id", XuberServiceClass.serviceID.toString())
        getCompositeDisposable().add(mRepository.getProviderList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
    }

    fun getProviderResponse(): MutableLiveData<ProviderListModel> {
        return providerListResponse
    }*/
}
