package com.gox.foodiemodule.fragment.ordercancelreason

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.data.OrderRepository
import com.gox.foodiemodule.data.model.OrderReasonReqModel
import com.gox.foodiemodule.data.model.ResFoodieReasonModel

import io.reactivex.disposables.Disposable

class OrderReasonViewModel : BaseViewModel<ReasonNavigator>() {
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    private lateinit var subscription: Disposable
    val mReasonResponseData = MutableLiveData<ResFoodieReasonModel>()
    var errorResponse = MutableLiveData<String>()
    val successResponse = MutableLiveData<ResCommonSuccessModel>()
    private val mRepository = OrderRepository.instance()
    fun dismissPopup() {
        navigator.closePopup()
    }

    fun getReason(type: String) {
        getCompositeDisposable().add(mRepository.getReasons(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), type))
    }

    fun getReasonResponse(): MutableLiveData<ResFoodieReasonModel> {
        return mReasonResponseData
    }

    fun cancelOrder(reqEstimateModel: OrderReasonReqModel) {

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id"] = reqEstimateModel.id!!.toInt()
        if (reqEstimateModel.reason != "") {
            hashMap["cancel_reason"] = reqEstimateModel.reason.toString()
        }
        getCompositeDisposable().add(mRepository.cancelOrder(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getCancelResponse(): MutableLiveData<ResCommonSuccessModel> {
        return successResponse
    }


}
