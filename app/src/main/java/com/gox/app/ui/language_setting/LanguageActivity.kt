package com.gox.app.ui.language_setting

import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.gox.app.R
import com.gox.app.databinding.ActivityLanguageBinding
import com.gox.app.ui.splash.SplashActivity
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.extensions.provideViewModel
import com.gox.basemodule.utils.LocaleUtils
import com.gox.basemodule.utils.ViewUtils
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(), LanguageNavigator {

    private lateinit var binding: ActivityLanguageBinding
    private lateinit var mViewModel: LanguageViewModel
    private lateinit var selectedLanguage: String

    override fun getLayoutId(): Int = R.layout.activity_language

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        binding = mViewDataBinding as ActivityLanguageBinding
        binding.lifecycleOwner = this

        mViewModel = provideViewModel {
            LanguageViewModel()
        }

        selectedLanguage = LocaleUtils.getLanguagePref(this)!!

        mViewModel.navigator = this
        mViewModel.setLanguage(selectedLanguage)

        binding.languageViewModel = mViewModel
        setSupportActionBar(binding.toolbar.title_toolbar)
        binding.toolbar.toolbar_back_img.setOnClickListener { onBackPressed() }
        binding.toolbar.title_toolbar.title = resources.getString(R.string.language)

    }

    override fun onLanguageChanged() {
        if (mViewModel.getCurrentLanguage() != selectedLanguage) {
            LocaleUtils.setNewLocale(this, mViewModel.getCurrentLanguage())
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        ViewUtils.showToast(this@LanguageActivity, getString(R.string.language_change_success), true)
    }
}
