package com.gox.foodiemodule.ui.restaurantlist_activity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.OrderCheckRequest
import com.gox.foodiemodule.data.model.PromoCodeListModel
import com.gox.foodiemodule.data.model.ResturantListModel

class RestaurantListViewModel : BaseViewModel<RestaurantListNavigator>() {

    var checkRequestResponse = MutableLiveData<OrderCheckRequest>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var resturantListResponse = MutableLiveData<ResturantListModel>()
    var promoCodeResponse = MutableLiveData<PromoCodeListModel>()

    fun openFilter() {
        navigator.goToFilter()
    }


    fun getResturantList(filter: String, qfilter: String, mServiceID: String,cityID:String) {
        loadingProgress.value = true
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("filter", filter)
        hashMap.put("qfilter", qfilter)
        hashMap.put("city_id",cityID)
        getCompositeDisposable().add(mRepository
                .getResturantList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , mServiceID, hashMap))
    }

    fun getPromocodeDetail() {
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository.getHomePromoCodeList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }

    fun getCheckRequest() {
        getCompositeDisposable().add(mRepository.getCheckRequest(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }
}