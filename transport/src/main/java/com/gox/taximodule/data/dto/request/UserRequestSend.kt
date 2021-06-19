package com.gox.taximodule.data.dto.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class UserRequestSend() : Parcelable {
    @SerializedName("s_latitude")
    var sourceLat: String? = null
    @SerializedName("s_longitude")
    var sourceLon: String? = null
    @SerializedName("service_type")
    var serviceType: String? = null
    @SerializedName("d_latitude")
    var destinationLat: String? = null
    @SerializedName("d_longitude")
    var destination: String? = null
    @SerializedName("distance")
    var distance: String? = null
    @SerializedName("payment_mode")
    var paymentMode: String? = null
    @SerializedName("use_wallet")
    var useWallet: String? = null

    constructor(parcel: Parcel) : this() {
        sourceLat = parcel.readString()
        sourceLon = parcel.readString()
        serviceType = parcel.readString()
        destinationLat = parcel.readString()
        destination = parcel.readString()
        distance = parcel.readString()
        paymentMode = parcel.readString()
        useWallet = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sourceLat)
        parcel.writeString(sourceLon)
        parcel.writeString(serviceType)
        parcel.writeString(destinationLat)
        parcel.writeString(destination)
        parcel.writeString(distance)
        parcel.writeString(paymentMode)
        parcel.writeString(useWallet)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRequestSend> {
        override fun createFromParcel(parcel: Parcel): UserRequestSend {
            return UserRequestSend(parcel)
        }

        override fun newArray(size: Int): Array<UserRequestSend?> {
            return arrayOfNulls(size)
        }
    }


}