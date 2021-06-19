package com.gox.xubermodule.ui.activity.providerreviews

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.data.ServiceRepository
import com.gox.xubermodule.data.model.ReviewListModel
import com.gox.xubermodule.data.model.XuberServiceClass

class ProviderReviewViewModel : BaseViewModel<ProviderReviewsNavigator>() {
    var errorResponse = MutableLiveData<String>()
    private val mRepository = ServiceRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var providerReviewResponse = MutableLiveData<ReviewListModel>()

    fun getProviderReviews(offset: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("limit", "10")
        hashMap.put("offset", offset)
        getCompositeDisposable().add(mRepository.getProviderReviews(this, XuberServiceClass.providerListModel.id,
                Constant.M_TOKEN +
                        preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),  hashMap))
    }

    fun getReviewResponse(): MutableLiveData<ReviewListModel> {
        return providerReviewResponse
    }

}
