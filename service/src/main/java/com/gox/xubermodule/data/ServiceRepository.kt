package com.gox.xubermodule.data

import androidx.lifecycle.ViewModel
import com.gox.basemodule.repositary.BaseRepository
import com.gox.basemodule.data.Constant
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.ui.activity.confirmbooking.ConfirmBookingViewModel
import com.gox.xubermodule.ui.activity.locationpick.LocationPickViewModel
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainModel
import com.gox.xubermodule.ui.activity.providerdetail.ProviderDetailViewModel
import com.gox.xubermodule.ui.activity.providerreviews.ProviderReviewViewModel
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersViewModel
import com.gox.xubermodule.ui.activity.selectlocation.SelectLocationViewModel
import com.gox.xubermodule.ui.activity.serviceflowactivity.ServiceFlowViewModel
import com.gox.xubermodule.ui.activity.xubersubserviceactivity.XuberSubServiceViewModel
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ServiceRepository : BaseRepository() {

    companion object {
        private var mServiceRepository: ServiceRepository? = null

        fun instance(): ServiceRepository {
            if (mServiceRepository == null) synchronized(ServiceRepository) {
                mServiceRepository = ServiceRepository()
            }
            return mServiceRepository!!
        }
    }

    fun getAddressList(viewModel: ViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getAddressList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is LocationPickViewModel)
                        viewModel.addressListResponse.value = it
                    else if (viewModel is SelectLocationViewModel) {
                        viewModel.loaderLiveData.value = false
                        viewModel.addressListResponse.value = it
                    }
                },
                        {
                            if (viewModel is LocationPickViewModel)
                                viewModel.errorResponse.value = getErrorMessage(it)
                            else if (viewModel is SelectLocationViewModel) {
                                viewModel.loaderLiveData.value=false
                                viewModel.errorResponse.value = getErrorMessage(it)
                            }
                        })
    }

    fun getSubCategoryList(viewModel: XuberMainModel, token: String, mServiceID: Int): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getSubCategory(token, mServiceID.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.subCategoryResponse.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getSubServiceList(viewModel: XuberSubServiceViewModel, token: String, mServiceID: Int,cityID:String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getSubService(token, XuberServiceClass.serviceID.toString(), mServiceID.toString(),cityID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.subServiceResponse.value = it
                }, {
                            if(it is HttpException){
                                viewModel.logout(true)
                            }
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getProviderList(viewModel: ProvidersViewModel, token: String, hashMap: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getProviderList(token, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.providerListResponse.value = it
                    viewModel.loaderLiveData.value=false
                },
                        {
                            viewModel.loaderLiveData.value=false
                            if(it is HttpException){
                                viewModel.logOut(true)
                            }
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getProviderReviews(viewModel: ViewModel, id: String, token: String, hashMap: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getReviewList(id, hashMap, token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is ProviderReviewViewModel)
                        viewModel.providerReviewResponse.value = it
                    else if (viewModel is ProviderDetailViewModel)
                        viewModel.providerReviewResponse.value = it
                },

                        {
                            if (viewModel is ProviderReviewViewModel)
                                viewModel.errorResponse.value = getErrorMessage(it)
                            else if (viewModel is ProviderDetailViewModel)
                                viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getCheckRequest(viewModel: ServiceFlowViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getCheckRequest(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.checkRequestResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getPromoCodeList(viewModel: ConfirmBookingViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getPromoCodeList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.promoCodeResponse.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun sendServiceRequest(viewModel: ConfirmBookingViewModel, token: String, params: HashMap<String, RequestBody>, image: MultipartBody.Part?): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .sendServiceRequest("${Constant.BaseUrl.SERVICE_BASE_URL}/user/service/send/request", token, params, image)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.sendRequestModel.value = it
                    viewModel.loading.value = false
                },
                        {

                            if(it is HttpException){
                                viewModel.logout(true)
                            }
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loading.value = false
                        })
    }

    fun submitRating(viewModel: ServiceFlowViewModel, token: String, params: HashMap<String, Any>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .submitRating(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.ratingResponseBody.value = it
                    viewModel.loading.value = false

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun cancelRequest(viewModel: ServiceFlowViewModel, token: String, params: HashMap<String, String>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .cancelService(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.successResponse.value = it
                    viewModel.loading.value = false

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getReasonList(viewModel: ServiceFlowViewModel, token: String, type: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getReasons(token, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mReasonResponseData.value = it
                    viewModel.loading.value = false

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getCheckRequestMain(viewModel: XuberMainModel, token: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getCheckRequest(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.checkRequestResponse.value = it

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getWallet(viewModel: ConfirmBookingViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .getPromoCodeList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.promoCodeResponse.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun changePayment(viewModel: ServiceFlowViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.SERVICE_BASE_URL, ServiceApiInterface::class.java)
                .changePayment(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.paymentChangeSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun setCardPayment(viewModel: ServiceFlowViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, ServiceApiInterface::class.java)
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
