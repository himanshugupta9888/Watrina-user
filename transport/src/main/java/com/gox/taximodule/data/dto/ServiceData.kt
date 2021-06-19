package com.gox.taximodule.data.dto

import com.google.gson.annotations.SerializedName

class ServiceData {
    @SerializedName("services")
    val serviceList: List<ServiceModel>? = null
    @SerializedName("providers")
    val providersList: ArrayList<ProviderModel>? = null
    @SerializedName("promocodes")
    val promocodeList: ArrayList<PromocodeModel>? = null
}