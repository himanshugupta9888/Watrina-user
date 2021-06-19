package com.gox.basemodule.common.manage_address

import com.gox.basemodule.model.AddressModel

interface OnManageAddressClickListener {
    fun onDelete(position: Int)
    fun onEdit(position: Int)
    fun onClick(addressModel: AddressModel)
}
