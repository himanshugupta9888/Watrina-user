package com.gox.foodiemodule.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class OrderTrackModel() : Parcelable {
    @SerializedName("storeLatitude")
    var storeLatitude: Double? = null
    @SerializedName("storeLongitude")
    var storeLongitude: Double? = null
    @SerializedName("deliveryLatitude")
    var deliveryLatitude: Double? = null
    @SerializedName("deliveryLongitude")
    var deliveryLongitude: Double? = null
    @SerializedName("providerId")
    var providerId: Int? = null
    @SerializedName("orderId")
    var orderId: Int? = null

    constructor(parcel: Parcel) : this() {
        storeLatitude = parcel.readValue(Double::class.java.classLoader) as? Double
        storeLongitude = parcel.readValue(Double::class.java.classLoader) as? Double
        deliveryLatitude = parcel.readValue(Double::class.java.classLoader) as? Double
        deliveryLongitude = parcel.readValue(Double::class.java.classLoader) as? Double
        providerId = parcel.readValue(Int::class.java.classLoader) as? Int
        orderId = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(storeLatitude)
        parcel.writeValue(storeLongitude)
        parcel.writeValue(deliveryLatitude)
        parcel.writeValue(deliveryLongitude)
        parcel.writeValue(providerId)
        parcel.writeValue(orderId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderTrackModel> {
        override fun createFromParcel(parcel: Parcel): OrderTrackModel {
            return OrderTrackModel(parcel)
        }

        override fun newArray(size: Int): Array<OrderTrackModel?> {
            return arrayOfNulls(size)
        }
    }
}