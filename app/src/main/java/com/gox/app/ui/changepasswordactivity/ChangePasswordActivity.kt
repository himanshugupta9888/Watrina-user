package com.gox.app.ui.changepasswordactivity

import android.text.TextUtils
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.clearAll
import com.gox.basemodule.utils.ViewUtils
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.ResponseData
import com.gox.app.databinding.ActivityChangepasswordBinding
import com.gox.app.ui.signin.SignInActivity
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class ChangePasswordActivity : BaseActivity<ActivityChangepasswordBinding>(), ChangePasswordNavigator {

    lateinit var mViewDataBinding: ActivityChangepasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)


    override fun getLayoutId(): Int = R.layout.activity_changepassword

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivityChangepasswordBinding
        changePasswordViewModel = ViewModelProviders.of(this)[ChangePasswordViewModel::class.java]
        changePasswordViewModel.navigator = this
        this.mViewDataBinding.changePasswordViewModel = changePasswordViewModel
        baseLiveDataLoading = changePasswordViewModel.loadingProgress

        this.mViewDataBinding.changepasswordToolbarLayout.title_toolbar.setTitle(getString(R.string.change_password).removeSuffix("?"))

        this.mViewDataBinding.changepasswordToolbarLayout.toolbar_back_img.setOnClickListener { view ->
            finish()
        }

        changePasswordViewModel.changePasswordResponse.observe(this@ChangePasswordActivity, Observer<ResponseData> {
            loadingObservable.value = false
            ViewUtils.showToast(this@ChangePasswordActivity, R.string.password_updated, true)
            preferenceHelper.clearAll()
            openNewActivity(this@ChangePasswordActivity, SignInActivity::class.java, false)
            finishAffinity()
        })

        changePasswordViewModel.errorResponse.observe(this@ChangePasswordActivity, Observer<String> {
            loadingObservable.value = false
            ViewUtils.showToast(this@ChangePasswordActivity, it, false)
        })

    }

    override fun saveNewPassword() {

    }

    override fun checkValidation(): Boolean {

        if (changePasswordViewModel.newPassword.get().equals(changePasswordViewModel.confrimPassword.get()) &&
                !TextUtils.isEmpty(changePasswordViewModel.newPassword.get().toString()) &&
                !TextUtils.isEmpty(changePasswordViewModel.oldPassword.get()) &&
                !TextUtils.isEmpty(changePasswordViewModel.confrimPassword.get()))
            return true
        else {
            ViewUtils.showToast(this@ChangePasswordActivity, R.string.password_not_matched, false)
            return false
        }
    }

}