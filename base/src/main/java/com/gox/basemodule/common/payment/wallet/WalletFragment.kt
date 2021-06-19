package com.gox.basemodule.common.payment.wallet

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.databinding.FragmentBaseWalletBinding
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.model.ProfileResponse
import com.gox.basemodule.common.payment.managepayment.ManagePaymentViewModel
import com.gox.basemodule.utils.ViewUtils
import com.gox.basemodule.common.cardlist.ActivityCardList

class WalletFragment : BaseFragment<FragmentBaseWalletBinding>(), WalletNavigator {

    private lateinit var fragmentWalletBinding: FragmentBaseWalletBinding
    private lateinit var walletViewModel: WalletViewModel
    // private lateinit var rvPaymentModes: RecyclerView
    private var strAmount: String? = null
    private lateinit var managePaymentViewModel: ManagePaymentViewModel
    private var mActivityFlag: String? = null

    companion object {
        var loadingProgress: MutableLiveData<Boolean>? = null

    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_base_wallet
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        fragmentWalletBinding = mViewDataBinding as FragmentBaseWalletBinding
        walletViewModel = WalletViewModel(resources)
        walletViewModel.navigator = this
        fragmentWalletBinding.walletmodel = walletViewModel
        fragmentWalletBinding.lifecycleOwner = this
        managePaymentViewModel = ViewModelProviders.of(activity!!).get(ManagePaymentViewModel::class.java)
        walletViewModel.resources = activity!!.resources
        walletViewModel.showLoading = baseLiveDataLoading as MutableLiveData<Boolean>
        getApiResponse()
        walletViewModel.getProfile()
        managePaymentViewModel.getFlag().observe(this, Observer {
            if (it != null) {
                mActivityFlag = it
            } else {
                mActivityFlag = "0"
            }

        })
    }

    fun getApiResponse() {
        //Add Amount
        observeLiveData(walletViewModel.walletLiveResponse) {
            // walletViewModel.showLoading.value = true
            if (walletViewModel.walletLiveResponse.value!!.getStatusCode().equals("200")) {
                if (walletViewModel.walletLiveResponse.value!!.getResponseData() != null) {
                    walletViewModel.showLoading.value = false
                    val balance = walletViewModel.walletLiveResponse.value!!.getResponseData()!!.getWalletBalance()
                    Constant.walletAmount = balance!!
                    fragmentWalletBinding.tvWalletBal.setText(String.format(resources.getString(R.string.balance), balance) + "  " + managePaymentViewModel.strCurrencyType.value.toString())
                    walletViewModel.walletAmount.value = ""
                    ViewUtils.showNormalToast(activity!!, "" + resources.getString(R.string.amount_added));
                }
            }
        }

        walletViewModel.mProfileResponse.observe(this, Observer<ProfileResponse> { profileResponse ->
            walletViewModel.showLoading.value = true
            if (profileResponse.statusCode.equals("200")) {
                try {
                    walletViewModel.showLoading.value = false
                    var balance = walletViewModel.mProfileResponse.value!!.profileData!!.walletBalance
                    managePaymentViewModel.strCurrencyType.value = walletViewModel.mProfileResponse.value!!.profileData!!.currencySymbol!!.toString()
                    fragmentWalletBinding.tvWalletBal.setText(String.format(resources.getString(R.string.balance), balance) + "  " + managePaymentViewModel.strCurrencyType.value.toString())
                    fragmentWalletBinding.btFifty.setText(managePaymentViewModel.strCurrencyType.value.toString() + "50")
                    fragmentWalletBinding.btHundred.setText(managePaymentViewModel.strCurrencyType.value.toString() + "100")
                    fragmentWalletBinding.btThousand.setText(managePaymentViewModel.strCurrencyType.value.toString() + "1000")
                    fragmentWalletBinding.tvCurrencySymbol.setText(managePaymentViewModel.strCurrencyType.value.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun showErrorMsg(error: String) {
        loadingProgress?.value = false;
        ViewUtils.showToast(activity!!, error, false)
    }


    override fun addAmount(view: View) {
        when (view.id) {
            R.id.bt_fifty -> {
                strAmount = "50"
            }

            R.id.bt_hundred -> {
                strAmount = "100"
            }

            R.id.bt_thousand -> {
                strAmount = "1000"
            }
        }
        walletViewModel.walletAmount.value = strAmount
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.RequestCode.SELECTED_CARD -> {
                if (resultCode == Activity.RESULT_OK) {

                    val stripeID = if (data != null && data.hasExtra("cardStripeID")) data.getStringExtra("cardStripeID") else ""
                    walletViewModel.selectedStripeID.value = stripeID
                    walletViewModel.showLoading.value = true
                    walletViewModel.callAddAmtApi()
                }
            }

        }
    }

    override fun validate(): Boolean {
        if (walletViewModel.walletAmount.value.isNullOrEmpty()) {
            ViewUtils.showToast(activity!!, resources.getString(R.string.empty_wallet_amount), false)
            return false
        } else if (walletViewModel.selectedStripeID.value.isNullOrEmpty()) {
            ViewUtils.showToast(activity!!, resources.getString(R.string.empty_card), false)
            return false
        } else {
            return true
        }
    }


    override fun getCard() {
        val intent = Intent(activity!!, ActivityCardList::class.java)
        intent.putExtra("isFromWallet", true)
        startActivityForResult(intent, Constant.RequestCode.SELECTED_CARD)
    }
}
