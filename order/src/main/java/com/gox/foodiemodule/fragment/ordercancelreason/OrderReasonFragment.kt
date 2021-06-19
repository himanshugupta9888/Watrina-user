package com.gox.foodiemodule.fragment.ordercancelreason

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.OnViewClickListener
import com.gox.foodiemodule.adapter.OrderCancelReasonAdapter
import com.gox.foodiemodule.data.model.OrderReasonReqModel
import com.gox.foodiemodule.data.model.ReasonModel
import com.gox.foodiemodule.databinding.OrderReasonFragmentBinding
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailViewModel


class OrderReasonFragment : BaseDialogFragment<OrderReasonFragmentBinding>() {
    private lateinit var mReasonFragmentBinding: OrderReasonFragmentBinding
    private lateinit var mViewModel: OrderReasonViewModel
    private lateinit var mOrderDetailViewModel: OrderDetailViewModel
    private var mReasonList: ArrayList<ReasonModel>? = null
    private var mReasonType: ArrayList<String>? = null
    private var mRequestId: Int? = null
    private var mReason: String? = null
    private var mReasonAdapter: OrderCancelReasonAdapter? = null

    companion object {
        fun newInstance(requestID: Int): OrderReasonFragment {
            val fragment = OrderReasonFragment()
            val args = Bundle()
            args.putInt("requestId", requestID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.order_reason_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mReasonFragmentBinding = mViewDataBinding as OrderReasonFragmentBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(OrderReasonViewModel::class.java)
        mOrderDetailViewModel = ViewModelProviders.of(activity!!).get(OrderDetailViewModel::class.java)

        mReasonList = ArrayList()
        mReasonType = ArrayList()
        mReasonType!!.clear()
        mViewModel.getReason(Constant.ModuleTypes.ORDER)
        mReasonFragmentBinding.executePendingBindings()
        mViewModel.getReasonResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mReasonList!!.clear()
                mReasonList!!.addAll(it.reasonModel!!)
                if (mReasonList!!.size > 0) {
                    mReasonAdapter = OrderCancelReasonAdapter(activity!!, mReasonList!!)
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
            val reqEstimateModel = OrderReasonReqModel()
            reqEstimateModel.id = mRequestId
            reqEstimateModel.reason = mReason
            mReason = mReasonFragmentBinding.etComents.text.toString()
            if (mReason.toString() != "") {
                mViewModel.cancelOrder(reqEstimateModel)
            } else {
                ViewUtils.showToast(activity!!, "Enter Valid Reason", true)
            }


        }

    }

    fun setCancelRide(reqReasonModel:OrderReasonReqModel) {
        reqReasonModel.id = mRequestId
        reqReasonModel.reason = mReason

    }

    private val mOnAdapterClickListener = object : OnViewClickListener {
        override fun onClick(position: Int) {
            val mReasonModel = mReasonList!![position]
                mReason = mReasonModel.reason.toString()
                val reqReasonModel=OrderReasonReqModel()
                reqReasonModel.id=mRequestId
                reqReasonModel.reason = mReason
             //   setCancelRide(reqReasonModel)
            mViewModel.cancelOrder(reqReasonModel)
            mViewModel.getCancelResponse().observe(activity!!, Observer {
                if(it.statusCode=="200"){
                    ViewUtils.showToast(activity!!, it.message, true)
                    dismiss()
                    activity!!.finish()
                }
            })
            mViewModel.errorResponse.observe(activity!!, Observer {
                if(it!=null){
                    ViewUtils.showToast(activity!!, it, true)
                }
            })


        }


    }
}
