package com.gox.app.ui.order_fragment

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.default
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.HistoryModel

class OrderFragmentViewModel : BaseViewModel<OrderFragmentNavigator>() {

    val appRepository = AppRepository.instance()
    val errorResponse = MutableLiveData<String>().default("")
    val historyResponse = MutableLiveData<HistoryModel>()
    var loadingProgress = MutableLiveData<Boolean>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    lateinit var selectedFilterService: String

    fun moveToCurrentOrderList() {
        navigator.goToCurrentOrder()
    }

    fun moveToPastOrderList() {
        navigator.goToPastOrder()
    }

    fun moveToUpcomingOrderList() {
        navigator.goToUpcomingOrder()
    }

    fun showFilter() {
        navigator.openFilterlayout()
    }

    fun filterApplied() {

    }

    fun getHistoryList() {
        getCompositeDisposable().add(AppRepository.instance()
                .getHistoryList(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN
                        , "").toString(),
                        "past", "10", "0","" ))
    }
}
