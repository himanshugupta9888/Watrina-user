package com.gox.app.ui.upcoming_fragment

import androidx.lifecycle.MutableLiveData
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.TransportHistory
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue

class UpcomingFragmentViewmodel : BaseViewModel<UpComingOrderNavigator>() {
    private val appRepository = AppRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var upComingHistoryResponse = MutableLiveData<TransportHistory>()
    var loadingProgress = MutableLiveData<Boolean>()

    var errorResponse = MutableLiveData<String>()
    fun movetoDetailPage() {

        navigator.goToDetailPage()
    }

    fun getTransportUpcomingHistory(selectedService: String, offset: String) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "10")
        hashMap.put("offset", offset)
        hashMap.put("type", "upcoming")

        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getUpcmomingHistory(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),
                        hashMap,selectedService))

    }
}
