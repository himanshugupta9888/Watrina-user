package com.gox.app.ui.privacypolicy

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.databinding.ViewDataBinding
import com.gox.app.R
import com.gox.app.databinding.ActivityPrivacyPolicyBinding
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceKey
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyPolicyBinding>() {

    private val preference = BaseApplication.getCustomPreference


    override fun getLayoutId() = R.layout.activity_privacy_policy

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(mViewDataBinding: ViewDataBinding?) {



        val privacyUrl = preference!!.getString(PreferenceKey.PRIVACY_URL,"")
        toolbar_layout.title_toolbar.title =getString(R.string.privacy_plicy)
        privacy_policy_webview!!.loadUrl(privacyUrl.toString())
        privacy_policy_webview?.settings?.domStorageEnabled = true
        privacy_policy_webview!!.getSettings().javaScriptEnabled = true
        privacy_policy_webview?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                baseLiveDataLoading.value = true
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                baseLiveDataLoading.value = false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                baseLiveDataLoading.value = false
            }
        }
    }

    /*@SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        val privacyUrl = preference!!.getString(PreferenceKey.PRIVACY_URL,"")
        toolbar_layout.title_toolbar.title =getString(R.string.privacy_plicy)
        privacy_policy_webview!!.loadUrl(privacyUrl.toString())
        privacy_policy_webview?.settings?.domStorageEnabled = true
        privacy_policy_webview!!.getSettings().javaScriptEnabled = true
        privacy_policy_webview?.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

            }
        })
    }*/
}
