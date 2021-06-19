package com.gox.app.ui.splash

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Handler
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.gox.app.BuildConfig
import com.gox.app.R
import com.gox.app.data.LocalConstant
import com.gox.app.data.repositary.remote.model.BaseApiResponse
import com.gox.app.databinding.ActivitySplashBinding
import com.gox.app.ui.dashboard.UserDashboardActivity
import com.gox.app.ui.onboard.OnBoardActivity
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.utils.LocaleUtils
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>(), SplashNavigator {

    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private lateinit var customPreference: SharedPreferences

    public override fun getLayoutId() = R.layout.activity_splash

    @SuppressLint("CommitPrefEdits")
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        val splashViewModel = SplashViewModel()
        splashViewModel.navigator = this

        LocaleUtils.setNewLocale(this, LocaleUtils.getLanguagePref(this)!!)

        if (isNetworkConnected)
            splashViewModel.getBaseConfig()

        customPreference = BaseApplication.getCustomPreference!!

        getDeviceToken()

        observeBaseApiResponse(splashViewModel)
    }

    private fun observeBaseApiResponse(splashViewModel: SplashViewModel) {
        splashViewModel.baseApiResponse.observe(this, Observer<BaseApiResponse> {
            LocalConstant.languages = it.responseData.appsetting.languages
            setDynamicBaseURL(it)
            setBaseSettings(it)
            moveToHome()
        })
    }

    private fun getDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        //  Log.w("Tag", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    customPreference.edit().putString(PreferenceKey.DEVICE_TOKEN, task.result?.token).apply()
                })
    }

    private fun setBaseSettings(it: BaseApiResponse) {
        customPreference.run {
            edit().putString(PreferenceKey.BASE_CONFIG_RESPONSE, Gson().toJson(it.responseData)).apply()
            edit().putInt(PreferenceKey.SEND_SMS, it.responseData.appsetting.send_sms).apply()
            edit().putInt(PreferenceKey.SEND_EMAIL, it.responseData.appsetting.send_email).apply()
            edit().putString(PreferenceKey.RIDE_OTP, Gson().toJson(it.responseData.appsetting.ride_otp)).apply()
            edit().putString(PreferenceKey.ORDER_OTP, Gson().toJson(it.responseData.appsetting.order_otp)).apply()
            edit().putString(PreferenceKey.SERVICE_OTP, Gson().toJson(it.responseData.appsetting.service_otp)).apply()
            edit().putString(PreferenceKey.DEMO_MODE, Gson().toJson(it.responseData.appsetting.demo_mode)).apply()
            edit().putString(PreferenceKey.SOCIAL_LOGIN, Gson().toJson(it.responseData.appsetting.social_login)).apply()
            edit().putString(PreferenceKey.REFERRAL, Gson().toJson(it.responseData.appsetting.referral)).apply()
            edit().putString(PreferenceKey.PROVIDER_NEGATIVE_BALANCE, Gson().toJson(it.responseData.appsetting.provider_negative_balance)).apply()
            edit().putString(PreferenceKey.PAYMENTLIST, Gson().toJson(it.responseData.appsetting.payments)).apply()
        }
        customPreference.edit().putString(PreferenceKey.GOOGLE_API_KEY, it.responseData.appsetting.android_key).apply()
        for (element in it.responseData.appsetting.payments) {
            when (element.name.toLowerCase(Locale.getDefault())) {
                Constant.PaymentMode.CARD -> for (data in element.credentials) {
                    if (data.name.toLowerCase(Locale.getDefault()) == "stripe_publishable_key")
                        customPreference.edit().putString(PreferenceKey.STRIPE_KEY, data.value).apply()
                }
            }
        }
    }

    private fun setDynamicBaseURL(it: BaseApiResponse) {
        customPreference.run {
            edit().putString(PreferenceKey.BASE_URL, it.responseData.base_url).apply()
            edit().putString(PreferenceKey.PRIVACY_URL, it.responseData.appsetting.cmspage.privacypolicy).apply()
            edit().putString(PreferenceKey.TERMS_CONDITION, it.responseData.appsetting.cmspage.terms).apply()
        }

        it.responseData.services.forEach {
            when (it.admin_service) {
                Constant.ModuleTypes.TRANSPORT -> customPreference.edit().putString(PreferenceKey.TRANSPORT_URL, it.base_url).apply()
                Constant.ModuleTypes.ORDER -> customPreference.edit().putString(PreferenceKey.ORDER_URL, it.base_url).apply()
                Constant.ModuleTypes.SERVICE -> customPreference.edit().putString(PreferenceKey.SERVICE_URL, it.base_url).apply()
            }
        }
    }


    override fun moveToHome() {
        val timer: Long = if (BuildConfig.DEBUG) 2000 else 3000
        Handler().postDelayed({
            if (preference.getValue(PreferenceKey.ACCESS_TOKEN, "")!! == "")
                openNewActivity(this, OnBoardActivity::class.java, true)
            else openNewActivity(this, UserDashboardActivity::class.java, true)
        }, timer)
    }
}
