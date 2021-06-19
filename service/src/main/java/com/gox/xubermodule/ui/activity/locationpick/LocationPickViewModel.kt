package com.gox.xubermodule.ui.activity.locationpick

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.FavoriteAddressResponse

class LocationPickViewModel : BaseViewModel<LocationPickNavigator>() {

    private val mRepository = ServiceRepository.instance()
    var errorResponse = MutableLiveData<String>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var addressListResponse = MutableLiveData<FavoriteAddressResponse>()

    fun getAddressList() {
        getCompositeDisposable().add(mRepository.getAddressList(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getAddressesResponse(): MutableLiveData<FavoriteAddressResponse> {
        return addressListResponse
    }
}
