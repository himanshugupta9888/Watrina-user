package com.gox.taximodule.ui.fragment.bookforsomeone

import com.gox.basemodule.base.BaseViewModel

class BookForSomeOneViewModel:BaseViewModel<BookForSomeOneNavigator>(){

    fun dismissPopup(){
        navigator.closePopup()
    }
}