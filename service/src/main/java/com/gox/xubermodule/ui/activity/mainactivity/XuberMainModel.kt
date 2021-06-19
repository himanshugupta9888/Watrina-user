package com.gox.xubermodule.ui.activity.mainactivity

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.ServiceCheckReqModel
import com.gox.xubermodule.data.model.SubCategoryModel

class XuberMainModel:BaseViewModel<XuberMainNavigator>(){

    var subCategoryResponse = MutableLiveData<SubCategoryModel>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var checkRequestResponse = MutableLiveData<ServiceCheckReqModel>()


    fun getServiceCategory(mServiceID: Int) {

        loadingProgress.value = true
        getCompositeDisposable().add(mRepository
                .getSubCategoryList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")
                        , mServiceID))

    }

    fun getCheckRequest() {
        getCompositeDisposable().add(mRepository.getCheckRequestMain(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "")))
    }
}