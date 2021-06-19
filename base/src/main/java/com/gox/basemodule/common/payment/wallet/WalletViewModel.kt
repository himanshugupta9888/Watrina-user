package com.gox.basemodule.common.payment.wallet

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.WebApiConstants
import com.gox.basemodule.common.payment.model.WalletResponse
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.model.ProfileResponse
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.repositary.BaseModuleRepository


class WalletViewModel(res: Resources) : BaseViewModel<WalletNavigator>() {
    var loadingProgress = MutableLiveData<Boolean>()
    var walletAmount = MutableLiveData<String>()
    var walletLiveResponse = MutableLiveData<WalletResponse>()
    var selectedStripeID = MutableLiveData<String>()
    var resources: Resources? = null
    var showLoading = MutableLiveData<Boolean>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var mProfileResponse = MutableLiveData<ProfileResponse>()


    init {
        this.resources = res
    }

    val appRepository = BaseModuleRepository.instance()

    fun amountAdd(view: View) {
        navigator.addAmount(view)
    }

    fun addWalletAmount() {
        if (walletAmount.value.isNullOrEmpty())
            navigator.showErrorMsg(resources!!.getString(R.string.empty_wallet_amount))
        else
            navigator.getCard()
    }

    fun getProfile() {
        showLoading.value = true
        getCompositeDisposable().add(appRepository.getProfile(object : ApiListener {
            override fun onSuccess(successData: Any) {
                mProfileResponse.postValue(successData as ProfileResponse)
            }

            override fun onError(error: Throwable) {
                navigator.showErrorMsg(getErrorMessage(error) ?: "")
            }
        }))
    }


    fun callAddAmtApi() {
        if (navigator.validate()) {
            showLoading.value = true
            val params = HashMap<String, String>()
            params.put(WebApiConstants.AddWallet.AMOUNT, walletAmount.value.toString())
            params.put(WebApiConstants.AddWallet.CARD_ID, selectedStripeID.value.toString())
            params.put(WebApiConstants.AddWallet.USER_TYPE, Constant.TYPE_USER)
            params.put(WebApiConstants.AddWallet.PAYMENT_MODE, Constant.PaymentMode.CARD)
            getCompositeDisposable().add(appRepository.addWalletAmount(object : ApiListener {
                override fun onSuccess(successData: Any) {
                    walletLiveResponse.postValue(successData as WalletResponse)
                }

                override fun onError(error: Throwable) {
                    navigator.showErrorMsg(getErrorMessage(error) ?: "")
                }
            }, params))
        }
    }


}