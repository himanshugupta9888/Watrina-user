package com.gox.taximodule.ui.fragment.coupon

import com.gox.basemodule.base.BaseViewModel

class TaxiCouponViewModel:BaseViewModel<TaxiCouponNavigator>(){

    fun dismissPopup(){
        navigator.closePopup()
    }

}