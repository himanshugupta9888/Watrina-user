package com.gox.taximodule.ui.fragment.tips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.data.Constant
import com.gox.taximodule.data.TaxiRepository
import com.gox.taximodule.data.dto.request.ReqTips

class TipsViewModel : BaseViewModel<TipsNavigator>() {
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var tipsResponse = MutableLiveData<ResCommonSuccessModel>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = TaxiRepository.instance()

    fun getTipsResponse(): LiveData<ResCommonSuccessModel> {
        return tipsResponse
    }

    fun getErrorResponse(): LiveData<String> {
        return errorResponse
    }


}
