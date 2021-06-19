package com.gox.basemodule.common.payment.managepayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.model.WalletTransferModel
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.repositary.BaseModuleRepository

class ManagePaymentViewModel : BaseViewModel<ManagePaymentNavigator>() {
    val activityFlag = MutableLiveData<String>()
    var strCurrencyType=MutableLiveData<String>()

    var strSendAmount=MutableLiveData<String>()
    var errorResponse = MutableLiveData<String>()
    var showLoading = MutableLiveData<Boolean>()
    var QrLink=MutableLiveData<String>()
    val appRepository = BaseModuleRepository.instance()
    var walletTransferTransfer=MutableLiveData<WalletTransferModel>()
    var walletTransferOtherUser=MutableLiveData<JsonObject>()
    var isPaymentTransFered= MutableLiveData<Boolean>()


    fun setFlag(flag: String) {
        activityFlag.value = flag
    }

    fun getFlag(): LiveData<String> {
        return activityFlag
    }


    fun sendWalletAmount(params:HashMap<String,String>){
        showLoading.value = true
        getCompositeDisposable().add(appRepository.walletTransfer(object : ApiListener {
            override fun onSuccess(successData: Any) {
                walletTransferTransfer.postValue( successData as WalletTransferModel)
                showLoading.postValue(false)
            }

            override fun onError(failData: Throwable) {
                errorResponse.value = getErrorMessage(failData)
                showLoading.postValue(false)
            }
        },params))
    }


}