package com.gox.app.ui.profile

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.CountryListResponse
import com.gox.app.data.repositary.remote.model.ProfileResponse
import com.gox.app.data.repositary.remote.model.SendOTPResponse
import com.gox.app.data.repositary.remote.model.response.ResProfileUpdate
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.repositary.ApiListener
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

public class ProfileViewModel : BaseViewModel<ProfileNavigator>() {

    var mProfileResponse = MutableLiveData<ProfileResponse>()
    var mProfileUpdateResponse = MutableLiveData<ResProfileUpdate>()
    var errorResponse = MutableLiveData<String>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var mUserName: ObservableField<String> = ObservableField("")
    var lastName: ObservableField<String> = ObservableField("")
    var mMobileNumber: ObservableField<String> = ObservableField("")
    var mEmail: ObservableField<String> = ObservableField("")
    var mCity: ObservableField<String> = ObservableField("")
    var mCityId: ObservableField<String> = ObservableField("")
    var mCountry: ObservableField<String> = ObservableField("")
    var mCountryId: ObservableField<String> = ObservableField("")
    var mCountryCode: ObservableField<String> = ObservableField("")
    var mProfileImage: ObservableField<String> = ObservableField("")
    var gender: ObservableField<String> = ObservableField("")

    var loadingProgress = MutableLiveData<Boolean>()
    var countryListResponse = MutableLiveData<CountryListResponse>()
    var sendOTPResponse = MutableLiveData<SendOTPResponse>()

    val appRepository = AppRepository.instance()

    fun getProfile() {
        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getUserProfile(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }


    fun getProfileResponse(): MutableLiveData<ProfileResponse> {
        return mProfileResponse
    }


    fun updateProfile(file: MultipartBody.Part) {
        loadingProgress.value = true
        val hashMap: HashMap<String, RequestBody> = HashMap()
        hashMap.put("first_name", RequestBody.create(MediaType.parse("text/plain"), mUserName.get().toString()))
        hashMap.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.get().toString()))
        hashMap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mMobileNumber.get().toString()))
        hashMap.put("country_code", RequestBody.create(MediaType.parse("text/plain"), mCountryCode.get().toString().replace("+", "")))
        hashMap.put("city_id", RequestBody.create(MediaType.parse("text/plain"), mCityId.get().toString()))
        hashMap.put("gender", RequestBody.create(MediaType.parse("text/plain"), gender.get().toString()))
        hashMap.put("country_id", RequestBody.create(MediaType.parse("text/plain"), mCountryId.get().toString()))
        getCompositeDisposable().add(appRepository
                .profileUpdate(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap, file))
    }

    fun updateProfileWithOutImage() {
        loadingProgress.value = true
        val hashMap: HashMap<String, RequestBody> = HashMap()
        hashMap.put("first_name", RequestBody.create(MediaType.parse("text/plain"), mUserName.get().toString()))
        hashMap.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.get().toString()))
        hashMap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mMobileNumber.get().toString()))
        hashMap.put("country_code", RequestBody.create(MediaType.parse("text/plain"), mCountryCode.get().toString().replace("+","")))
        hashMap.put("city_id", RequestBody.create(MediaType.parse("text/plain"), mCityId.get().toString()))
        hashMap.put("gender", RequestBody.create(MediaType.parse("text/plain"), gender.get().toString()))
        hashMap.put("country_id", RequestBody.create(MediaType.parse("text/plain"), mCountryId.get().toString()))
        getCompositeDisposable().add(appRepository
                .profileWithoutImageUpdate(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
        loadingProgress.value = false

    }

    fun getProfileCountryList() {
        loadingProgress.value = true

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["salt_key"] = "MQ=="

        getCompositeDisposable().add(appRepository
                .countryList(this, hashMap))

    }

    fun updateProfileResponse(): MutableLiveData<ResProfileUpdate> {
        return mProfileUpdateResponse
    }

    fun setUserName(username: String) {
        mUserName.set(username)
    }

    fun setMobileNumber(MobileNumber: String) {
        mMobileNumber.set(MobileNumber)
    }

    fun setEmail(email: String) {
        mEmail.set(email)
    }

    fun setCity(city: String) {
        mCity.set(city)
    }

    fun setCountry(country: String) {
        mCountry.set(country)
    }

    fun setCountryCode(cCode: String) {
        mCountryCode.set(cCode)
    }

    fun getCityList() {
        navigator.goToCityListActivity(mCountryId)
    }

    fun changePassord() {
        navigator.goToChangePasswordActivity()
    }

    fun sendOTP() {
        val hashMap: HashMap<String, RequestBody> = HashMap()
        hashMap.put("country_code", RequestBody.create(MediaType.parse("text/plain"), mCountryCode.get().toString().replace("+","")))
        hashMap.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mMobileNumber.get().toString()))
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

}