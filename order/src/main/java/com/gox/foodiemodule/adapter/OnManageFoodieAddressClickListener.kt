package com.gox.foodiemodule.adapter

import com.gox.foodiemodule.data.model.FoodieAddressModel

interface OnManageFoodieAddressClickListener {
    fun onDelete(position: Int)
    fun onEdit(position: Int)
    fun onClick(addressModel: FoodieAddressModel)

}
