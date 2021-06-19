package com.gox.xubermodule.ui.fragment.coupon

import com.gox.basemodule.base.BaseViewModel

class XuberCouponViewModel:BaseViewModel<XuberCouponNavigator>(){

    fun dismissPopup(){
        navigator.closePopup()
    }

}
