package com.gox.basemodule.common.payment.transaction

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.repositary.BaseModuleRepository
import com.gox.basemodule.common.payment.model.WalletTransactionList

class TransactionViewModel : BaseViewModel<TransactionNavigator>() {
    var transcationLiveResponse = MutableLiveData<WalletTransactionList>()
    var errorResponse = MutableLiveData<String>()
    val appRepository = BaseModuleRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var showLoading=MutableLiveData<Boolean>()

    fun callTranscationApi() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "100")
        hashMap.put("offset", "0")

        getCompositeDisposable().add(appRepository.getTransaction(this,
                Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()
                , hashMap))
    }
}

