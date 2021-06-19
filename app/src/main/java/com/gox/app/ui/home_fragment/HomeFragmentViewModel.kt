package com.gox.app.ui.home_fragment

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.default
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.CountryListResponse
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.data.repositary.remote.model.ProfileResponse
import com.gox.basemodule.model.ResCommonSuccussModel

class HomeFragmentViewModel : BaseViewModel<HomeFragmentNavigator>() {
    var mProfileResponse = MutableLiveData<ProfileResponse>()
    var mSuccessResponse = MutableLiveData<ResCommonSuccussModel>()
    var loadingProgress = MutableLiveData<Boolean>()
    var menuResponse = MutableLiveData<HomeMenuResponse>()
    var errorResponse = MutableLiveData<String>().default("")
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    val appRepository = AppRepository.instance()
    var countryListResponse = MutableLiveData<CountryListResponse>()
    var abc = ObservableField<CountryListResponse>()
    val isShownRecent= MutableLiveData(false)
    fun showMoreLess() {
        navigator.showMoreLess()
    }

    fun getMenu() {
        getCompositeDisposable().add(AppRepository.instance()
                .getMenus(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun getMenuResponse(): LiveData<HomeMenuResponse> {
        return menuResponse
    }

    fun getProfile() {

        getCompositeDisposable().add(AppRepository.instance()
                .getUserProfile(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))


    }

    fun getProfileCountryList() {
        loadingProgress.value = true

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["salt_key"] = "MQ=="

        getCompositeDisposable().add(appRepository
                .countryList(this, hashMap))

    }

    fun updateCity(cityId: Int) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["city_id"] = cityId
        getCompositeDisposable().add(appRepository
                .updateCity(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap))
        getMenu()
    }

}