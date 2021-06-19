package com.gox.foodiemodule.ui.resturantdetail_activity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.CartMenuItemModel
import com.gox.foodiemodule.data.model.ResturantDetailsModel

open class RestaturantDetailViewModel : BaseViewModel<ResturantDetailNavigator>() {

    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse =   MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var resturantDetailResponse = MutableLiveData<ResturantDetailsModel>()
    var resturantDetailResponseCustom = MutableLiveData<ResturantDetailsModel>()
    var cartMenuItemResponse = MutableLiveData<CartMenuItemModel>()
    var cartRemoveResponse = MutableLiveData<CartMenuItemModel>()
    var storeType=MutableLiveData<String>()


    fun openCartPage() {
        navigator.goToCartPage()
    }


    fun getResturantDetails(resturantId: String,search:String,qfilter:String) {
        loadingProgress.value = true
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("search", search)
        hashMap.put("qfilter", qfilter)
        getCompositeDisposable().add(mRepository
                .getResturantDetail(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , resturantId, hashMap))
    }

    fun getResturantDetailsNew(resturantId: String,categoryId:String,qfilter:String) {
        loadingProgress.value = true
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("filter", categoryId)
        hashMap.put("qfilter", qfilter)
        getCompositeDisposable().add(mRepository
                .getResturantDetail(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , resturantId, hashMap))
    }

    fun addItemToCart(item_id: Int, cart_id: Int, qty: Int, addons: String, repeat: Int,customize:Int) {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("item_id", item_id)
        hashMap.put("cart_id", cart_id)
        hashMap.put("qty", qty)
        hashMap.put("addons", addons)
        hashMap.put("repeat", repeat)
        hashMap.put("customize", customize)
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .getCartMenuItemQty(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }

    fun removeCart(cart_id: Int) {

        val hashMap: HashMap<String, Int> = HashMap()
        hashMap.put("cart_id", cart_id)
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .removeCart(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }


}