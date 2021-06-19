package com.gox.foodiemodule.ui.viewCartActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.default
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.*

open class ViewCartViewModel : BaseViewModel<ViewCartNavigator>() {

    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    var cartRefreshResponse = MutableLiveData<String>()
    var promoerror = MutableLiveData<String>()
    var descriptionData = MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var cartListResponse = MutableLiveData<CartMenuItemModel>()
    var promoCodeResponse = MutableLiveData<PromoCodeListModel>()
    var selectedPromoCode = MutableLiveData<PromocodeModel>()
    var checkoutResponse = MutableLiveData<ResCheckCartModel>()
    var orderDetailResponse = MutableLiveData<ResCheckCartModel>()
    var paymentType=MutableLiveData<String>().default("CASH")
    var isdoorStep=MutableLiveData<Boolean>().default(false)
    var Promo = MutableLiveData<Boolean>().default(true)

    fun openCartPage() {
        navigator.goToOrderPage()
    }

    fun onDoorStepCheck(isDoorStep:Boolean){
        isdoorStep.value=isDoorStep
        Log.e("isChecked","-------"+isdoorStep.value)
    }

    fun setAddNote(description: String) {
        descriptionData.value = description
    }

    fun getAddNote(): LiveData<String> {
        return descriptionData
    }

    fun getCartList(order_type: String,addressID:Int) {
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository.getCartList(this, order_type,addressID))
    }

    fun applyPromoCode(promoCode:Int) {
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .applyPromoCOde(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),promoCode
                ))
    }

    fun addItemToCart(item_id: Int, cart_id: Int, qty: Int, addons: Int, repeat: Int,customize:Int) {
        loadingProgress.value = true
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("item_id", item_id)
        hashMap.put("cart_id", cart_id)
        hashMap.put("qty", qty)
        hashMap.put("addons", addons)
        hashMap.put("repeat", repeat)
        hashMap.put("customize", customize)
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .getCartViewMenuItemQty(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }

    fun removeCart(cart_id: Int) {
        loadingProgress.value = true
        val hashMap: HashMap<String, Int> = HashMap()
        hashMap.put("cart_id", cart_id)
        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .removeCartViewList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }

    fun getPromocodeDetail() {
        getCompositeDisposable().add(mRepository.getPromoCodeList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }


    fun setSelectedPromo(promo: PromocodeModel) {
        selectedPromoCode.value = promo
    }

    fun getSelectedPromo(): LiveData<PromocodeModel> {
        return selectedPromoCode
    }

    fun checkOut(reqPlaceOrder: ReqPlaceOrder) {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("promocode_id", reqPlaceOrder.promocodeId.toString())
        hashMap.put("wallet", reqPlaceOrder.wallet.toString())
        hashMap.put("payment_mode", reqPlaceOrder.paymentMode.toString())
        hashMap.put("card_id", reqPlaceOrder.card_id.toString())
        if (reqPlaceOrder.orderType.equals("DELIVERY",true)) {
            hashMap.put("user_address_id", reqPlaceOrder.userAddressId!!.toInt())
        }
//        hashMap.put("user_address_id", reqPlaceOrder.userAddressId!!.toInt())
        hashMap.put("order_type", reqPlaceOrder.orderType!!.toString())
        hashMap.put("city_id",reqPlaceOrder.cityID!!.toString())
        hashMap.put("leave_at_door",reqPlaceOrder.isDoorStep.toString())
        getCompositeDisposable().add(mRepository.checkout(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
    }


}