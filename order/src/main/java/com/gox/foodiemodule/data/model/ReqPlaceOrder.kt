package com.gox.foodiemodule.data.model

import com.google.gson.annotations.SerializedName

class ReqPlaceOrder {

    @SerializedName("promocode_id")
    var promocodeId: String? = null
    @SerializedName("wallet")
    var wallet: String? = null
    @SerializedName("payment_mode")
    var paymentMode: String? = null
    @SerializedName("card_id")
    var card_id: String? = null
    @SerializedName("user_address_id")
    var userAddressId: Int? = null
    @SerializedName("delivery_date")
    var deliveryDate: String? = ""
    @SerializedName("order_type")
    var orderType: String? = ""
    @SerializedName("description")
    var description: String? = null
    @SerializedName("city_id")
    var cityID:String?=""
    @SerializedName("isDoorStep")
    var isDoorStep:Int?=0

}