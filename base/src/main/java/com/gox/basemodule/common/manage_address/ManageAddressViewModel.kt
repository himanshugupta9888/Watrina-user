package com.gox.basemodule.common.manage_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.getValue
import com.gox.basemodule.model.ResCommonSuccussModel
import com.gox.basemodule.model.ResAddressListModel
import com.gox.basemodule.repositary.BaseModuleRepository

class ManageAddressViewModel : BaseViewModel<ManageAddressNavigator>() {

    var addressListResponse = MutableLiveData<ResAddressListModel>()
    var deleteAddressResponse = MutableLiveData<ResCommonSuccussModel>()
    var errorResponse = MutableLiveData<String>()
    var loading = MutableLiveData<Boolean>()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    private val appRepository = BaseModuleRepository.instance()

    fun moveToAddAddressPage() {
        navigator.goToMainActivity()
    }

    fun getAddressList() {
        getCompositeDisposable().add(appRepository.getAddressList(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun deleteAddress(addressId: String) {
        getCompositeDisposable().add(appRepository.deleteAddress(this, Constant.M_TOKEN +
                preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), addressId))
    }

    fun getAddressListResponse(): LiveData<ResAddressListModel> {
        return addressListResponse
    }

    fun getDeleteAddressResponse(): LiveData<ResCommonSuccussModel> {
        return deleteAddressResponse
    }
}
