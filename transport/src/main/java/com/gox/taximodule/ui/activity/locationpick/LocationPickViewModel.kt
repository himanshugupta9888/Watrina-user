package com.gox.taximodule.ui.activity.locationpick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.taximodule.data.TaxiRepository
import com.gox.taximodule.data.dto.request.ReqExtendTripModel
import com.gox.taximodule.data.dto.response.FavoriteAddressResponse
import io.reactivex.disposables.Disposable

class LocationPickViewModel : BaseViewModel<LocationPickNavigator>() {

    private val mRepository = TaxiRepository.instance()
    var errorResponse = MutableLiveData<String>()
    var extendTripResponse = MutableLiveData<ResCommonSuccessModel>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    private lateinit var subscription: Disposable
    var addressListResponse = MutableLiveData<FavoriteAddressResponse>()

    fun getAddressList() {
        getCompositeDisposable().add(mRepository.getAddressList(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getAddressesResponse(): MutableLiveData<FavoriteAddressResponse> {
        return addressListResponse
    }

    fun extendTrip(reqExtendTripModel: ReqExtendTripModel) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = reqExtendTripModel.requestId.toString()
        hashMap["latitude"] = reqExtendTripModel.latitude.toString()
        hashMap["longitude"] = reqExtendTripModel.longitude.toString()
        hashMap["address"] = reqExtendTripModel.address.toString()

        getCompositeDisposable().add(mRepository.extendTrip(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getExtendTripResponse(): LiveData<ResCommonSuccessModel> {
        return extendTripResponse
    }
}
