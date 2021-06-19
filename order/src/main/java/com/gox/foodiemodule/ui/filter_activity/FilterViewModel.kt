package com.gox.foodiemodule.ui.filter_activity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.CusineListModel

class FilterViewModel : BaseViewModel<FilterNavigator>() {

    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var cusineListData = MutableLiveData<CusineListModel>()

    fun getCusineList(id: String) {
        getCompositeDisposable().add(mRepository.getCusineList(this, id))
    }
}
