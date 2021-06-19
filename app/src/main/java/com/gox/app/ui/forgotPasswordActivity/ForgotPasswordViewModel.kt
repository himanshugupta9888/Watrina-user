package com.gox.app.ui.forgotPasswordActivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.extensions.default
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.WebApiConstants
import com.gox.app.data.repositary.remote.model.ForgotPasswordResponse

class ForgotPasswordViewModel : BaseViewModel<ForgotPasswordNavigator>() {
    var email = MutableLiveData<String>().default("")
    var phone = MutableLiveData<String>().default("")
    lateinit var country_code: String
    var accountype: String = "email"
    var forgotPasswordResponse = MutableLiveData<ForgotPasswordResponse>()

    //Thease two live data common for all api calls
    var errorResponse = MutableLiveData<String>()
    var loadingProgress = MutableLiveData<Boolean>()


    fun openRestPasswordActivity() {
        navigator.goToRestPasswordActivity()
    }

    fun changeOtpVerifyViaPhone() {
        accountype = "phone"
        navigator.changeOtpVerifyViaPhone()
    }

    fun gotoCountryPicker() {
        navigator.goToCountryCodePickerActivity()
    }

    fun changeOtpVerifyViaMail() {
        accountype = "email"
        navigator.changeOtpVerifyViaMail()
    }

    fun restPasswordApicall() {
        loadingProgress.value = true

        if (navigator.forgotpasswordInValidation(if (accountype == "email") email.value!!.toString() else phone.value!!.toString(), accountype)) {
            loadingProgress.value = true
            val hashMap = HashMap<String, Any>()
            hashMap["salt_key"] = "MQ=="
            if (accountype == "email") {
                hashMap[WebApiConstants.ForgotPassword.EMAIL] = email.value!!
                hashMap["account_type"] = "email"

            } else if (accountype == "phone") {

                hashMap["mobile"] = phone.value!!
                hashMap["account_type"] = "mobile"
                hashMap["country_code"] = country_code.removePrefix("+")

            }
            getCompositeDisposable().add(AppRepository.instance()
                    .forgotPassword(this,hashMap))
        }
    }
}