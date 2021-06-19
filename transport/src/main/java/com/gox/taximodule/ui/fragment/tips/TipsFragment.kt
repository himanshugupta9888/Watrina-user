package com.gox.taximodule.ui.fragment.tips

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.request.ReqTips
import com.gox.taximodule.databinding.TipsFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel

class TipsFragment : BaseDialogFragment<TipsFragmentBinding>(), TipsNavigator {

    private lateinit var viewModel: TipsViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private lateinit var mTipsFragmentBinding: TipsFragmentBinding
    private var mRequestId: Int? = null
    private var mTipsAmount: String? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)

    companion object {
        fun newInstance() = TipsFragment()
    }

    override fun getLayoutId(): Int = R.layout.tips_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mTipsFragmentBinding = mViewDataBinding as TipsFragmentBinding
        viewModel = ViewModelProviders.of(this).get(TipsViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        //   mTipsFragmentBinding.rbTips.isChecked = true


        mTaxiMainViewModel.getRequestId().observe(this, Observer {
            if (it != null) {
                mRequestId = it
            }
        })
        viewModel.getTipsResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(activity!!, it.message.toString(), true)
                    dismiss()
                }
            }
        })

        mTipsFragmentBinding.oneCurrTxt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + "0"
        mTipsFragmentBinding.secondCurrTxt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + "2"
        mTipsFragmentBinding.thirdCurrTxt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + "5"

        mTipsFragmentBinding.rbZero.setOnClickListener {
            mTipsAmount = "0"
            mTipsFragmentBinding.oneCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiPrimary))
            mTipsFragmentBinding.secondCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.thirdCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.otherCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.ettips.visibility = View.GONE
            mTipsFragmentBinding.oneDollarBtn.setImageResource(R.drawable.ic_radio_selected_btn)
            mTipsFragmentBinding.twoDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.fiveDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.othersBtn.setImageResource(R.drawable.ic_radio_normal_btn)
        }
        mTipsFragmentBinding.rbTwo.setOnClickListener {
            mTipsAmount = "2"
            mTipsFragmentBinding.oneCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.secondCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiPrimary))
            mTipsFragmentBinding.thirdCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.otherCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.ettips.visibility = View.GONE
            mTipsFragmentBinding.oneDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.twoDollarBtn.setImageResource(R.drawable.ic_radio_selected_btn)
            mTipsFragmentBinding.fiveDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.othersBtn.setImageResource(R.drawable.ic_radio_normal_btn)
        }
        mTipsFragmentBinding.rbFour.setOnClickListener {
            mTipsAmount = "5"
            mTipsFragmentBinding.oneCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.secondCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.thirdCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiPrimary))
            mTipsFragmentBinding.otherCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.ettips.visibility = View.GONE
            mTipsFragmentBinding.oneDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.twoDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.fiveDollarBtn.setImageResource(R.drawable.ic_radio_selected_btn)
            mTipsFragmentBinding.othersBtn.setImageResource(R.drawable.ic_radio_normal_btn)
        }
        mTipsFragmentBinding.rbOther.setOnClickListener {
            mTipsAmount = "other"
            mTipsFragmentBinding.oneCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.secondCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.thirdCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiGrey2))
            mTipsFragmentBinding.otherCurrTxt.setTextColor(ContextCompat.getColor(context!!, R.color.colorTaxiPrimary))
            mTipsFragmentBinding.ettips.visibility = View.VISIBLE
            mTipsFragmentBinding.oneDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.twoDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.fiveDollarBtn.setImageResource(R.drawable.ic_radio_normal_btn)
            mTipsFragmentBinding.othersBtn.setImageResource(R.drawable.ic_radio_selected_btn)
        }
        viewModel.getErrorResponse().observe(this, Observer {
            if (it != null) {
                ViewUtils.showToast(activity!!, it.toString(), false)
            }
        })

        mTipsFragmentBinding.tvAddTips.setOnClickListener {
            val otherTips = mTipsFragmentBinding.ettips.text.toString()
            if (mTipsAmount.equals("other")) {
                if (otherTips.equals("")) {
                    ViewUtils.showNormalToast(activity!!, "Enter Valid Amount")
                } else {
                    mTipsAmount = otherTips
                }
            } else {
                val reqTips = ReqTips()
                reqTips.requestId = mRequestId
                reqTips.tipsAmount = mTipsAmount

            }
            mTaxiMainViewModel.mAddTips.value = mTipsAmount
            dismiss()


        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.navigator = this
        mTipsFragmentBinding.viewModel = viewModel


    }

}
