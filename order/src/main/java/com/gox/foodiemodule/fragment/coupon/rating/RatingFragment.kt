package com.gox.foodiemodule.fragment.coupon.rating

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ReqRatingModel
import com.gox.foodiemodule.databinding.FragmentRatingBinding
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailViewModel

class RatingFragment : BaseDialogFragment<FragmentRatingBinding>() {

    private lateinit var mRatingFragmentBinding: FragmentRatingBinding
    private lateinit var viewCartViewModel: OrderDetailViewModel

    private var reqRatingModel = ReqRatingModel()
    private var mOrderId: Int? = 0
    private var mShopId: Int? = 0
    private var mComment: String? = ""

    companion object {
        fun newInstance() = RatingFragment()
    }

    override fun getLayoutId() = R.layout.fragment_rating

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mRatingFragmentBinding = mViewDataBinding as FragmentRatingBinding
        viewCartViewModel = ViewModelProviders.of(activity!!).get(OrderDetailViewModel::class.java)
        mComment = mRatingFragmentBinding.etAddNote.text.toString()
        if(viewCartViewModel.orderType.equals("FOOD")) {
            mRatingFragmentBinding.txtRateRestayrant.text = getString(R.string.rate_the_restaurant)
        } else {
            mRatingFragmentBinding.txtRateRestayrant.text = getString(R.string.rate_the_shop)
        }
        viewCartViewModel.resOrderDetail.observe(this, Observer {
            if (it != null) if (it.statusCode == "200") {
                mOrderId = it.responseData?.id
                mShopId = it.responseData?.store_id
                if (it.responseData?.provider != null) {
                    mViewDataBinding.llUserName.visibility = View.VISIBLE
                    mViewDataBinding.rateServiceLabel.visibility = View.VISIBLE
                    mViewDataBinding.ratingService.visibility = View.VISIBLE
                    mViewDataBinding.tvRatingUserName.text = it.responseData.provider.first_name + " " + it.responseData.provider.last_name
                    mViewDataBinding.rateCardDriverRatingtv.text = it.responseData.provider.rating.toString()

                    if (it.responseData.provider.picture != null) ViewUtils.setImageViewGlide(activity!!,
                            mViewDataBinding.ratingProviderCiv, it.responseData.provider.picture)
                    else ViewUtils.setImageViewGlide(activity!!, mViewDataBinding.ratingProviderCiv, "")

                } else {
                    mViewDataBinding.llUserName.visibility = View.GONE
                    mViewDataBinding.llUserName.visibility = View.GONE
                    mViewDataBinding.rateServiceLabel.visibility = View.GONE
                    mViewDataBinding.ratingService.visibility = View.GONE
                }
            }
        })

        viewCartViewModel.getRatingResponse.observe(this, Observer {
            if (it != null) if (it.statusCode == "200") {
                ViewUtils.showToast(activity!!, it.message, true)
                dismiss()
                activity!!.finish()
            }
        })

        mRatingFragmentBinding.ratingService.rating = 1.0f
        mRatingFragmentBinding.ratingRestaurant.rating = 1.0f

        mRatingFragmentBinding.submit.setOnClickListener {
            reqRatingModel.requestId = mOrderId
            reqRatingModel.ProviderRating = mRatingFragmentBinding.ratingService.rating.toInt()
            reqRatingModel.ShopRating = mRatingFragmentBinding.ratingRestaurant.rating.toInt()
            reqRatingModel.shopId = mShopId
            reqRatingModel.comment = mRatingFragmentBinding.etAddNote.text.toString()
            viewCartViewModel.setRating(reqRatingModel)
        }
    }
}