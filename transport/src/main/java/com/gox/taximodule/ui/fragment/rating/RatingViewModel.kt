package com.gox.taximodule.ui.fragment.rating

import com.gox.basemodule.base.BaseViewModel

class RatingViewModel : BaseViewModel<RatingNavigator>() {

    fun dismissDialog(){
        navigator.dismissDialog()
    }

}
