package com.gox.taximodule.ui.fragment.reason

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceHelper
import com.gox.taximodule.data.dto.response.ResReasonModel
import io.reactivex.disposables.Disposable

class ReasonViewModel : BaseViewModel<ReasonNavigator>() {
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    private lateinit var subscription: Disposable
    val mReasonResponseData = MutableLiveData<ResReasonModel>()
    var errorResponse = MutableLiveData<Throwable>()
    fun dismissPopup() {
        navigator.closePopup()
    }


}
