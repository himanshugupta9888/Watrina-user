package com.gox.foodiemodule.ui.searchResturantActivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.ResturantListModel

class SearchRestaturantsViewModel : BaseViewModel<SearchRestaturantsNavigator>() {

    var resturantListResponse = MutableLiveData<ResturantListModel>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    fun serachResturants() {

    }

    fun openRestaturantDetailPage() {
        navigator.goToResturantDetail()
    }

    fun getSearchResturantList(q: String, storetype: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("q", q)
        hashMap.put("t", storetype)
        loadingProgress!!.value = true
        var id:String = preferenceHelper.getValue(PreferenceKey.SERVICE_ID, "1").toString()
        getCompositeDisposable().add(mRepository
                .getSearchResturantList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , id, hashMap))
    }

}