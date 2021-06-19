package com.gox.taximodule.ui.fragment.rating

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.TaxiRideRequest
import com.gox.taximodule.data.dto.request.ReqRatingModel
import com.gox.taximodule.databinding.RatingFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainActivity
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.communication.CommunicationListener
import kotlinx.android.synthetic.main.rating_fragment.*

class RatingFragment : BaseDialogFragment<RatingFragmentBinding>(), RatingNavigator {

    private lateinit var mRatingFragmentBinding: RatingFragmentBinding
    private lateinit var mCommunicationListener: CommunicationListener
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private var mRequestId: Int? = null
    private var mRatingValue: Int? = null
    private var mComment: String? = null
    private var adminService: String? = null
    private var ratingSuccess = false


    companion object {
        fun newInstance() = RatingFragment()
    }

    private lateinit var viewModel: RatingViewModel

    override fun getLayoutId(): Int = R.layout.rating_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mRatingFragmentBinding = mViewDataBinding as RatingFragmentBinding
        mRatingFragmentBinding.tvRatingSubmit.setOnClickListener {
            val reqRatingModel = ReqRatingModel()
            reqRatingModel.requesterId = mRequestId.toString()
            reqRatingModel.adminService = adminService
            mRatingValue = mRatingFragmentBinding.rvUser.rating.toInt()
            mComment = mRatingFragmentBinding.commentEt.text.toString()
            reqRatingModel.rating = mRatingValue
            reqRatingModel.comment = mComment
            mTaxiMainViewModel.setRating(reqRatingModel)
            /**/
        }

        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        mRatingFragmentBinding.rvUser.rating = 1.0f
        mTaxiMainViewModel.getCheckRequest()
        mTaxiMainViewModel.getCheckRequestResponse().observe(this, Observer {
            val provider = it.responseData?.data?.get(0)?.provider
            tv_rating_user_name.text = "${provider?.first_name} ${provider?.last_name}"
            rate_card_driver_ratingtv.text = provider?.rating.toString()

            mRequestId = it.responseData?.data?.get(0)?.id
//            adminService = it.responseData?.data?.get(0)?.service_type?.admin_service_id
            adminService = Constant.ModuleTypes.TRANSPORT

            if (provider?.picture != null) {
                Glide.with(this)
                        .load(it.responseData.data[0]!!.provider?.picture)
                        .placeholder(R.drawable.ic_profile_place_holder)
                        .into(rating_provider_civ)
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_profile_place_holder)
                        .into(rating_provider_civ)
            }

            mTaxiMainViewModel.setDestinationAddress("")

        })


        mTaxiMainViewModel.getRatingResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ratingSuccess = true
                    ViewUtils.showToast(context!!, "" + it.message, true)
                    dismissDialog()
                    activity!!.finish()
                }
            }
        })

        mTaxiMainViewModel.errorResponse.observe(this, Observer {
            if(!ratingSuccess)
                ViewUtils.showToast(context!!, "" + it, false)
        })

    }

    /*override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TaxiMainActivity) mCommunicationListener = context
    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RatingViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        viewModel.navigator = this

        mRatingFragmentBinding.viewModel = viewModel
        /*mTaxiMainViewModel.getCheckRequestResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    if (it.responseData?.data?.get(0)?.status.equals("COMPLETED")) {
                        mProviderId = it.responseData?.data?.get(0)?.id
                        adminService = it.responseData?.data?.get(0)?.service_type?.admin_service_id


                        mRatingFragmentBinding.tvRatingUserName.text = it.responseData.data[0]!!.provider?.first_name
                        mRatingFragmentBinding.rateCardDriverRatingtv.text = it.responseData.data[0]!!.provider?.rating.toString()
                        mTaxiMainViewModel.setDestinationAddress("")
                    }
                }
            }
        })*/
        /*mTaxiMainViewModel.getRatingResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(context!!, "" + it.message, true)
                    dismissDialog()
                    activity!!.finish()
                }
            }
        })*/
    }

    override fun dismissDialog() {
        dismiss()
        TaxiRideRequest.setStatus("EMPTY")
        mCommunicationListener.onMessage("EMPTY")
    }
}