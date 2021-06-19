package com.gox.foodiemodule.adapter

import com.gox.foodiemodule.data.model.ResturantDetailsModel

interface  MenuItemClickListner{

    fun addToCart(id: Int?, itemCount: Int, cartId: Int?,repeat: Int,customize:Int)
    fun showAddonLayout(position: Int?, itemCount: Int
                        , itemsaddon: ResturantDetailsModel.ResponseData.Product?, b: Boolean)
    fun removeCart(position: Int)
    fun addedAddons(position: Int)
    fun removedAddons(position: Int)
    fun addFilterType(filterType: String)

}