package com.gox.app.ui.changepasswordactivity

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.WebApiConstants
import com.gox.app.data.repositary.remote.model.ResponseData

class ChangePasswordViewModel : BaseViewModel<ChangePasswordNavigator>() {

    var oldPassword: ObservableField<String> = ObservableField("")
    var newPassword: ObservableField<String> = ObservableField("")
    var confrimPassword: ObservableField<String> = ObservableField("")

    var loadingProgress = MutableLiveData<Boolean>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var changePasswordResponse = MutableLiveData<ResponseData>()
    var errorResponse = MutableLiveData<String>()


    val appRepository = AppRepository.instance()

    fun changePassword() {
        if (navigator.checkValidation()) {
            loadingProgress.value = true
            val hashMap = HashMap<String, Any>()
            hashMap[WebApiConstants.SALT_KEY] = "MQ=="
            hashMap[WebApiConstants.OLD_PASSWORD] = oldPassword.get().toString()
            hashMap[WebApiConstants.NEW_PASSWORD] =  newPassword.get().toString()
            hashMap[WebApiConstants.CONFIRM_PASSWORD] = confrimPassword.get().toString()

            getCompositeDisposable().add(appRepository
                    .changePassword(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()
                            , hashMap))
        }
    }
}