package com.gox.app.data.repositary.remote.model

import com.google.gson.annotations.SerializedName

class CountryDataModel{
    @SerializedName("id")
    var id:String?=null
    @SerializedName("country_name")
    var CountryName:String?=null
    @SerializedName("country_code")
    var countryCode:String?=null

}