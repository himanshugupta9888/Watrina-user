package com.gox.app.ui.currentorder_fragment

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.TransportHistory

class CurrentOrderViewModel : BaseViewModel<CurrentOrderNavigator>() {

    private val appRepository = AppRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var transportCurrentHistoryResponse = MutableLiveData<TransportHistory>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()

    fun openDetailPage() {
        navigator.goToDetailPage()
    }


    fun getTransportCurrentHistory(selectedservice: String,offset :String) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "10")
        hashMap.put("offset", offset)
        hashMap.put("type", "current")
        getCompositeDisposable().add(appRepository
                .getTransaportCurrentHistory(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap, selectedservice))

    }

    fun getServiceCurrentHistory(selectedservice: String) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "100")
        hashMap.put("offset", "0")
        hashMap.put("type", "current")
        getCompositeDisposable().add(appRepository
                .getServiceCurrentHistory(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }
}