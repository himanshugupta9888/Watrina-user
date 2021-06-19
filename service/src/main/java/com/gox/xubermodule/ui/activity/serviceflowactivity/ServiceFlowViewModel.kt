package com.gox.xubermodule.ui.activity.serviceflowactivity

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.default
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.RequestCancelModel
import com.gox.xubermodule.data.model.ResReasonModel
import com.gox.xubermodule.data.model.ServiceCheckReqModel
import okhttp3.ResponseBody
import com.gox.basemodule.model.ReqPaymentUpdateModel
import com.gox.xubermodule.data.model.ReqTips

class ServiceFlowViewModel : BaseViewModel<ServiceFlowNavigator>() {

    var addressvalue: ObservableField<String> = ObservableField("")
    var sourceEditTextValue = MutableLiveData<String>().default("")
    var sourceLat: ObservableField<String> = ObservableField("")
    var sourceLon: ObservableField<String> = ObservableField("")
    var destinationLon: ObservableField<String> = ObservableField("")
    var destinationLat: ObservableField<String> = ObservableField("")
    var paymentType = MutableLiveData<String>()
    var destinationaddressvalue: ObservableField<String> = ObservableField("")
    var onRide: ObservableBoolean = ObservableBoolean(false)
    var requestID = MutableLiveData<Int>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var checkRequestResponse = MutableLiveData<ServiceCheckReqModel>()
    var ratingResponseBody = MutableLiveData<ResponseBody>()
    var loading = MutableLiveData<Boolean>()
    val successResponse = MutableLiveData<ResCommonSuccessModel>()
    val paymentResponse = MutableLiveData<ResCommonSuccessModel>()
    val paymentChangeSuccessResponse = MutableLiveData<ResCommonSuccessModel>()
    var currentStatus = MutableLiveData<String>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var polyLineSrc = MutableLiveData<LatLng>()
    var polyLineDest = MutableLiveData<LatLng>()

    val mReasonResponseData = MutableLiveData<ResReasonModel>()
    fun pickLocation() {
        navigator.goToLocationPick()
    }


    fun pickSourceLocation() {
        navigator.goToSourceLocationPick()
    }

    fun goBack() {
        navigator.goBack()
    }




    fun setRide(isRideON: Boolean) {
        onRide.set(isRideON)
        onRide.notifyChange()
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        sourceEditTextValue.value = s.toString()
    }


    fun setAddress(address: String) {
        addressvalue.set(address)
    }




    fun getCheckRequest() {
        getCompositeDisposable().add(mRepository.getCheckRequest(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }


    fun submitRating(id: String, rating: Int, comment: String, admin_service_id: String) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("id", admin_service_id)
        hashMap.put("rating", rating)
        hashMap.put("comment", comment)
        hashMap.put("admin_service", Constant.ModuleTypes.SERVICE)

        getCompositeDisposable().add(mRepository.submitRating(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
    }


    fun cancelRideRequest(requestCancelModel: RequestCancelModel) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = requestCancelModel.requestId.toString()
        if (requestCancelModel.reason != "" && requestCancelModel.reason != null) {
            hashMap["reason"] = requestCancelModel.reason.toString()
        }
        getCompositeDisposable().add(mRepository.cancelRequest(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
    }

    fun getReasonList(type: String) {
        getCompositeDisposable().add(mRepository.getReasonList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), type))
    }

    fun changePayment(reqPaymentUpdateModel: ReqPaymentUpdateModel) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id"] = reqPaymentUpdateModel.requestId!!.toInt()
        hashMap["payment_mode"] = reqPaymentUpdateModel.paymentMode.toString()
        if (reqPaymentUpdateModel.cardId != null) {
            hashMap["card_id"] = reqPaymentUpdateModel.cardId.toString()
        }
        getCompositeDisposable().add(mRepository.changePayment(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))

    }

    fun setCardPayment(reqTips: ReqTips) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id"] = reqTips.requestId.toString()
        if(reqTips.cardId!=null){
            hashMap["card_id  "] = reqTips.cardId.toString()
        }
        if(reqTips.tipsAmount!=null){
            hashMap["tips"] = reqTips.tipsAmount!!.toInt()
        }
        getCompositeDisposable().add(mRepository.setCardPayment(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(),hashMap))
    }


}