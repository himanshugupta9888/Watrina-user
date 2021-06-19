package com.gox.app.data.repositary

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gox.app.data.repositary.remote.ApiInterface
import com.gox.app.ui.changepasswordactivity.ChangePasswordViewModel
import com.gox.app.ui.currentorder_fragment.CurrentOrderViewModel
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.app.ui.forgotPasswordActivity.ForgotPasswordViewModel
import com.gox.app.ui.historydetailactivity.HistoryDetailViewModel
import com.gox.app.ui.home_fragment.HomeFragmentViewModel
import com.gox.app.ui.invitereferals.InviteReferalsViewModel
import com.gox.app.ui.myaccount_fragment.MyAccountFragmentViewModel
import com.gox.app.ui.notification_fragment.NotificationFragmentViewModel
import com.gox.app.ui.order_fragment.OrderFragmentViewModel
import com.gox.app.ui.otpactivity.OtpVerificationViewModel
import com.gox.app.ui.pastorder_fragment.PastOrderViewModel
import com.gox.app.ui.profile.ProfileViewModel
import com.gox.app.ui.signin.SigninViewModel
import com.gox.app.ui.signup.SignupViewModel
import com.gox.app.ui.splash.SplashViewModel
import com.gox.app.ui.upcoming_fragment.UpcomingFragmentViewmodel
import com.gox.app.ui.verifyotp.VerifyOTPViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.repositary.BaseRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import retrofit2.http.PartMap


class AppRepository : BaseRepository() {

    fun postSignIn(viewModel: SigninViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .signIn(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loginResponse.value = it
                }, {
                    viewModel.getErrorObservable().value = getErrorMessage(it)
                })
    }

    fun socialLogin(viewModel: SigninViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .socialLogin(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loginResponse.value = it
                }, {
                    viewModel.getErrorObservable().value = getErrorMessage(it)
                })
    }


    fun signUp(viewModel: SignupViewModel, @PartMap params: HashMap<String, RequestBody>, @Part image: MultipartBody.Part?): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .signUp(params, image!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.signupResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun sendOTP(listener: ApiListener, @PartMap params: HashMap<String, RequestBody>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .sendOTP(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    listener.onSuccess(it)
                }, {
                    listener.onError(it)
                })
    }

    fun verifyOTP(viewModel: VerifyOTPViewModel, @PartMap params: HashMap<String, RequestBody>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .verifyOTP(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.verifyOTPResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun signUpwithoutImage(viewModel: SignupViewModel, @PartMap params: HashMap<String, RequestBody>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .signUpWithOutImage(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.signupResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun countryList(viewModel: ViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .countryList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is SignupViewModel) {
                        viewModel.loadingProgress.value = false
                        viewModel.countryListResponse.value = it
                    } else if (viewModel is ProfileViewModel) {
                        viewModel.loadingProgress.value = false
                        viewModel.countryListResponse.value = it
                    } else if (viewModel is HomeFragmentViewModel) {
                        viewModel.loadingProgress.value = false
                        viewModel.countryListResponse.value = it
                    }
                }, {
                    if (viewModel is SignupViewModel) {
                        viewModel.loadingProgress.value = false
                        viewModel.errorResponse.value = getErrorMessage(it)
                    } else if (viewModel is ProfileViewModel) {
                        viewModel.loadingProgress.value = false
                        viewModel.errorResponse.value = getErrorMessage(it)
                    }

                })
    }

    fun verifyMobilePhone(viewModel: SignupViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .verifyMobilePhone(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.verifyEmailPhone.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }


    fun getMenus(viewModel: ViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getMenu(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is UserDashboardViewModel) {
                        viewModel.menuResponse.value = it
                    } else if (viewModel is HomeFragmentViewModel) {
                        viewModel.menuResponse.value = it
                    }
                }, {
                    if (viewModel is UserDashboardViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                    } else if (viewModel is HomeFragmentViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                    }
                })
    }

    fun getUserProfile(viewModel: ViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getUserProfile(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is ProfileViewModel) {
                        viewModel.mProfileResponse.value = it
                        viewModel.loadingProgress.value = false
                    } else if (viewModel is InviteReferalsViewModel) {
                        viewModel.mProfileResponse.value = it
                        viewModel.loadingProgress.value = false
                    } else if (viewModel is HomeFragmentViewModel) {
                        viewModel.mProfileResponse.value = it
                        viewModel.loadingProgress.value = false
                    }
                }, {
                    if (viewModel is ProfileViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                        viewModel.loadingProgress.value = false
                    } else if (viewModel is InviteReferalsViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                        viewModel.loadingProgress.value = false
                    } else if (viewModel is HomeFragmentViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                        viewModel.loadingProgress.value = false
                    }
                })
    }


    fun updateCity(viewModel: HomeFragmentViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .updateCity(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }


    fun getBaseConfig(viewModel: SplashViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(ApiInterface::class.java)
                .baseApiConfig(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.baseApiResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun profileUpdate(viewModel: ProfileViewModel, token: String, @PartMap params: HashMap<String, RequestBody>, @Part image: MultipartBody.Part): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .profileUpdate(token, params, image)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mProfileUpdateResponse.value = it
                    viewModel.loadingProgress.value = false
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun profileWithoutImageUpdate(viewModel: ProfileViewModel, token: String, @PartMap params: HashMap<String, RequestBody>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .profilewithOutImageUpdate(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mProfileUpdateResponse.value = it
                    viewModel.loadingProgress.value = false
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun forgotPassword(viewModel: ForgotPasswordViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .forgotPassword(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.forgotPasswordResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })

    }

    fun resetPassword(viewModel: OtpVerificationViewModel, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .resetPassword(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.otpResetPasswordResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getNotification(viewModel: NotificationFragmentViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getnotification(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.notificationResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun changePassword(viewModel: ChangePasswordViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .changePassword(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.changePasswordResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    /*fun getCardList(viewModel: WalletViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getCardList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cardResponseData.postValue(it)
                }, {

                })
    }

    fun addWalletAmount(viewModel: WalletViewModel, params: HashMap<String, String>, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .addWalletMoney(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.walletLiveResponse.postValue(it)
                }, {
                    viewModel.navigator.showErrorMsg(getErrorMessage(it)!!)
                })
    }

  */
    fun getTransaportHistory(viewModel: PastOrderViewModel, token: String, params: HashMap<String, String>
                             , selectedservice: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getTransportHistory(token, selectedservice, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.transportHistoryResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getUpcmomingHistory(viewModel: UpcomingFragmentViewmodel, token: String, params: HashMap<String, String>
                            , selectedservice: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getTransportHistory(token, selectedservice, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.upComingHistoryResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getHistoryDetail(viewModel: HistoryDetailViewModel, token: String, selected_id: String, servicetype: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getHistoryDetail(token, selected_id, servicetype)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.historyDetailResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getUpcomingHistory(viewModel: UpcomingFragmentViewmodel, token: String, hashMap: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getTransportUpcomingHistory(token, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.upComingHistoryResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getTransaportCurrentHistory(viewModel: CurrentOrderViewModel, token: String, hashMap: HashMap<String, String>
                                    , selectedservice: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getTransportHistory(token, selectedservice, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.transportCurrentHistoryResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getUpcomingHistoryDetail(viewModel: HistoryDetailViewModel, token: String, selectedID: String, servicetype: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getUpcomingHistoryDetail(token, selectedID, servicetype)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.historyUpcomingDetailResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    Log.d("_D_ERROR", it.message)
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }


    fun cancelScheduleRequest(viewModel: HistoryDetailViewModel, token: String, servicetype: String, hashMap: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .cancelSchedule(token, servicetype, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cancelSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun getServiceCurrentHistory(viewModel: CurrentOrderViewModel, token: String, hashMap: HashMap<String
            , String>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getServiceHistory(token, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.transportCurrentHistoryResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getDisputeList(viewModel: HistoryDetailViewModel, token: String, servicename: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getDisputeList(token, servicename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.disputeListData.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun addDispute(viewModel: HistoryDetailViewModel, token: String
                   , hashMap: HashMap<String, String>
                   , servicename: String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .addDispute(token, hashMap, servicename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.addDisputeResponse.value = it

                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.postValue(getErrorMessage(it))
                })
    }

    fun getDisputeStatus(viewModel: HistoryDetailViewModel, token: String
                         , selected_id: String
                         , servicename: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getDisputeStatus(token, selected_id, servicename)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.disputeStatusResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun addLostItem(viewModel: HistoryDetailViewModel, token: String, hashMap: HashMap<String, String>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .addLostItem(token, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.loadingProgress.value = false
                    viewModel.addLostItemResponse.value = it
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getHistoryList(viewModel: OrderFragmentViewModel, token: String, serviceType: String, limit: String,
                       offset: String, orderType: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .getHistoryList(token, serviceType, limit, offset, orderType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.historyResponse.postValue(it)
                }, {
                    viewModel.loadingProgress.value = false
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }


    fun logout(viewModel: ViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, ApiInterface::class.java)
                .logout(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (viewModel is MyAccountFragmentViewModel) {
                        viewModel.successResponse.value = it
                    }
                }, {
                    if (viewModel is MyAccountFragmentViewModel) {
                        viewModel.errorResponse.value = getErrorMessage(it)
                    }
                })
    }

    companion object {
        private var appRepository: AppRepository? = null

        fun instance(): AppRepository {
            if (appRepository == null) synchronized(AppRepository) {
                appRepository = AppRepository()
            }
            return appRepository!!
        }
    }
}
