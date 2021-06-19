package com.gox.foodiemodule.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class PromocodeModel() : Parcelable {
    @SerializedName("id")
    val id: Int? = null
    @SerializedName("promo_code")
    val promoCode: String? = null
    @SerializedName("service")
    val service: String? = null
    @SerializedName("picture")
    val picture: String? = null
    @SerializedName("percentage")
    val percentage: Int? = null
    @SerializedName("max_amount")
    val maxAmount: Int? = null
    @SerializedName("promo_description")
    val promoDescription: String? = null
    @SerializedName("status")
    val status: String? = null
    @SerializedName("expiration")
    val promoExpiration: String? = null

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PromocodeModel> {
        override fun createFromParcel(parcel: Parcel): PromocodeModel {
            return PromocodeModel(parcel)
        }

        override fun newArray(size: Int): Array<PromocodeModel?> {
            return arrayOfNulls(size)
        }
    }
}
