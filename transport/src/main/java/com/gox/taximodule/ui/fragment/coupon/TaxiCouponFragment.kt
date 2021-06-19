package com.gox.taximodule.ui.fragment.coupon

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.data.dto.EstimateFareResponse
import com.gox.taximodule.data.dto.PromocodeModel
import com.gox.taximodule.databinding.FragmentTaxiCouponBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.adapter.CouponAdapter


/**/

class TaxiCouponFragment : BaseBottomDialogFragment<FragmentTaxiCouponBinding>(), TaxiCouponNavigator {

    companion object {
        fun newInstance() = TaxiCouponFragment()
    }

    private lateinit var mFragmentTaxiCouponBinding: FragmentTaxiCouponBinding
    private lateinit var mViewModel: TaxiCouponViewModel
    private lateinit var mTaxiViewModel: TaxiMainViewModel
    private lateinit var mAdapter: CouponAdapter
    private var mPromoCodeList: ArrayList<EstimateFareResponse.ResponseData.Promocode>? = null
    private var mPromoId: Int? = null

    override fun getLayoutId(): Int = R.layout.fragment_taxi_coupon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mFragmentTaxiCouponBinding = mViewDataBinding as FragmentTaxiCouponBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(TaxiCouponViewModel::class.java)
        mTaxiViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        mPromoCodeList = ArrayList()
        mViewModel.navigator = this
        mFragmentTaxiCouponBinding.viewModel = mViewModel
        mFragmentTaxiCouponBinding.viewPagerCoupons.offscreenPageLimit = 1

        mTaxiViewModel.getPromoCode().observe(this, Observer {
            if (it != null) {
                mPromoId = it.id
            }
        })
        mFragmentTaxiCouponBinding.tabLayout.setupWithViewPager(mFragmentTaxiCouponBinding.viewPagerCoupons, true)
        mTaxiViewModel.getEstimateResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    if (it.responseData.promocodes.isNotEmpty()) {
                        mPromoCodeList!!.clear()
                        if (mPromoId != null) {
                            /*for (i in 0 until mPromoCodeList!!.size) {
                                mPromoCodeList!![i]. = mPromoId == mPromoCodeList!![i].id
                            }*/
                            mPromoCodeList!!.addAll(it.responseData.promocodes)
                            mAdapter = CouponAdapter(activity!!, mPromoCodeList!!)
                            mFragmentTaxiCouponBinding.viewPagerCoupons.adapter = mAdapter
                            mAdapter.setOnClickListener(mOnAdapterClickListener)

                        } else {
                            mPromoCodeList!!.addAll(it.responseData.promocodes)
                            mAdapter = CouponAdapter(activity!!, mPromoCodeList!!)
                            mFragmentTaxiCouponBinding.viewPagerCoupons.adapter = mAdapter
                            mAdapter.setOnClickListener(mOnAdapterClickListener)
                        }
                    } else {
                        ViewUtils.showNormalToast(activity!!, "No PromoCodes Available")
                        dismiss()
                    }
                }
            }
        })
    }

    override fun closePopup() {
        dismiss()
    }

    private val mOnAdapterClickListener = object : OnViewClickListener {
        override fun onClick(position: Int) {
            val promodata = mPromoCodeList!![position]
            mTaxiViewModel.setPromoCode(promodata)
            closePopup()
        }
    }
}
