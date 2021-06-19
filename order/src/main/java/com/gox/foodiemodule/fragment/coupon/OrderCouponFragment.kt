package com.gox.foodiemodule.fragment.coupon

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.OrderCouponAdapter
import com.gox.foodiemodule.data.model.PromocodeModel
import com.gox.foodiemodule.databinding.FragmentOrderCouponBinding
import com.gox.foodiemodule.ui.viewCartActivity.ViewCartViewModel

class OrderCouponFragment : BaseBottomDialogFragment<FragmentOrderCouponBinding>()
        , OrderCouponNavigator, OrderCouponAdapter.PromoCodeUse {

    companion object {
        fun newInstance() = OrderCouponFragment()
    }

    private lateinit var mFragmentTaxiCouponBinding: FragmentOrderCouponBinding
    private lateinit var mViewModel: OrderCouponViewModel
    private lateinit var mAdapter: OrderCouponAdapter
    private var mPromoCodeList: ArrayList<PromocodeModel>? = null
    private var mViewCartViewModel: ViewCartViewModel? = null
    override fun getLayoutId(): Int = R.layout.fragment_order_coupon


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.OrderCustomBottomSheetDialogTheme)
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mFragmentTaxiCouponBinding = mViewDataBinding as FragmentOrderCouponBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(OrderCouponViewModel::class.java)
        mViewCartViewModel = ViewModelProviders.of(activity!!).get(ViewCartViewModel::class.java)
        mPromoCodeList = ArrayList()
        mViewModel.navigator = this
        mFragmentTaxiCouponBinding.viewModel = mViewModel
        mFragmentTaxiCouponBinding.viewPagerCoupons.offscreenPageLimit = 1
        mFragmentTaxiCouponBinding.tabLayout.setupWithViewPager(mFragmentTaxiCouponBinding.viewPagerCoupons, true)
        mViewCartViewModel!!.promoCodeResponse.observe(this,
                Observer {
                    if (it.statusCode == "200") {
                        if (it.responseData.isNotEmpty()) {
                            mPromoCodeList!!.addAll(it.responseData)
                            mAdapter = OrderCouponAdapter(activity!!, mPromoCodeList!!)
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

    /*override fun useCoupon(promo: PromocodeModel) {
        confirmBookingViewModel?.setSelectedPromo(promo)
        dismiss()
    }*/
    override fun useCoupon(promo: PromocodeModel) {
        mViewCartViewModel?.setSelectedPromo(promo)
        dismiss()
    }

}

