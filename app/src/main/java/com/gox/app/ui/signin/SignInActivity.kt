package com.gox.app.ui.signin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.SignInResponse
import com.gox.app.databinding.ActivitySignInBinding
import com.gox.app.ui.countrypicker.CountryCodeActivity
import com.gox.app.ui.dashboard.UserDashboardActivity
import com.gox.app.ui.forgotPasswordActivity.ForgotPasswordActivity
import com.gox.app.ui.signup.SignupActivity
import com.gox.app.utils.Country
import com.gox.app.utils.Enums
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant.COUNTRYCODE_PICKER_REQUEST_CODE
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.setValue
import com.gox.basemodule.utils.ValidationUtils
import com.gox.basemodule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class SignInActivity : BaseActivity<ActivitySignInBinding>(), SigninNavigator {

    private lateinit var mViewDataBinding: ActivitySignInBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signinViewmodel: SigninViewModel

    private var isEmailLogin: Boolean = false

    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    private companion object {
        const val REQUEST_GOOGLE_LOGIN = 123
    }

    override fun getLayoutId(): Int = R.layout.activity_sign_in

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_signin_server_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        this.mViewDataBinding = mViewDataBinding as ActivitySignInBinding
        signinViewmodel = ViewModelProviders.of(this)[SigninViewModel::class.java]
        signinViewmodel.navigator = this
        this.mViewDataBinding.siginmodel = signinViewmodel

        observeResponse()

        observeError()

        socialLoginControl()

        //setDefaultCountry()
        detectDefaultCountry()

    }

    private fun detectDefaultCountry() {
        val resultIntent = Intent()
//        val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        val countryModel = Country.getCountryByISO(tm.networkCountryIso)
//        if (countryModel == null) {
//            resultIntent.putExtra("countryName", "Australia")
//            resultIntent.putExtra("countryCode", "+61")
//            resultIntent.putExtra("countryFlag", R.drawable.flag_australia)
//        } else {
//            resultIntent.putExtra("countryName", countryModel.name)
//            resultIntent.putExtra("countryCode", countryModel.dialCode)
//            resultIntent.putExtra("countryFlag", countryModel.flag)
//        }

        resultIntent.putExtra("countryName", "Australia")
        resultIntent.putExtra("countryCode", "+61")
        resultIntent.putExtra("countryFlag", R.drawable.flag_australia)
        handleCountryCodePickerResult(resultIntent)
    }

    private fun socialLoginControl() {
        if (BaseApplication.getCustomPreference!!.getString(PreferenceKey.SOCIAL_LOGIN, "1").equals("1")) {
            llcSocial.visibility = View.VISIBLE
            tvConnectSocialLabel.visibility = View.VISIBLE
        } else {
            llcSocial.visibility = View.GONE
            tvConnectSocialLabel.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        this.mViewDataBinding.phonenumberRegisterEt.setText("")
        this.mViewDataBinding.emailidRegisterEt.setText("")
        this.mViewDataBinding.passwordRegisterEt.setText("")
    }


    fun setDefaultCountry() {
        val dr = ContextCompat.getDrawable(this, R.drawable.flag_india as Int)
        val bitmap = (dr as BitmapDrawable).bitmap
        var width: Int = 0
        var height: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            width = dpsToPixels(this@SignInActivity, 28)
            height = dpsToPixels(this@SignInActivity, 20)
        } else {
            width = dpsToPixels(this@SignInActivity, 15)
            height = dpsToPixels(this@SignInActivity, 15)
        }

        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
    }


    private fun dpsToPixels(activity: Activity, dps: Int): Int {
        val r = activity.resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), r.displayMetrics).toInt()
    }


    private fun observeResponse() {
        signinViewmodel.loginResponse.observe(this@SignInActivity, Observer<SignInResponse> {
            loadingObservable.value = false
            gotoHome(it)
        })
    }

    private fun observeError() {
        signinViewmodel.getErrorObservable().observe(this@SignInActivity, Observer<String> { message ->
            loadingObservable.value = false
            ViewUtils.showToast(this@SignInActivity, message, false)
        })
    }

    //move to signup page
    override fun goToSignup() {
        openNewActivity(this@SignInActivity, SignupActivity::class.java, false)
    }

    override fun googleSignin() {
        loadingObservable.value = true
        startActivityForResult(mGoogleSignInClient.signInIntent, Companion.REQUEST_GOOGLE_LOGIN)
    }

    override fun facebookSignin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    println("Token received " + loginResult.accessToken.token)
                    val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject: JSONObject, graphResponse: GraphResponse ->
                        signinViewmodel.socialLogin(Enums.FACEBOOK, graphResponse.jsonObject.getString("id"))
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,link,gender,first_name,last_name,verified")
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            override fun onCancel() {}

            override fun onError(exception: FacebookException) {
                exception.printStackTrace()
                val s = exception.message
                if (exception is FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null)
                        LoginManager.getInstance().logOut()
                } else if (s!!.contains("GraphQLHttpFailureDomain"))
                    ViewUtils.showToast(this@SignInActivity, R.string.fb_session_expired, false)
            }
        })
    }

    override fun performValidation() {
        hideKeyboard()

        if (isEmailLogin && signinViewmodel.email.value.isNullOrBlank()) {
            ViewUtils.showToast(this@SignInActivity, R.string.error_invalid_email_address, false)
        } else if (!isEmailLogin && signinViewmodel.phone.value.isNullOrBlank()) {
            ViewUtils.showToast(this@SignInActivity, R.string.error_invalid_phonenumber, false)
        } else if (TextUtils.isEmpty(signinViewmodel.password.value!!.trim()) && ValidationUtils.isMinLength(signinViewmodel.password.value!!, 6)) {
            ViewUtils.showToast(this@SignInActivity, R.string.error_invalid_password, false)
        } else {
            loadingObservable.value = true
            signinViewmodel.postSignIn(isEmailLogin)
        }
    }

    override fun gotoHome(data: SignInResponse) {
        preferenceHelper.setValue(PreferenceKey.ACCESS_TOKEN, data.responseData.access_token)
        openNewActivity(this@SignInActivity, UserDashboardActivity::class.java, true)
        finishAffinity()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == Companion.REQUEST_GOOGLE_LOGIN) {

                val TAG = "REQUEST_GOOGLE_LOGIN"
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    //String token = account.getIdToken();
                    val runnable = {
                        try {
                            val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                            val accessToken = GoogleAuthUtil.getToken(applicationContext, Objects.requireNonNull(Objects.requireNonNull(account)!!.account), scope, Bundle())
                            Log.d(TAG, "accessToken:$accessToken")
                            signinViewmodel.socialLogin("GOOGLE", account?.id!!)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: GoogleAuthException) {
                            e.printStackTrace()
                        }
                    }
                    AsyncTask.execute { kotlin.run(runnable) }

                } catch (e: ApiException) {
                    Log.w(TAG, "signInResult:failed code = " + e.statusCode)
                }

            } else if (requestCode == 111) {
                if (data!!.hasExtra("countryName")) {
                    handleCountryCodePickerResult(data)
                }

            }
        } else {
            loadingObservable.value = false
        }


    }

    private fun handleCountryCodePickerResult(data: Intent) {
        mViewDataBinding.countrycodeRegisterEt.setText(data.extras?.get("countryCode").toString())
        val countryFlag = data.extras?.get("countryFlag") as Int
        val dr = ContextCompat.getDrawable(this, countryFlag)
        preferenceHelper.setValue(PreferenceKey.COUNTRY_FLAG, countryFlag)
        val bitmap = (dr as BitmapDrawable).bitmap
        var width: Int = 0
        var height: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            width = dpsToPixels(this@SignInActivity, 8)
            height = dpsToPixels(this@SignInActivity, 8)
        } else {
            width = dpsToPixels(this@SignInActivity, 15)
            height = dpsToPixels(this@SignInActivity, 15)
        }
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
        signinViewmodel.country_code.value = data.extras?.get("countryCode").toString()
    }


    override fun changeSigninViaPhone() {
        isEmailLogin = false
        mViewDataBinding.passwordRegisterEt.setText("")
        mViewDataBinding.phoneLogin.visibility = View.VISIBLE
        mViewDataBinding.emailLogin.visibility = View.GONE
        mViewDataBinding.phonenumberRegisterEt.requestFocus()
        mViewDataBinding.phoneSigninImgview.setColorFilter(ContextCompat.getColor(this@SignInActivity, R.color.colorAccent))
        mViewDataBinding.mailSinginImgview.setColorFilter(ContextCompat.getColor(this@SignInActivity, R.color.dark_grey))
        mViewDataBinding.phoneSigninImgview.setBackgroundResource((R.drawable.login_icon_selected_bg))
        mViewDataBinding.mailSinginImgview.setBackgroundResource((R.drawable.login_icon_normal_bg))
    }

    override fun changeSigninViaMail() {
        isEmailLogin = true
        mViewDataBinding.passwordRegisterEt.setText("")
        mViewDataBinding.phoneLogin.visibility = View.GONE
        mViewDataBinding.emailLogin.visibility = View.VISIBLE
        mViewDataBinding.emailidRegisterEt.requestFocus()
        mViewDataBinding.phoneSigninImgview.setColorFilter(ContextCompat.getColor(this@SignInActivity, R.color.dark_grey))
        mViewDataBinding.mailSinginImgview.setColorFilter(ContextCompat.getColor(this@SignInActivity, R.color.colorAccent))
        mViewDataBinding.phoneSigninImgview.setBackgroundResource((R.drawable.login_icon_normal_bg))
        mViewDataBinding.mailSinginImgview.setBackgroundResource((R.drawable.login_icon_selected_bg))
    }

    override fun goToCountryCodePickerActivity() {
        val intent = Intent(this@SignInActivity, CountryCodeActivity::class.java)
        startActivityForResult(intent, COUNTRYCODE_PICKER_REQUEST_CODE)
    }

    override fun goToForgotPasswordActivity() {
        openNewActivity(this@SignInActivity, ForgotPasswordActivity::class.java, false)
    }
}
