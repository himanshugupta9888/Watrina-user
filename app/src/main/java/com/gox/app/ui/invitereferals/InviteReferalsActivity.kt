package com.gox.app.ui.invitereferals

import android.content.Intent
import android.os.Build
import android.text.Html
import androidx.core.text.HtmlCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.databinding.ActivityInviteFriendBinding
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import androidx.lifecycle.Observer
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue


class InviteReferalsActivity : BaseActivity<ActivityInviteFriendBinding>(), InviteReferalsNavigator {


    lateinit var mViewDataBinding: ActivityInviteFriendBinding
    private lateinit var mViewModel: InviteReferalsViewModel
    private var mShareLink: String? = null
    private var mReferralCode: String = ""
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private var currency: String? = preference.getValue(PreferenceKey.CURRENCY, "₹").toString()

    override fun getLayoutId(): Int = R.layout.activity_invite_friend

    override fun initView(mViewDataBinding: ViewDataBinding?) {


        this.mViewDataBinding = mViewDataBinding as ActivityInviteFriendBinding
        mViewDataBinding.toolbarLayout.title_toolbar.setTitle(R.string.invite_refferals)
        mViewDataBinding.toolbarLayout.toolbar_back_img.setOnClickListener { view ->
            finish()
        }

        mViewModel = ViewModelProviders.of(this).get(InviteReferalsViewModel::class.java)
        mViewModel.navigator = this
        mViewDataBinding.inviteReferalsViewModel = mViewModel
        baseLiveDataLoading = mViewModel.loadingProgress
        mViewModel.getProfile()
        mViewModel.getProfileResponse().observe(this, Observer {
            mReferralCode = it.profileData?.referalData?.referralCode.toString()
            mShareLink = getString(R.string.referal_share1) + getString(R.string.app_name) + getString(R.string.referal_share2) + mReferralCode +" "+getString(R.string.playstore_url) + packageName +" "+ getString(R.string.have_a_good_day)
            mViewModel.mUserReferralAmount.set(currency+it.profileData!!.referalData!!.userReferralAmount)
            mViewModel.mReferralCode.set(it.profileData!!.referalData!!.referralCode)
            mViewModel.mUserReferralCount.set(it.profileData!!.referalData!!.userReferralCount)
            /*mViewModel.mReferralInviteText.set(getString(R.string.invite_friends) +" ${preference.getValue(PreferenceKey.CURRENCY,"$")}"+ "${it.profileData!!.referalData!!.referalAmount} " +getString(R.string.every)  + " ${it.profileData!!.referalData!!.referralCount} " + getString(R.string.newusers))*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mViewDataBinding.invitefrndTv.text =Html.fromHtml(String.format(resources.getString(R.string.invite_referal_hint),
                        currency + it.profileData!!.referalData!!.referalAmount, it.profileData!!.referalData!!.referralCount), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }else{
                mViewDataBinding.invitefrndTv.text =Html.fromHtml(String.format(resources.getString(R.string.invite_referal_hint),
                        currency + it.profileData!!.referalData!!.referalAmount, it.profileData!!.referalData!!.referralCount))
            }
        })
    }

    override fun goToInviteOption() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        share.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
        share.putExtra(Intent.EXTRA_TEXT, mShareLink)
        startActivity(Intent.createChooser(share, "Share"))
    }

}