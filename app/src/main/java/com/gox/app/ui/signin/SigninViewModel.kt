package com.gox.app.ui.signin

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.extensions.default
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.WebApiConstants
import com.gox.app.data.repositary.remote.model.SignInResponse
import com.gox.app.utils.Enums

class SigninViewModel : BaseViewModel<SigninNavigator>() {

    var email = MutableLiveData<String>().default("")
    var phone = MutableLiveData<String>().default("")
    var password = MutableLiveData<String>().default("")
    var country_code=MutableLiveData<String>().default("+91")
    private val appRepository = AppRepository.instance()
    var loginResponse = MutableLiveData<SignInResponse>()

    //These two live data common for all api calls
    fun openSignUp() {
        navigator.goToSignup()
    }

    fun facebookLogin() = navigator.facebookSignin()

    fun googleLogin() = navigator.googleSignin()

    fun socialLogin(loginBy: String, socialUniqueId: String) {
        val hashMap = HashMap<String, Any>()
        hashMap[WebApiConstants.SocialLogin.DEVICE_TOKEN] = BaseApplication.getCustomPreference!!.getString(PreferenceKey.DEVICE_TOKEN, "111") as String
        hashMap[WebApiConstants.SocialLogin.LOGIN_BY] = loginBy
        hashMap[WebApiConstants.SocialLogin.SOCIAL_UNIQUE_ID] = socialUniqueId
        hashMap[WebApiConstants.SocialLogin.DEVICE_TYPE] = Enums.DEVICE_TYPE
        hashMap[WebApiConstants.SALT_KEY] = "MQ=="
        getCompositeDisposable().add(appRepository.socialLogin(this, hashMap))
    }

    fun signIn() {
        navigator.performValidation()
    }

    fun postSignIn(isEmailLogin: Boolean) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap[WebApiConstants.SignIn.PASSWORD] = password.value!!.trim()
        hashMap[WebApiConstants.SALT_KEY] = "MQ=="
        hashMap[WebApiConstants.SocialLogin.DEVICE_TYPE] = Enums.DEVICE_TYPE
        hashMap[WebApiConstants.SocialLogin.DEVICE_TOKEN] = BaseApplication.getCustomPreference!!.getString(PreferenceKey.DEVICE_TOKEN, "111") as String
        if (isEmailLogin) {
            hashMap[WebApiConstants.SignIn.EMAIL] = email.value!!.trim()
        } else {
            hashMap[WebApiConstants.SignIn.MOBILE] = phone.value!!.trim()
            hashMap[WebApiConstants.SignIn.COUNTRY_CODE] = country_code.value!!.removePrefix("+")
        }
        getCompositeDisposable().add(appRepository.postSignIn(this, hashMap))
    }

    fun changeSigninViaPhone() {
        return navigator.changeSigninViaPhone()
    }

    fun gotoCountryPicker() {
        navigator.goToCountryCodePickerActivity()
    }

    fun changeSigninViaMail() {
        return navigator.changeSigninViaMail()
    }

    fun forgotPassword() {
        navigator.goToForgotPasswordActivity()
    }

}