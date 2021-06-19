package com.gox.foodiemodule.fragment.coupon

import com.gox.basemodule.base.BaseViewModel

class OrderCouponViewModel:BaseViewModel<OrderCouponNavigator>(){

    fun dismissPopup(){
        navigator.closePopup()
    }

}
