package com.gox.basemodule.model

import com.google.gson.annotations.SerializedName

class ReqAddressModel {
    @SerializedName("address_type")
    var addressType: String? = null
    @SerializedName("id")
    var addressId: String? = null
    @SerializedName("landmark")
    var landmark: String? = null
    @SerializedName("flat_no")
    var flatNo: String? = null
    @SerializedName("street")
    var street: String? = null
    @SerializedName("latitude")
    var lat: String? = null
    @SerializedName("longitude")
    var lon: String? = null
    @SerializedName("_method")
    var method: String? = null
    @SerializedName("city")
    var city: String? = null
    @SerializedName("state")
    var state: String? = null
    @SerializedName("county")
    var country: String? = null
    @SerializedName("zipcode")
    var zipCode: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("*map_address")
    var mapAddress: String? = null


}