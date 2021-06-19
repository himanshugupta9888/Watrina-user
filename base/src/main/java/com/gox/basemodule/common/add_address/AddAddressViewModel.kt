package com.gox.basemodule.common.add_address

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

class AddAddressViewModel : BaseViewModel<AddAddressNavigator>() {
    var loading = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    var addAddressSuccessResponse = MutableLiveData<ResCommonSuccussModel>()
    val appRepositary = BaseModuleRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)


    fun addAddress(reqAddressModel: ReqAddressModel) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["address_type"] = reqAddressModel.addressType.toString()
        hashMap["landmark"] = reqAddressModel.landmark.toString()
        hashMap["flat_no"] = reqAddressModel.flatNo.toString()
        hashMap["street"] = reqAddressModel.street.toString()
        hashMap["latitude"] = reqAddressModel.lat.toString()
        hashMap["longitude"] = reqAddressModel.lon.toString()
        hashMap["title"] = reqAddressModel.title.toString()
        hashMap["map_address"] = reqAddressModel.mapAddress.toString()
        getCompositeDisposable().add(appRepositary
                .addAddress(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), hashMap))
    }

    fun getAddAddressResponse(): MutableLiveData<ResCommonSuccussModel> {
        return addAddressSuccessResponse
    }

}