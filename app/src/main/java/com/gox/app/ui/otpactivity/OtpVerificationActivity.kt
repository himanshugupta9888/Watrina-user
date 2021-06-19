package com.gox.app.ui.otpactivity

import android.text.TextUtils
import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.utils.ViewUtils
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.ForgotPasswordResponse
import com.gox.app.databinding.ActivityOtpverificationBinding
import com.gox.app.ui.signin.SignInActivity
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class OtpVerificationActivity : BaseActivity<ActivityOtpverificationBinding>(), OtpVerificationNavigator {



    lateinit var mViewDataBinding: ActivityOtpverificationBinding
    private lateinit var otpVerificationModel: OtpVerificationViewModel

    override fun getLayoutId(): Int = R.layout.activity_otpverification

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivityOtpverificationBinding
        val forgotPasswordResponse = intent.getSerializableExtra("forgotPasswordResponse") as ForgotPasswordResponse
        otpVerificationModel = ViewModelProviders.of(this)[OtpVerificationViewModel::class.java]
        mViewDataBinding.otpVerificationViewModel = otpVerificationModel

        otpVerificationModel.navigator = this
        mViewDataBinding.changepasswordToolbarLayout.title_toolbar.setTitle(getString(R.string.rest_password))

        mViewDataBinding.changepasswordToolbarLayout.toolbar_back_img.setOnClickListener { view ->
            finish()
        }
        baseLiveDataLoading = otpVerificationModel.loadingProgress

        otpVerificationModel.account_type = forgotPasswordResponse.responseData.account_type
        otpVerificationModel.username = forgotPasswordResponse.responseData.username
        Log.d("_D_OTP", forgotPasswordResponse.responseData.otp.toString())


        otpVerificationModel.otpResetPasswordResponse.observe(this@OtpVerificationActivity, Observer<ForgotPasswordResponse> {
            goToSignin()
        })

        otpVerificationModel.errorResponse.observe(this@OtpVerificationActivity, Observer<String> { message ->
            ViewUtils.showToast(this@OtpVerificationActivity, message, false)
        })
    }

    private fun goToSignin() {
        openNewActivity(this@OtpVerificationActivity, SignInActivity::class.java, true)
    }

    override fun checkConfrimPassword(newPwd: String?, confrimPwd: String?): Boolean {
        if(otpVerificationModel.otp.value.isNullOrEmpty()){
            ViewUtils.showToast(this@OtpVerificationActivity, R.string.please_enter_otp, false)
            return false
        } else if (!newPwd.equals(confrimPwd) && TextUtils.isEmpty(newPwd)) {
            ViewUtils.showToast(this@OtpVerificationActivity, R.string.password_not_matched, false)
            return false
        } else return true
    }
}