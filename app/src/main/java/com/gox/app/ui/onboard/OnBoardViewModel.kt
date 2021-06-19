package com.gox.app.ui.onboard

import com.gox.basemodule.base.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class OnBoardViewModel : BaseViewModel<OnBoardNavigator>() {



    private val mCompositeDisposable = CompositeDisposable()


    fun openSignIn() {
        navigator.goToSignIn()
    }

    fun openSignUp() {
        navigator.goToSignUp()
    }


    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }
}