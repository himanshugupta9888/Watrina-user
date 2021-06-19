package com.gox.taximodule.data

import android.util.Log
import com.gox.basemodule.repositary.BaseRepository
import com.gox.basemodule.data.Constant
import com.gox.taximodule.ui.activity.locationpick.LocationPickViewModel
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.fragment.confirmpage.ConfirmPageViewModel
import com.gox.taximodule.ui.fragment.invoice.InvoiceViewModel
import com.gox.taximodule.ui.fragment.tips.TipsViewModel
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class TaxiRepository : BaseRepository() {

    companion object {
        private var mTaxiRepository: TaxiRepository? = null

        fun instance(): TaxiRepository {
            if (mTaxiRepository == null) synchronized(TaxiRepository) {
                mTaxiRepository = TaxiRepository()
            }
            return mTaxiRepository!!
        }
    }

    fun getAddressList(viewModel: LocationPickViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getAddressList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.addressListResponse.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun extendTrip(viewModel: LocationPickViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .extendTrip(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.extendTripResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun cancelRide(viewModel: TaxiMainViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .cancelRide(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.successResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun checkRequest(viewModel: TaxiMainViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .checkRequest(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mCheckRequestResponse.postValue(it)
                }, {
                    viewModel.errorResponse.postValue(getErrorMessage(it))
                }

                )
    }

    fun setRating(viewModel: TaxiMainViewModel, token: String, params: HashMap<String, Any?>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .setRating(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mRatingSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun getServices(viewModel: TaxiMainViewModel, token: String, serviceId: Int, lat: Double, lng: Double,cityID:String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getServices(token, serviceId.toString(), lat.toString(), lng.toString(),cityID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.serviceResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun getEstimate(viewModel: TaxiMainViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getEstimate(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.estimateResponse.value = it
                }, {
                    if(it is HttpException){
                       Log.e("GustCode","-----"+it.code())
                    }
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun updatePayment(viewModel: InvoiceViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .updatePayment(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.successResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }
                )
    }

    fun getReasons(viewModel: TaxiMainViewModel, token: String, type: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getReasons(token, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mReasonResponseData.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun getWalletData(viewModel: ConfirmPageViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getWalletData(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.walletResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun startRide(viewModel: ConfirmPageViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .startRide(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.startRideResonse.value = it
                }, {
//                    if(it is HttpException){
//                        viewModel.logout(true)
//                    }
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun getEstimation(viewModel: ConfirmPageViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .getEstimate(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.estimateResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }


    fun setCardPayment(viewModel: InvoiceViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, TaxiApiInterface::class.java)
                .setPayment(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.paymentResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }



}