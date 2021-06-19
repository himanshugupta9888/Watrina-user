package com.gox.taximodule.ui.activity.main

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
import com.gox.taximodule.data.TaxiRepository
import com.gox.taximodule.data.dto.*
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.data.dto.request.ReqPaymentUpdateModel
import com.gox.taximodule.data.dto.request.ReqRatingModel
import com.gox.taximodule.data.dto.request.ReqScheduleRide
import com.gox.taximodule.data.dto.response.ResCheckRequest
import com.gox.taximodule.data.dto.response.ResReasonModel
import com.gox.taximodule.data.dto.response.ServiceResponse

class TaxiMainViewModel : BaseViewModel<TaxiMainNavigator>() {

    // var driverStatus: ObservableField<String> = ObservableField("Driver accepted your request")
    var estimateResponse = MutableLiveData<EstimateFareResponse>()
    var addressvalue: ObservableField<String> = ObservableField("")
    var sourceEditTextValue = MutableLiveData<String>().default("")
    var sourceLat: ObservableField<String> = ObservableField("")
    var sourceLon: ObservableField<String> = ObservableField("")
    var destinationLon: ObservableField<String> = ObservableField("")
    var destinationLat: ObservableField<String> = ObservableField("")
    var serviceType: ObservableField<String> = ObservableField("")
    var paymentType: ObservableField<String> = ObservableField("")
    var card_id: ObservableField<String> = ObservableField("")
    var someOnemodel = MutableLiveData<ReqSomeOneModel>()
    var scheduleModel = MutableLiveData<ReqScheduleRide>()
    val successResponse = MutableLiveData<ResCommonSuccessModel>()
    val mRatingSuccessResponse = MutableLiveData<ResCommonSuccessModel>()
    val mCheckRequestResponse = MutableLiveData<ResCheckRequest>()
    var destinationaddressvalue: ObservableField<String> = ObservableField("")
    var destinationaddress = MutableLiveData<String>()
    var onRide: ObservableBoolean = ObservableBoolean(false)
    var errorResponse = MutableLiveData<String>()
    var rateCardData = MutableLiveData<ServiceModel>()
    var requestID = MutableLiveData<Int>()
    var serviceResponse = MutableLiveData<ServiceResponse>()
    var serviceList = ArrayList<ServiceModel>()
    val promoCode = MutableLiveData<EstimateFareResponse.ResponseData.Promocode>()
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var loadingProgress = MutableLiveData<Boolean>()
    val mReasonResponseData = MutableLiveData<ResReasonModel>()
    var currentStatus = MutableLiveData<String>()
    var showServiceFlag = MutableLiveData<String>()
    var mAddTips = MutableLiveData<String>()
    var rideTypeId = MutableLiveData<Int>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var polyLineSrc = MutableLiveData<LatLng>()
    var polyLineDest = MutableLiveData<LatLng>()
    var providerName : ObservableField<String> = ObservableField("")
    private val mRepository = TaxiRepository.instance()
    var driverSpeed = MutableLiveData<Double>()
    var tempSrc = MutableLiveData<LatLng>()
    var tempDest = MutableLiveData<LatLng>()

    fun pickLocation() {
        navigator.goToLocationPick()
    }


    fun pickSourceLocation() {
        navigator.goToSourceLocationPick()
    }

    fun goBack() {
        navigator.goBack()
    }

    fun showCurrentLocation() {
        navigator.showCurrentLocation()
    }

    fun moveStatusFlow() {
        navigator.moveStatusFlow()
    }


    fun showServiceList() {
        navigator.showService()
    }

    /* fun setDriverStatus(status: String) {
         driverStatus.set(status)
         driverStatus.notifyChange()
     }*/

    fun getShowFlag(): LiveData<String> {
        return showServiceFlag
    }

    fun setRideTypeId(serviceId: Int) {
        rideTypeId.value = serviceId
    }

    fun getRideTypeId(): LiveData<Int> {
        return rideTypeId
    }

    fun setRide(isRideON: Boolean) {
        onRide.set(isRideON)
        onRide.notifyChange()
    }

    fun getText(): LiveData<String> {
        return sourceEditTextValue
    }

    fun setAddress(address: String) {
        addressvalue.set(address)
    }

    fun setDestinationAddress(address: String) {
        destinationaddressvalue.set(address)
        destinationaddress.value = address
    }


    fun setRateCard(model: ServiceModel) {
        rateCardData.value = model
    }

    fun getRateCard(): MutableLiveData<ServiceModel> {
        return rateCardData
    }

    fun setRequestId(id: Int) {
        requestID.value = id
    }

    fun getRequestId(): MutableLiveData<Int> {
        return requestID
    }

    fun cancelRideRequest(reqEstimateModel: ReqEstimateModel) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = reqEstimateModel.id.toString()
        if (reqEstimateModel.reason != "" && reqEstimateModel.reason != null) {
            hashMap["reason"] = reqEstimateModel.reason.toString()
        }
        getCompositeDisposable().add(mRepository.cancelRide(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getCancelResponse(): MutableLiveData<ResCommonSuccessModel> {
        return successResponse
    }


    fun getCheckRequest() {
        getCompositeDisposable().add(mRepository.checkRequest(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getCheckRequestResponse(): MutableLiveData<ResCheckRequest> {
        return mCheckRequestResponse
    }

    fun setRating(reqRatingModel: ReqRatingModel) {
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["id"] = reqRatingModel.requesterId.toString()
        hashMap["rating"] = reqRatingModel.rating!!.toInt()
        hashMap["comment"] = reqRatingModel.comment.toString()
        hashMap["admin_service"] = Constant.ModuleTypes.TRANSPORT
        getCompositeDisposable().add(mRepository.setRating(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getRatingResponse(): MutableLiveData<ResCommonSuccessModel> {
        return mRatingSuccessResponse
    }

    fun getServices(serviceId: Int, lat: Double, lng: Double,cityID:String) {
        getCompositeDisposable().add(mRepository.getServices(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), serviceId,
                lat,
                lng,cityID))
    }

    fun getServicesResponse(): LiveData<ServiceResponse> {
        return serviceResponse
    }

    fun getServiceList(): MutableList<ServiceModel> {
        return serviceList
    }

    fun setSomeoneData(reqSomeOneModel: ReqSomeOneModel) {
        someOnemodel.value = reqSomeOneModel
    }

    fun getSomeOneData(): MutableLiveData<ReqSomeOneModel> {
        return someOnemodel
    }

    fun setScheduleDateTime(reqScheduleRide: ReqScheduleRide) {
        scheduleModel.value = reqScheduleRide
    }

    fun getScheduleDateTimeData(): MutableLiveData<ReqScheduleRide> {
        return scheduleModel
    }

    fun getEstimate(reqEstimateModel: ReqEstimateModel) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["s_latitude"] = reqEstimateModel.sourceLattitude.toString()
        hashMap["s_longitude"] = reqEstimateModel.sourceLongitude.toString()
        hashMap["service_type"] = reqEstimateModel.serviceType.toString()
        hashMap["d_latitude"] = reqEstimateModel.destinationLatitude.toString()
        hashMap["d_longitude"] = reqEstimateModel.destinationLongitude.toString()
        hashMap["payment_mode"] = reqEstimateModel.paymentMode.toString()
        if(!reqEstimateModel.cityID.isNullOrEmpty())
        hashMap["city_id"]=reqEstimateModel.cityID.toString()
        //preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString())

        getCompositeDisposable().add(mRepository.getEstimate(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getEstimateResponse(): LiveData<EstimateFareResponse> {
        return estimateResponse
    }

    fun setPromoCode(promoData: EstimateFareResponse.ResponseData.Promocode) {
        promoCode.value = promoData
    }

    fun getPromoCode(): LiveData<EstimateFareResponse.ResponseData.Promocode> {
        return promoCode
    }


    fun getReason(type: String) {
        getCompositeDisposable().add(mRepository.getReasons(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), type))
    }

    fun getReasonResponse(): MutableLiveData<ResReasonModel> {
        return mReasonResponseData
    }

}