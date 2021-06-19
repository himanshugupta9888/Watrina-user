package com.gox.app.ui.pastorder_fragment

import android.preference.Preference
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.TransportHistory

public class PastOrderViewModel : BaseViewModel<PastOrderNavigator>() {

    private val appRepository = AppRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var transportHistoryResponse = MutableLiveData<TransportHistory>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()


    fun openDetailPage() {
        navigator.gotoDetailPage()

    }

    fun getTransportPastHistory(selectedService: String, offset: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "10")
        hashMap.put("offset", offset)
        hashMap.put("type", "past")
        if (offset == "0")
            loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getTransaportHistory(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap, selectedService))

    }
}