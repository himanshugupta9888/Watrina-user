package com.gox.xubermodule.ui.fragment.coupon

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.PromocodeModel
import com.gox.xubermodule.databinding.FragmentXuberCouponBinding
import com.gox.xubermodule.ui.activity.confirmbooking.ConfirmBookingViewModel
import com.gox.xubermodule.ui.adapter.XuberCouponAdapter

class XuberCouponFragment : BaseBottomDialogFragment<FragmentXuberCouponBinding>(), XuberCouponNavigator
        , XuberCouponAdapter.PromoCodeUse {

    companion object {
        fun newInstance() = XuberCouponFragment()
    }

    private lateinit var mFragmentTaxiCouponBinding: FragmentXuberCouponBinding
    private lateinit var mViewModel: XuberCouponViewModel
    private lateinit var mAdapter: XuberCouponAdapter
    private var mPromoCodeList: ArrayList<PromocodeModel>? = null
    private var confirmBookingViewModel: ConfirmBookingViewModel? = null
    override fun getLayoutId(): Int = R.layout.fragment_xuber_coupon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.XuberCustomBottomSheetDialogTheme)
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mFragmentTaxiCouponBinding = mViewDataBinding as FragmentXuberCouponBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(XuberCouponViewModel::class.java)
        confirmBookingViewModel = ViewModelProviders.of(activity!!).get(ConfirmBookingViewModel::class.java)
        mPromoCodeList = ArrayList()
        mViewModel.navigator = this
        mFragmentTaxiCouponBinding.viewModel = mViewModel
        mFragmentTaxiCouponBinding.viewPagerCoupons.offscreenPageLimit = 1
        mFragmentTaxiCouponBinding.tabLayout.setupWithViewPager(mFragmentTaxiCouponBinding.viewPagerCoupons, true)
        confirmBookingViewModel!!.getPromoCode().observe(this,
                Observer {
                    if (it.statusCode == "200") {
                        if (it.responseData.isNotEmpty()) {
                            mPromoCodeList!!.addAll(it.responseData)
                            mAdapter = XuberCouponAdapter(activity!!, mPromoCodeList!!)
                            mFragmentTaxiCouponBinding.viewPagerCoupons.adapter = mAdapter
                            mAdapter.setOnClickListener(this)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                })
    }

    override fun closePopup() {
        dismiss()
    }

    override fun useCoupon(promo: PromocodeModel) {
        confirmBookingViewModel?.setSelectedPromo(promo)
        dismiss()
    }
}
