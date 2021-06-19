package com.gox.app.ui.viewCouponActivity

import com.gox.basemodule.base.BaseViewModel

class ViewCouponViewModel:BaseViewModel<ViewCouponNavigator>(){

    fun closeCurrentActivity()
    {
        navigator.closeActivity()
    }

}