package com.gox.xubermodule.ui.activity.confirmbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.PromoCodeListModel
import com.gox.xubermodule.data.model.PromocodeModel
import com.gox.xubermodule.data.model.SendRequestModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ConfirmBookingViewModel : BaseViewModel<ConfirmBookingNavigator>() {
    var errorResponse = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var promoCodeResponse = MutableLiveData<PromoCodeListModel>()
    var sendRequestModel = MutableLiveData<SendRequestModel>()
    private var selectedPromoCode = MutableLiveData<PromocodeModel>()

    fun logout(isTrue:Boolean){
        navigator.logout(isTrue)
    }

    fun getPromoCodesList() {
        getCompositeDisposable().add(mRepository.getPromoCodeList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }

    fun sendRequest(file: MultipartBody.Part?, params: HashMap<String, RequestBody>) {
            getCompositeDisposable().add(mRepository.sendServiceRequest(this, Constant.M_TOKEN +
                    preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), params, file))
    }

    fun setSelectedPromo(promo: PromocodeModel) {
        selectedPromoCode.value = promo
    }

    fun getSelectedPromo(): LiveData<PromocodeModel> {
        return selectedPromoCode
    }

    fun getPromoCode(): MutableLiveData<PromoCodeListModel> {
        return promoCodeResponse
    }
}
