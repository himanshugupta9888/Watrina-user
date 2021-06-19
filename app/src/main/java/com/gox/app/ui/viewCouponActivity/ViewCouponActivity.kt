package com.gox.app.ui.viewCouponActivity

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.databinding.ActivityCouponviewBinding
import com.gox.app.utils.CommanMethods
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class ViewCouponActivity : BaseActivity<ActivityCouponviewBinding>(), ViewCouponNavigator {


    private lateinit var mViewDataBinding: ActivityCouponviewBinding
    private lateinit var viewCouponViewModel: ViewCouponViewModel
    override fun getLayoutId(): Int = R.layout.activity_couponview
    private lateinit var mPromoCodeModel:HomeMenuResponse.ResponseData.Promocode

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivityCouponviewBinding
        viewCouponViewModel = ViewModelProviders.of(this)[ViewCouponViewModel::class.java]
        viewCouponViewModel.navigator = this
        mViewDataBinding.viewCouponViewModel = viewCouponViewModel
        mPromoCodeModel = intent.extras!!.get("promocode") as HomeMenuResponse.ResponseData.Promocode
        mViewDataBinding.couponToolbar.title_toolbar.setTitle(R.string.coupon)
        mViewDataBinding.couponToolbar.toolbar_back_img.setOnClickListener { view ->
            closeActivity()
        }

        mViewDataBinding.couponCode.text = mPromoCodeModel.promo_code
        mViewDataBinding.couponDesc.text = mPromoCodeModel.promo_description
        mViewDataBinding.couponExpire.text = getString(R.string.OfferWillExpireIn) + CommanMethods.getLocalTimeStamp(mPromoCodeModel.expiration!!, "Req_Date_Month_year")
    }

    override fun closeActivity() {
        finish()
    }
}
