package com.gox.basemodule.common.chatmessage

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.model.ReqChatModel
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.common.payment.model.ResMessageModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.repositary.BaseModuleRepository

class ChatMainViewModel : BaseViewModel<ChatNavigator>() {

    private val mRepository = BaseModuleRepository.instance()

    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var errorResponse = MutableLiveData<String>()
    var getMessageResponse = MutableLiveData<ResMessageModel>()
    val successResponse = MutableLiveData<ResCommonSuccessModel>()


    fun getMessages(admin_service: String, id: Int) {
        getCompositeDisposable().add(mRepository.getMessage(this, Constant.M_TOKEN
                + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), admin_service, id))
    }
}