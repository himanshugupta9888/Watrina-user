package com.gox.taximodule.data.dto

class TaxiRideRequest{

    companion object {
        lateinit var mTaxiStaus: String
        fun getStatus():String{
            return mTaxiStaus
        }
        fun setStatus(mTaxiStaus: String){
            Companion.mTaxiStaus = mTaxiStaus
        }
    }

}