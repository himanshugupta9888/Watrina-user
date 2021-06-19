package com.gox.app.ui.signup

interface SignupNavigator {

    fun signup()
    fun openSignin()
    fun signupValidation(email: String,
                         phonenumber: String,
                         firstname : String,
                         lastname : String,
                         gender :String,
                         password:String,
                         country:String,
                         state:String,
                         city:String,
                         termsandcondition:Boolean): Boolean
    fun showDialog()
    fun hideDialog()
    fun openCountryPicker()
    fun verifyPhoneNumber()
    fun goToCityListActivity(countryStateId: String)
    fun googleSignin()
    fun facebookSignin()
    fun gotoTermsAndCondition()


}