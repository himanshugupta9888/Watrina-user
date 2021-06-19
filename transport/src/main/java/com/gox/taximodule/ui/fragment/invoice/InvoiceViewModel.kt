package com.gox.taximodule.ui.fragment.invoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.taximodule.data.TaxiRepository
import com.gox.taximodule.data.dto.request.ReqPaymentUpdateModel
import com.gox.taximodule.data.dto.request.ReqTips

class InvoiceViewModel : BaseViewModel<InvoiceNavigator>() {
    private val mRepository = TaxiRepository.instance()
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var paymentResponse = MutableLiveData<ResCommonSuccessModel>()
    val successResponse = MutableLiveData<ResCommonSuccessModel>()
    var errorResponse = MutableLiveData<String>()
    fun showRating(){
        navigator.showRating()
    }

    fun setCardPayment(reqTips: ReqTips) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id"] = reqTips.requestId.toString()
        hashMap["card_id  "] = reqTips.cardId.toString()
        if(reqTips.tipsAmount!=null){
            hashMap["tips"] = reqTips.tipsAmount!!.toInt()
        }
        getCompositeDisposable().add(mRepository.setCardPayment(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(),hashMap))
    }

    fun updatePayment(reqPaymentUpdateModel: ReqPaymentUpdateModel) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id"] = reqPaymentUpdateModel.requestId!!.toInt()
        hashMap["payment_mode"] = reqPaymentUpdateModel.paymentMode.toString()
        getCompositeDisposable().add(mRepository.updatePayment(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))

    }

    fun getUpdatePaymentResponse(): MutableLiveData<ResCommonSuccessModel> {
        return successResponse
    }
    fun getInvoiceResponse(): LiveData<ResCommonSuccessModel> {
        return paymentResponse
    }

    fun getErrorResponse(): LiveData<String> {
        return errorResponse
    }
}
