package com.gox.app.ui.otpactivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.extensions.default
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.WebApiConstants
import com.gox.app.data.repositary.remote.model.ForgotPasswordResponse

class OtpVerificationViewModel : BaseViewModel<OtpVerificationNavigator>() {

    var otp = MutableLiveData<String>().default("")
    var newPassword = MutableLiveData<String>().default("")
    var confirmPassword = MutableLiveData<String>().default("")
    var account_type: String = ""
    var username: String = ""

    var otpResetPasswordResponse = MutableLiveData<ForgotPasswordResponse>()
    var errorResponse = MutableLiveData<String>()
    var loadingProgress = MutableLiveData<Boolean>()


    fun restPassword() {

        if (navigator.checkConfrimPassword(newPassword.value!!, confirmPassword.value!!)) {
            loadingProgress.value = true

            val hashMap: HashMap<String, Any> = HashMap()
            hashMap[WebApiConstants.SALT_KEY] = "MQ=="
            hashMap[WebApiConstants.ResetPassword.ACCOUNT_TYPE] = account_type
            hashMap[WebApiConstants.ResetPassword.USERNAME] = username
            hashMap[WebApiConstants.ResetPassword.OTP] = otp.value!!
            hashMap[WebApiConstants.ResetPassword.PASSWORD] = newPassword.value!!
            hashMap[WebApiConstants.ResetPassword.PASSWORD_CONFIRMATION] = confirmPassword.value!!


            getCompositeDisposable().add(AppRepository.instance()
                    .resetPassword(this,hashMap))
        }
    }
}