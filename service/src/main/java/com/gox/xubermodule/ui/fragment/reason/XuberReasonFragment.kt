package com.gox.xubermodule.ui.fragment.reason

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReasonModel
import com.gox.xubermodule.data.model.RequestCancelModel
import com.gox.xubermodule.databinding.XuberReasonFragmentBinding
import com.gox.xubermodule.ui.activity.serviceflowactivity.ServiceFlowViewModel
import com.gox.xubermodule.ui.adapter.OnViewClickListener
import com.gox.xubermodule.ui.adapter.XuberReasonAdapter


class XuberReasonFragment : BaseDialogFragment<XuberReasonFragmentBinding>() {
    private lateinit var mReasonFragmentBinding: XuberReasonFragmentBinding
    private lateinit var mServiceFlowViewModel: ServiceFlowViewModel
    private var mReasonList: ArrayList<ReasonModel>? = null
    private var mReasonType: ArrayList<String>? = null
    private var mRequestId: Int? = null
    private var mReason: String? = null
    private var mReasonAdapter: XuberReasonAdapter? = null

    companion object {
        fun newInstance(requestID: Int): XuberReasonFragment {
            val fragment = XuberReasonFragment()
            val args = Bundle()
            args.putInt("requestId", requestID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.xuber_reason_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mReasonFragmentBinding = mViewDataBinding as XuberReasonFragmentBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mServiceFlowViewModel = ViewModelProviders.of(activity!!).get(ServiceFlowViewModel::class.java)
        /* mTaxiMainViewModel.getRequestId().observe(this, Observer {
             mRequestId = it
         })*/
        mReasonList = ArrayList()
        mReasonType = ArrayList()
        mReasonType!!.clear()
        mReasonFragmentBinding.executePendingBindings()
        showLoading()
        mServiceFlowViewModel.mReasonResponseData.observe(this, Observer {
            if (it.statusCode == "200") {
                mReasonList!!.clear()
                mReasonList!!.addAll(it.reasonModel!!)
                if (mReasonList!!.size > 0) {
                    mReasonAdapter = XuberReasonAdapter(activity!!, mReasonList!!)
                    mReasonFragmentBinding.reasonadapter = mReasonAdapter
                    mReasonFragmentBinding.reasonadapter!!.notifyDataSetChanged()
                    mReasonAdapter!!.setOnClickListener(mOnAdapterClickListener)
                    hideLoading()
                }
            }
        })
        mReasonFragmentBinding.ivClear.setOnClickListener {
            dismiss()
            hideLoading()
        }

        mRequestId = arguments!!.getInt("requestId")

        mReasonFragmentBinding.reasonSubmitButton.setOnClickListener {
            val reqCancelModel = RequestCancelModel()
            reqCancelModel.requestId = mRequestId
            reqCancelModel.reason = mReason
            mReason = mReasonFragmentBinding.etComents.text.toString()
            if (mReason.toString() != "") {
                mServiceFlowViewModel.cancelRideRequest(reqCancelModel)
            } else {
                ViewUtils.showToast(activity!!, "Enter Valid Reason", true)
            }


        }

    }

    fun setCancelRide() {
        val reqCancelModel = RequestCancelModel()
        reqCancelModel.requestId = mRequestId
        reqCancelModel.reason = mReason
        mServiceFlowViewModel.cancelRideRequest(reqCancelModel)
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
