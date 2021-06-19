package com.gox.taximodule.ui.fragment.reason

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.data.dto.ReasonModel
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.databinding.ReasonFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.adapter.ReasonAdapter


class ReasonFragment : BaseDialogFragment<ReasonFragmentBinding>() {
    private lateinit var mReasonFragmentBinding: ReasonFragmentBinding
    private lateinit var mViewModel: ReasonViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private var mReasonList: ArrayList<ReasonModel>? = null
    private var mReasonType: ArrayList<String>? = null
    private var mRequestId: Int? = null
    private var mReason: String? = null
    private var mReasonAdapter: ReasonAdapter? = null

    companion object {
        fun newInstance(requestID: Int): ReasonFragment {
            val fragment = ReasonFragment()
            val args = Bundle()
            args.putInt("requestId", requestID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.reason_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mReasonFragmentBinding = mViewDataBinding as ReasonFragmentBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ReasonViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        /* mTaxiMainViewModel.getRequestId().observe(this, Observer {
             mRequestId = it
         })*/
        mReasonList = ArrayList()
        mReasonType = ArrayList()
        mReasonType!!.clear()
        mReasonFragmentBinding.executePendingBindings()
        mTaxiMainViewModel.getReasonResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mReasonList!!.clear()
                mReasonList!!.addAll(it.reasonModel!!)
                if (mReasonList!!.size > 0) {
                    mReasonAdapter = ReasonAdapter(activity!!, mReasonList!!)
                    mReasonFragmentBinding.reasonadapter = mReasonAdapter
                    mReasonFragmentBinding.reasonadapter!!.notifyDataSetChanged()
                    mReasonAdapter!!.setOnClickListener(mOnAdapterClickListener)
                }
            }
        })
        mReasonFragmentBinding.ivClear.setOnClickListener {
            dismiss()
        }

        mRequestId = arguments!!.getInt("requestId")

        mReasonFragmentBinding.reasonSubmitButton.setOnClickListener {
            val reqEstimateModel = ReqEstimateModel()
            reqEstimateModel.id = mRequestId
            reqEstimateModel.reason = mReason
            mReason = mReasonFragmentBinding.etComents.text.toString()
            if (mReason.toString() != "") {
                mTaxiMainViewModel.cancelRideRequest(reqEstimateModel)
            } else {
                ViewUtils.showToast(activity!!, "Enter Valid Reason", true)
            }


        }

    }

    fun setCancelRide() {
        val reqEstimateModel = ReqEstimateModel()
        reqEstimateModel.id = mRequestId
        reqEstimateModel.reason = mReason
        mTaxiMainViewModel.cancelRideRequest(reqEstimateModel)
    }

    private val mOnAdapterClickListener = object : OnViewClickListener {
        override fun onClick(position: Int) {
            val mReasonModel = mReasonList!![position]
            if (mReasonModel.reason.toString().toLowerCase().equals("others")) {
                mReasonFragmentBinding.etComents.visibility = View.VISIBLE
                mReasonFragmentBinding.reasonSubmitButton.visibility = View.VISIBLE
            } else {
                mReason = mReasonModel.reason.toString()
                setCancelRide()
                dismiss()
            }

        }


    }
}
