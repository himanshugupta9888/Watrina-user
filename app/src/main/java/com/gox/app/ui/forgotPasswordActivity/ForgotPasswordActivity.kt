package com.gox.app.ui.forgotPasswordActivity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.ValidationUtils
import com.gox.basemodule.utils.ViewUtils
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.ForgotPasswordResponse
import com.gox.app.databinding.ActivityForgotpasswordBinding
import com.gox.app.ui.countrypicker.CountryCodeActivity
import com.gox.app.ui.otpactivity.OtpVerificationActivity
import com.gox.app.utils.Country
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.setValue
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import java.io.Serializable

class ForgotPasswordActivity : BaseActivity<ActivityForgotpasswordBinding>(), ForgotPasswordNavigator {


    lateinit var mViewDataBinding: ActivityForgotpasswordBinding
    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    override fun getLayoutId(): Int = R.layout.activity_forgotpassword

    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)


    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivityForgotpasswordBinding
        forgotPasswordViewModel = ViewModelProviders.of(this)[ForgotPasswordViewModel::class.java]
        forgotPasswordViewModel.navigator = this
        this.mViewDataBinding.forgotPasswordViewModel = forgotPasswordViewModel
        this.mViewDataBinding.forgotpasswordToolbar.title_toolbar.setTitle(getString(R.string.forgot_password).removeSuffix("?"))
        this.mViewDataBinding.forgotpasswordToolbar.toolbar_back_img.setOnClickListener { view ->
            finish()
        }

        forgotPasswordViewModel.loadingProgress.observe(this, Observer<Boolean> {
            loadingObservable.value = it
        })


        forgotPasswordViewModel.forgotPasswordResponse.observe(this@ForgotPasswordActivity, Observer<ForgotPasswordResponse> {
            gotoOTP(it)
        })

        forgotPasswordViewModel.errorResponse.observe(this@ForgotPasswordActivity, Observer<String> { message ->
            ViewUtils.showToast(this@ForgotPasswordActivity, message, false)
        })

        detectDefaultCountry()
    }

    private fun detectDefaultCountry() {
        val resultIntent = Intent()
        val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val countryModel = Country.getCountryByISO(tm.networkCountryIso)
        if (countryModel == null) {
            resultIntent.putExtra("countryName", "India")
            resultIntent.putExtra("countryCode", "+91")
            resultIntent.putExtra("countryFlag", R.drawable.flag_in)
        } else {
            resultIntent.putExtra("countryName", countryModel.name)
            resultIntent.putExtra("countryCode", countryModel.dialCode)
            resultIntent.putExtra("countryFlag", countryModel.flag)
        }

        handleCountryCodePickerResult(resultIntent)
    }


    private fun gotoOTP(it: ForgotPasswordResponse?) {
        it?.responseData?.otp
        val intent = Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java)
        intent.putExtra("forgotPasswordResponse", it as Serializable)
        startActivity(intent)
    }


    override fun goToRestPasswordActivity() {


    }


    override fun forgotpasswordInValidation(email_phone: String, accountype: String): Boolean {

        if (accountype.equals("email")) {
            if (TextUtils.isEmpty(email_phone) && !ValidationUtils.isValidEmail(email_phone)) {
                ViewUtils.showToast(this@ForgotPasswordActivity, R.string.error_invalid_email_address, false)
                forgotPasswordViewModel.loadingProgress.value = false
                return false
            }
        } else {
            if (TextUtils.isEmpty(email_phone)) {
                ViewUtils.showToast(this@ForgotPasswordActivity, R.string.error_invalid_phonenumber, false)
                forgotPasswordViewModel.loadingProgress.value = false

                return false
            }
        }
        return true

    }


    override fun changeOtpVerifyViaPhone() {
        mViewDataBinding.phoneLogin.visibility = View.VISIBLE
        mViewDataBinding.emailLogin.visibility = View.GONE
        mViewDataBinding.phoneOtpImgview.setColorFilter(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.colorAccent))
        mViewDataBinding.mailOtpImgview.setColorFilter(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.dark_grey))

    }

    override fun changeOtpVerifyViaMail() {

        mViewDataBinding.phoneLogin.visibility = View.GONE
        mViewDataBinding.emailLogin.visibility = View.VISIBLE
        mViewDataBinding.phoneOtpImgview.setColorFilter(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.dark_grey))
        mViewDataBinding.mailOtpImgview.setColorFilter(ContextCompat.getColor(this@ForgotPasswordActivity, R.color.colorAccent))
    }

    override fun goToCountryCodePickerActivity() {
        val intent = Intent(this@ForgotPasswordActivity, CountryCodeActivity::class.java)
        startActivityForResult(intent, Constant.COUNTRYCODE_PICKER_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            Constant.COUNTRYCODE_PICKER_REQUEST_CODE -> if (data != null) {
                handleCountryCodePickerResult(data)
            }

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
            width = dpsToPixels(this, 8)
            height = dpsToPixels(this, 8)
        } else {
            width = dpsToPixels(this, 15)
            height = dpsToPixels(this, 15)
        }
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
        forgotPasswordViewModel.country_code = data.extras?.get("countryCode").toString()
    }

    private fun dpsToPixels(activity: Activity, dps: Int): Int {
        val r = activity.resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), r.displayMetrics).toInt()
    }


}