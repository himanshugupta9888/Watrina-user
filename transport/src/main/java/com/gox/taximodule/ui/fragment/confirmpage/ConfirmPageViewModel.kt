package com.gox.taximodule.ui.fragment.confirmpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.taximodule.data.TaxiRepository
import com.gox.taximodule.data.dto.EstimateFareResponse
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.data.dto.response.ResRideRequestModel
import com.gox.taximodule.data.dto.response.ResWalletModel
import io.reactivex.disposables.Disposable

class ConfirmPageViewModel : BaseViewModel<ConfirmPageNavigator>() {
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    val estimateResponse = MutableLiveData<EstimateFareResponse>()
    val startRideResonse = MutableLiveData<ResRideRequestModel>()
    val walletResponse = MutableLiveData<ResWalletModel>()
    var errorResponse = MutableLiveData<String>()
    var loadingProgress = MutableLiveData<Boolean>()
    private val mRepository = TaxiRepository.instance()
    private lateinit var subscription: Disposable

    fun searchProviders() = navigator.searchProviders()

    fun openBookSomeOneUI() = navigator.openBookSomeOneUI()

    fun openScheduleUI() = navigator.openScheduleUI()

    fun goToHome() = navigator.goToHome()

    fun logout(isTrue:Boolean) = navigator.isNeedToLogout(isTrue)

    fun viewCoupons() = navigator.viewCoupons()

  /*  fun getEstimate(reqEstimateModel: ReqEstimateModel) {
        loadingProgress.value = true
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["s_latitude"] = reqEstimateModel.sourceLattitude.toString()
        hashMap["s_longitude"] = reqEstimateModel.sourceLongitude.toString()
        hashMap["service_type"] = reqEstimateModel.serviceType.toString()
        hashMap["d_latitude"] = reqEstimateModel.destinationLatitude.toString()
        hashMap["d_longitude"] = reqEstimateModel.destinationLongitude.toString()
        hashMap["payment_mode"] = reqEstimateModel.paymentMode.toString()
        //preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString())
        getCompositeDisposable().add(mRepository.getEstimation(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(),hashMap))
    }

    fun getEstimateResponse(): LiveData<EstimateFareResponse> {
        return estimateResponse
    }*/

    fun startRide(reqEstimateModel: ReqEstimateModel) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["s_latitude"] = reqEstimateModel.sourceLattitude.toString()
        hashMap["s_longitude"] = reqEstimateModel.sourceLongitude.toString()
        hashMap["service_type"] = reqEstimateModel.serviceType.toString()
        hashMap["d_latitude"] = reqEstimateModel.destinationLatitude.toString()
        hashMap["d_longitude"] = reqEstimateModel.destinationLongitude.toString()
        hashMap["payment_mode"] = reqEstimateModel.paymentMode.toString()
        hashMap["card_id"] = reqEstimateModel.card_id.toString()
        hashMap["s_address"] = reqEstimateModel.sourceAddress.toString()
        hashMap["d_address"] = reqEstimateModel.destinationAddress.toString()
        hashMap["distance"] = reqEstimateModel.distance.toString()
        hashMap["use_wallet"] = reqEstimateModel.useWallet.toString()
        hashMap["wheelchair"] = reqEstimateModel.wheelChair!!
        hashMap["child_seat"] = reqEstimateModel.childSeat!!
        hashMap["ride_type_id"] = reqEstimateModel.rideTypeId
        hashMap["promocode_id"] = reqEstimateModel.promoId
        if (reqEstimateModel.scheduleDate != "" && reqEstimateModel.scheduleTime != "") {
            hashMap["schedule_date"] = reqEstimateModel.scheduleDate
            hashMap["schedule_time"] = reqEstimateModel.scheduleTime
        }
        if (reqEstimateModel.someone == 1) {
            hashMap["someone"] = reqEstimateModel.someone.toString()
            hashMap["someone_email"] = reqEstimateModel.someOneEmail.toString()
            hashMap["someone_mobile"] = reqEstimateModel.someOneMobile.toString()
        }

      /*  subscription = serviceApi.startRide(Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    startRideResonse.value = result
                }, {
                    errorResponse.value = it
                })*/

        getCompositeDisposable().add(mRepository.startRide(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(),hashMap))
    }

    fun getWalletData() {
        /*  subscription = serviceApi.getWalletData(Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString())
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe({ result ->
                      walletResponse.value = result
                      loadingProgress.value = false
                  },
                          {
                              errorResponse.value = it
                          })*/
        getCompositeDisposable().add(mRepository.getWalletData(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getWalletResponse(): LiveData<ResWalletModel> {
        return walletResponse
    }


}
