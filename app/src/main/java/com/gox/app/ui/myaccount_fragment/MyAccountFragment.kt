package com.gox.app.ui.myaccount_fragment

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.clearAll
import com.gox.basemodule.common.payment.managepayment.ManagePaymentActivity
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.AccountMenuModel
import com.gox.app.databinding.FragmentMyaccountBinding
import com.gox.app.ui.invitereferals.InviteReferalsActivity
import com.gox.app.ui.language_setting.LanguageActivity
import com.gox.basemodule.common.manage_address.ManageAddressActivity
import com.gox.app.ui.onboard.OnBoardActivity
import com.gox.app.ui.privacypolicy.PrivacyPolicyActivity
import com.gox.app.ui.profile.ProfileActivity
import com.gox.app.ui.support.SupportActivity
import kotlinx.android.synthetic.main.fragment_myaccount.*

class MyAccountFragment : BaseFragment<FragmentMyaccountBinding>(), MyAccountFragmentNavigator {
    lateinit var mViewDataBinding: FragmentMyaccountBinding
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    val mViewModel = MyAccountFragmentViewModel()
    override fun getLayoutId(): Int = R.layout.fragment_myaccount

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as FragmentMyaccountBinding

        mViewModel.navigator = this
        mViewDataBinding.myaccountfragmentviewmodel = mViewModel
        (baseActivity).setSupportActionBar(mViewDataBinding.profileToolbar)
        setHasOptionsMenu(true)
        mViewModel.successResponse.observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(activity!!, it.message, true)
                    preferenceHelper.clearAll()
                    openNewActivity(activity, OnBoardActivity::class.java, false)
                    baseActivity.finishAffinity()
                }
            }
        })

        toolbar_logo.setOnClickListener {
            logout()
        }

        val accountMenuTitles = resources.getStringArray(R.array.account_title)
        val accountMenuIcons = resources.obtainTypedArray(R.array.account_icons)
        val accountMenus = List(accountMenuTitles.size) {
            AccountMenuModel(accountMenuTitles[it], accountMenuIcons.getResourceId(it, -1))
        }
        accountMenuIcons.recycle()
        mViewDataBinding.adapter = AccountMenuAdapter(accountMenus, object : OnClickListener {
            override fun onClick(position: Int) {
                when (position) {
                    0 ->goToProfile()
                    1 ->goToManageAddress()
                    2 ->goToCardList()
                    3 ->goToPayment()
                    4 ->gToprivacyPolicy()
                    5 ->goToSupport()
                    6 ->openLanguage()
                    7 ->goToInviteRefferals()

                }
            }
        })
    }

    private fun logout() {
        ViewUtils.showAlert(baseActivity, R.string.xjek_logout_alert, object : ViewCallBack.Alert {
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                mViewModel.Logout()
                dialog.dismiss()
            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {
                dialog.dismiss()
            }
        })
    }

    override fun goToProfile() {
        openNewActivity(activity, ProfileActivity::class.java, false)
    }

    override fun goToManageAddress() {
        val intent = Intent(activity, ManageAddressActivity::class.java)
        intent.putExtra("changeAddressFlag", "0")
        startActivity(intent)

    }

    override fun goToPayment() {
        val intent = Intent(activity, ManagePaymentActivity::class.java)
        intent.putExtra("activity_result_flag", "0")
        startActivity(intent)
    }

    override fun goToInviteRefferals() {
        openNewActivity(activity, InviteReferalsActivity::class.java, false)
    }

    override fun gToprivacyPolicy() {
        openNewActivity(activity, PrivacyPolicyActivity::class.java, false)
    }

    override fun goToSupport() {
        openNewActivity(activity, SupportActivity::class.java, false)
    }

    override fun openLanguage() {
        openNewActivity(activity, LanguageActivity::class.java, false)
    }

    override fun goToCardList() {
        val intent = Intent(activity, ActivityCardList::class.java)
        intent.putExtra("activity_result_flag", "0")
        startActivity(intent)
    }
}
