package com.gox.app.ui.support

import android.content.Intent
import android.net.Uri
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.BaseApiResponseData
import com.gox.app.data.repositary.remote.model.SupportDetails
import com.gox.app.databinding.ActivitySupportBinding
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class SupportActivity : BaseActivity<ActivitySupportBinding>(), SupportNavigator {

    lateinit var mViewDataBinding: ActivitySupportBinding
    private lateinit var supportDetails: SupportDetails
    private lateinit var supportViewModel: SupportViewModel
    private lateinit var supportURL:String


    val preference = PreferenceHelper(BaseApplication.baseApplication)

    override fun getLayoutId(): Int = R.layout.activity_support

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivitySupportBinding
        mViewDataBinding.toolbarLayout.title_toolbar.setTitle(R.string.support)
        mViewDataBinding.toolbarLayout.toolbar_back_img.setOnClickListener { view ->
            finish()
        }
        supportViewModel = ViewModelProviders.of(this)[SupportViewModel::class.java]
        mViewDataBinding.supportViewModel = supportViewModel
        supportViewModel.navigator = this

        val baseApiResponseString: String = BaseApplication.getCustomPreference!!.getString(PreferenceKey.BASE_CONFIG_RESPONSE, "")!!
        val baseApiResponsedata: BaseApiResponseData = Gson().fromJson<BaseApiResponseData>(baseApiResponseString, BaseApiResponseData::class.java)
        supportDetails = baseApiResponsedata.appsetting.supportdetails
        supportURL = baseApiResponsedata.appsetting.cmspage.help
        this.mViewDataBinding.phonenumberSupportTv.text = supportDetails.contact_number[0].number
        this.mViewDataBinding.mailSupportTv.text = supportDetails.contact_email
        this.mViewDataBinding.websiteSupportTv.text = supportURL

        phonenumber_support_tv.setOnClickListener {
            goToPhoneCall()
        }

        mail_support_tv.setOnClickListener {
            goToMail()
        }



    }

    override fun goToPhoneCall() {

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + supportDetails.contact_number[0].number.toString())
        startActivity(intent)
    }

    override fun goToMail() {

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", supportDetails.contact_email, null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.support_mail)
        emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.mail_mainbody)
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    override fun goToWebsite() {
              val i = Intent(Intent.ACTION_VIEW)
              i.data = Uri.parse(supportURL)
              startActivity(i)
    }



}