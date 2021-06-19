package com.gox.app.ui.signin

import com.gox.app.data.repositary.remote.model.SignInResponse

interface SigninNavigator {

    fun changeSigninViaPhone()
    fun changeSigninViaMail()
    fun goToSignup()
    fun googleSignin()
    fun facebookSignin()
    fun gotoHome(data: SignInResponse)
    fun performValidation()
    fun goToCountryCodePickerActivity()
    fun goToForgotPasswordActivity()
}