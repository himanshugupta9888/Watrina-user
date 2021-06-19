package com.gox.taximodule.data.dto.request

import com.google.gson.annotations.SerializedName

class ReqEstimateModel {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("s_latitude")
    var sourceLattitude: String? = null
    @SerializedName("s_longitude")
    var sourceLongitude: String? = null
    @SerializedName("s_address")
    var sourceAddress: String? = null
    @SerializedName("service_type")
    var serviceType: String? = null
    @SerializedName("d_latitude")
    var destinationLatitude: String? = null
    @SerializedName("d_longitude")
    var destinationLongitude: String? = null
    @SerializedName("d_address")
    var destinationAddress: String? = null
    @SerializedName("payment_mode")
    var paymentMode: String? = null
    @SerializedName("card_id")
    var card_id: String? = null
    @SerializedName("distance")
    var distance: String? = null
    @SerializedName("use_wallet")
    var useWallet: String? = null
    @SerializedName("reason")
    var reason: String? = null
    @SerializedName("someone")
    var someone: Int? = null
    @SerializedName("someone_name")
    var someOneName: String? = null
    @SerializedName("someone_email")
    var someOneEmail: String? = null
    @SerializedName("someone_mobile")
    var someOneMobile: String? = null
    @SerializedName("wheelchair")
    var wheelChair: Boolean? = null
    @SerializedName("child_seat")
    var childSeat: Boolean? = null
    @SerializedName("schedule_date")
    var scheduleDate: String = ""
    @SerializedName("schedule_time")
    var scheduleTime: String = ""
    @SerializedName("promocode_id")
    var promoId: String = ""
    @SerializedName("ride_type_id")
    var rideTypeId: String = ""
    @SerializedName("city_id")
    var cityID:String?=""

}