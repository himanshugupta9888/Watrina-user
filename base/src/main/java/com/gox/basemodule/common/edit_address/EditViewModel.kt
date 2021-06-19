package com.gox.basemodule.common.edit_address

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.model.ReqAddressModel
import com.gox.basemodule.model.ResCommonSuccussModel
import com.gox.basemodule.repositary.BaseModuleRepository

class EditViewModel : BaseViewModel<EditAddressNavigator>() {
    var loading = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    var editAddressSuccessResponse = MutableLiveData<ResCommonSuccussModel>()
    private val appRepositary = BaseModuleRepository.instance()
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    fun updateAddress(reqAddressModel: ReqAddressModel) {
        loading.value = true
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["address_type"] = reqAddressModel.addressType.toString()
        hashMap["id"] = reqAddressModel.addressId.toString()
        hashMap["_method"] = reqAddressModel.method.toString()
        hashMap["landmark"] = reqAddressModel.landmark.toString()
        hashMap["flat_no"] = reqAddressModel.flatNo.toString()
        hashMap["street"] = reqAddressModel.street.toString()
        hashMap["latitude"] = reqAddressModel.lat.toString()
        hashMap["longitude"] = reqAddressModel.lon.toString()
        hashMap["title"] = reqAddressModel.title.toString()
        getCompositeDisposable().add(appRepositary
                .editAddress(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getEditResponse(): MutableLiveData<ResCommonSuccussModel> {
        return editAddressSuccessResponse
    }
}