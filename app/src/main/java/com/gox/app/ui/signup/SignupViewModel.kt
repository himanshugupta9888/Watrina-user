package com.gox.app.ui.signup

import android.text.Editable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.WebApiConstants
import com.gox.app.data.repositary.remote.model.CountryListResponse
import com.gox.app.data.repositary.remote.model.SendOTPResponse
import com.gox.app.data.repositary.remote.model.SignupResponse
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.extensions.default
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.utils.ValidationUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

class SignupViewModel : BaseViewModel<SignupNavigator>() {

    var email: ObservableField<String> = ObservableField("")
    var phonenumber = MutableLiveData<String>().default("")
    var first_name: ObservableField<String> = ObservableField("")
    var last_name: ObservableField<String> = ObservableField("")
    var password: ObservableField<String> = ObservableField("")
    var gender: ObservableField<String> = ObservableField("MALE")
    var referralCode: ObservableField<String> = ObservableField("")
    var acceptTermsCondition: ObservableField<Boolean> = ObservableField(true)
    //    var country_code: ObservableField<String> = ObservableField("")
    var country_id: String = ""
    var state_id: String = ""
    var city_id: String = ""
    var country_code=MutableLiveData<String>().default("+61")
    var loginby: String = "manual"
    var social_uniqueid = ""

    var countryListResponse = MutableLiveData<CountryListResponse>()
    var signupResponse = MutableLiveData<SignupResponse>()
    var sendOTPResponse = MutableLiveData<SendOTPResponse>()
    var errorResponse = MutableLiveData<String>()
    var verifyEmailPhone = MutableLiveData<ResponseBody>()
    var loadingProgress = MutableLiveData<Boolean>()

    private val appRepository = AppRepository.instance()


    fun facebookLogin() = navigator.facebookSignin()
    fun googleLogin() = navigator.googleSignin()
    fun openTermsAndCondition() {
        navigator.gotoTermsAndCondition()
    }

    fun doRegistration() {
        if (navigator.signupValidation(email.get().toString(),
                        phonenumber.value!!.toString(),
                        first_name.get().toString(),
                        last_name.get().toString(),
                        gender.get().toString(),
                        password.get().toString(),
                        country_id,
                        state_id,
                        city_id,
                        acceptTermsCondition.get()!!))
            navigator.verifyPhoneNumber()

    }

    fun gotoSignin() {
        navigator.openSignin()
    }


    fun sendOTP() {
        val hashMap: HashMap<String, RequestBody> = HashMap()
        hashMap.put("country_code", RequestBody.create(MediaType.parse("text/plain"), country_code.value!!.replace("+", "")))
        hashMap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), phonenumber.value!!.toString()))
        hashMap.put("salt_key", RequestBody.create(MediaType.parse("text/plain"), BuildConfig.SALT_KEY))
        getCompositeDisposable().add(appRepository.sendOTP(object : ApiListener {
            override fun onSuccess(successData: Any) {
                sendOTPResponse.postValue(successData as SendOTPResponse)
            }

            override fun onError(error: Throwable) {
                errorResponse.postValue(getErrorMessage(error))
            }
        }, hashMap))
    }

    fun verifiedPhoneNumber(file: MultipartBody.Part?) {
        loadingProgress.value = true

        val hashMap: HashMap<String, RequestBody> = HashMap()

        if (!loginby.equals("manual")) {
            hashMap.put("social_unique_id", RequestBody.create(MediaType.parse("text/plain"), social_uniqueid))
            hashMap.put("login_by", RequestBody.create(MediaType.parse("text/plain"), loginby))
        } else {
            hashMap.put("login_by", RequestBody.create(MediaType.parse("text/plain"), "manual"))
        }
        hashMap.put("salt_key", RequestBody.create(MediaType.parse("text/plain"), BuildConfig.SALT_KEY))
        hashMap.put("device_type", RequestBody.create(MediaType.parse("text/plain"), "ANDROID"))
        hashMap.put("device_token", RequestBody.create(MediaType.parse("text/plain"), BaseApplication.getCustomPreference!!.getString(PreferenceKey.DEVICE_TOKEN, "111")))
        hashMap.put("first_name", RequestBody.create(MediaType.parse("text/plain"), first_name.get().toString()))
        hashMap.put("last_name", RequestBody.create(MediaType.parse("text/plain"), last_name.get().toString()))
        hashMap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), phonenumber.value!!.toString()))
        hashMap.put("email", RequestBody.create(MediaType.parse("text/plain"), email.get().toString()))
        hashMap.put("password", RequestBody.create(MediaType.parse("text/plain"), password.get().toString()))
        hashMap.put("gender", RequestBody.create(MediaType.parse("text/plain"), gender.get().toString()))
        hashMap.put("country_code", RequestBody.create(MediaType.parse("text/plain"), country_code.value!!.replace("+","")))
        hashMap.put("country_id", RequestBody.create(MediaType.parse("text/plain"), country_id))
        hashMap.put("city_id", RequestBody.create(MediaType.parse("text/plain"), city_id))
        if (referralCode.get().toString().isNotEmpty()) {
            hashMap.put("referral_code", RequestBody.create(MediaType.parse("text/plain"), referralCode.get().toString()))
        }

        if (file != null) {
            getCompositeDisposable().add(appRepository
                    .signUp(this, hashMap, file))
        } else {
            getCompositeDisposable().add(appRepository
                    .signUpwithoutImage(this, hashMap))
        }

    }


    fun gotoCountryPicker() {
        navigator.openCountryPicker()
    }


    fun getCountryList() {
        loadingProgress.value = true

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap[WebApiConstants.SALT_KEY] = "MQ=="

        getCompositeDisposable().add(appRepository
                .countryList(this, hashMap)
        )

    }


    fun getCityList() {
        navigator.goToCityListActivity(country_id)
    }

    fun afterTextChangedPhone(editable: Editable) {
        if (editable.toString().length >= 6) {
            phonenumber.value = (editable.toString())
            checkPhoneEmailVerify("phone")
        }
    }

    fun afterTextChangedEmail(editable: Editable) {
        if (ValidationUtils.isValidEmail(editable.toString())) {
            email.set(editable.toString())
            checkPhoneEmailVerify("email")
        }
    }


    fun checkPhoneEmailVerify(s: String) {

        val hashMap: HashMap<String, Any> = HashMap()
        if (s.equals("phone", true)) {

            hashMap.put("mobile", phonenumber.value!!.toString())
            hashMap.put("country_code", country_code)
        } else {
            hashMap.put("email", email.get().toString())
        }
        hashMap.put("salt_key", "MQ==")


        getCompositeDisposable().add(appRepository
                .verifyMobilePhone(this, hashMap))


    }


}