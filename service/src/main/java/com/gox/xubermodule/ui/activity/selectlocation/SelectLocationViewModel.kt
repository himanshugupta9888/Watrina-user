package com.gox.xubermodule.ui.activity.selectlocation

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.default
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.FavoriteAddressResponse

class SelectLocationViewModel : BaseViewModel<SelectLocationNavigator>() {

    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var errorResponse = MutableLiveData<String>()
    var addressListResponse = MutableLiveData<FavoriteAddressResponse>()
    var loaderLiveData=MutableLiveData<Boolean>().default(false)

    fun getAddressList() {
        loaderLiveData.value=true
        getCompositeDisposable().add(mRepository.getAddressList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getAddressesResponse(): MutableLiveData<FavoriteAddressResponse> {
        return addressListResponse
    }
}
