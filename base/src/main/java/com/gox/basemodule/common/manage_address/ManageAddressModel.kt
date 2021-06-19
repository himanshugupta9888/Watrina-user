package com.gox.basemodule.common.manage_address

import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.model.AddressModel

class ManageAddressModel {
    private val addressTag = MutableLiveData<String>()
    private val maddress = MutableLiveData<String>()

    fun bind(address: AddressModel) {
        addressTag.value = address.addressType
        maddress.value = address.flatNumber + " , " + address.street + " , " + address.city + " , " + address.state + " , " + address.zipcode + "," + address.landmark

    }

}