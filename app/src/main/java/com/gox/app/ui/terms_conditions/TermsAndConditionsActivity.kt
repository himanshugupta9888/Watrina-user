package com.gox.app.ui.terms_conditions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.PreferenceKey
import com.gox.app.R

class TermsAndConditionsActivity : Activity() {
    private var mTermsConditions: WebView? = null
    private val preference = BaseApplication.getCustomPreference
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        val termsUrl = preference!!.getString(PreferenceKey.TERMS_CONDITION, "")
        Log.d("TERMSANDCONDITION_T","TERMSANDCONDITION"+termsUrl)
        mTermsConditions = findViewById(R.id.terms_conditions)
        mTermsConditions!!.loadUrl(termsUrl.toString())
        mTermsConditions?.settings?.domStorageEnabled = true
        mTermsConditions!!.getSettings().javaScriptEnabled = true
    }
}
