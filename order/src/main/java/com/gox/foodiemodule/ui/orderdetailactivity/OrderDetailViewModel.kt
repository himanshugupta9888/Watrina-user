package com.gox.foodiemodule.ui.orderdetailactivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.CheckOutModel
import com.gox.foodiemodule.data.model.ReqRatingModel
import com.gox.foodiemodule.data.model.ResOrderDetail
import com.gox.foodiemodule.data.model.ResFoodieCommonSuccussModel

class OrderDetailViewModel : BaseViewModel<OrderDetailNavigator>() {

    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = OrderRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var checkoutResponseData = MutableLiveData<CheckOutModel>()
    var resOrderDetail = MutableLiveData<ResOrderDetail>()
    var getRatingResponse = MutableLiveData<ResFoodieCommonSuccussModel>()
    var orderType: String = ""


    fun getOrderDetails(orderId: Int) {
        // loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .orderDetail(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , orderId))
    }

    fun setRating(reqRatingModel: ReqRatingModel) {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap.put("request_id", reqRatingModel.requestId!!)
        hashMap.put("rating", reqRatingModel.ProviderRating!!)
        hashMap.put("comment", reqRatingModel.comment.toString())
        hashMap.put("shopid", reqRatingModel.shopId!!)
        hashMap.put("shoprating", reqRatingModel.ShopRating!!)
        getCompositeDisposable().add(mRepository
                .setRating(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , hashMap))
    }
}