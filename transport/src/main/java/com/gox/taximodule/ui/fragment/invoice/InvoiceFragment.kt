package com.gox.taximodule.ui.fragment.invoice

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.request.ReqPaymentUpdateModel
import com.gox.taximodule.data.dto.request.ReqTips
import com.gox.taximodule.data.dto.response.ResCheckRequest
import com.gox.taximodule.databinding.InvoiceFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.fragment.rating.RatingFragment
import com.gox.taximodule.ui.fragment.tips.TipsFragment
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.jar.Manifest

@RuntimePermissions
class InvoiceFragment : BaseFragment<InvoiceFragmentBinding>(), InvoiceNavigator {

    private lateinit var mInvoiceFragmentBinding: InvoiceFragmentBinding
    private lateinit var mInvoiceViewModel: InvoiceViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private var mRequestId: Int? = null
    private var mCardId: String? = null
    private var mTipsAmount: String? = null

    companion object {
        fun newInstance() = InvoiceFragment()
    }

    override fun getLayoutId(): Int = R.layout.invoice_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mInvoiceFragmentBinding = mViewDataBinding as InvoiceFragmentBinding
        mInvoiceViewModel = ViewModelProviders.of(this).get(InvoiceViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)

        mInvoiceViewModel.navigator = this
        mInvoiceFragmentBinding.viewModel = mInvoiceViewModel

        mTaxiMainViewModel.getCheckRequestResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                if (it.responseData?.data!!.isNotEmpty()) {
                    mRequestId = it.responseData.data[0]!!.id
                        statusWhenDropped(it)
                }
            }
        })
        mInvoiceViewModel.getUpdatePaymentResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode.equals("200")) {
                    ViewUtils.showToast(activity!!, it.message, true)

                }
            }
        })
        mInvoiceFragmentBinding.tvAddTips.setOnClickListener {
            val mTipsFragment = TipsFragment.newInstance()
            mTipsFragment.isCancelable = true
            mTipsFragment.show(baseActivity.supportFragmentManager, mTipsFragment.tag)
        }
        mInvoiceFragmentBinding.tvTaxiChangePayment.setOnClickListener {

            val intent = Intent(activity!!, ActivityCardList::class.java)
            intent.putExtra("activity_result_flag", "1")
            intent.putExtra("payment_type", mTaxiMainViewModel.paymentType.get())
            startActivityForResult(intent, Constant.PAYMENT_TYPE_REQUEST_CODE)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun statusWhenDropped(it: ResCheckRequest?) {
        val result = it?.responseData?.data?.get(0)!!
        mInvoiceFragmentBinding.tvTaxiSourceLocation.text = result.s_address
        mInvoiceFragmentBinding.tvTaxiDestinationLocation.text = result.d_address
        mInvoiceFragmentBinding.tvBookingID.text = result.booking_id
        mInvoiceFragmentBinding.tvDistanceTravelled.text = "%.2f".format(result.distance) + " " + result.unit.toString()
        if (!result.travel_time.isNullOrEmpty() && result.travel_time.toInt() > 0) {
            mInvoiceFragmentBinding.tvTimeTaken.visibility = VISIBLE
            mInvoiceFragmentBinding.tvLabelTimeTaken.visibility = VISIBLE
            mInvoiceFragmentBinding.tvTimeTaken.text = result.travel_time.toString() + getString(R.string.mins)
        } else {
            mInvoiceFragmentBinding.tvTimeTaken.visibility = GONE
            mInvoiceFragmentBinding.tvLabelTimeTaken.visibility = GONE
        }
        if (result.payment?.discount!! > 0) {
            mInvoiceFragmentBinding.tvDisCountFare.text = "-" + result.currency +
                    " " + String.format("%.2f", result.payment.discount)
            mInvoiceFragmentBinding.tvLabelDisCount.visibility = VISIBLE
            mInvoiceFragmentBinding.tvDisCountFare.visibility = VISIBLE
        } else {
            mInvoiceFragmentBinding.tvLabelDisCount.visibility = GONE
            mInvoiceFragmentBinding.tvDisCountFare.visibility = GONE
        }
        if (result.payment.waiting_amount!! > 0) {
            mInvoiceFragmentBinding.tvLabelWaitingTimeFare.text = result.currency.toString() + " " + result.payment.waiting_amount.toString()
            mInvoiceFragmentBinding.tvLabelWaitingTimeFare.visibility = VISIBLE
            mInvoiceFragmentBinding.tvLabelWaitingTime.visibility = VISIBLE
        } else {
            mInvoiceFragmentBinding.tvLabelWaitingTimeFare.visibility = GONE
            mInvoiceFragmentBinding.tvLabelWaitingTime.visibility = GONE
        }
        if (result.payment.toll_charge!! > 0) {
            mInvoiceFragmentBinding.tvLabelTollFare.text = result.currency.toString() + " " + result.payment.toll_charge.toString()
            mInvoiceFragmentBinding.tvLabelTollFare.visibility = VISIBLE
            mInvoiceFragmentBinding.tvLabelToll.visibility = VISIBLE
        } else {
            mInvoiceFragmentBinding.tvLabelTollFare.visibility = GONE
            mInvoiceFragmentBinding.tvLabelToll.visibility = GONE
        }
        mInvoiceFragmentBinding.tvBaseFare.text = result.currency.toString() + " " + result.payment?.fixed
        mInvoiceFragmentBinding.tvDistanceFare.text = result.currency.toString() + " " + result.payment?.distance.toString()
        mInvoiceFragmentBinding.tvTax.text = result.currency.toString() + " " + result.payment?.tax.toString()
        mInvoiceFragmentBinding.totalPayableAmount.text = result.currency.toString() + " " + result.payment?.total.toString()
        mInvoiceFragmentBinding.tvTotalAmount.text = result.currency.toString() + " " + result.payment?.payable?.toInt()
        mInvoiceFragmentBinding.PayableAmount.text = result.currency.toString() + " " + result.payment?.payable?.toString()
        mTaxiMainViewModel.paymentType.set(result.payment_mode)
        if (result.payment_mode.equals("CASH")) {
            mInvoiceFragmentBinding.tvLabelTips.visibility = GONE
            mInvoiceFragmentBinding.tvAddTips.visibility = GONE
            mInvoiceFragmentBinding.btConfirm.visibility = GONE
            mInvoiceFragmentBinding.tvPaymentType.text = result.payment_mode
            mInvoiceFragmentBinding.ivPaymentMode.setImageDrawable(resources.getDrawable(R.drawable.ic_money))
            mInvoiceFragmentBinding.tvTaxiChangePayment.visibility = VISIBLE
        } else {
            mInvoiceFragmentBinding.tvLabelTips.visibility = VISIBLE
            mInvoiceFragmentBinding.tvAddTips.visibility = VISIBLE
            mInvoiceFragmentBinding.tvPaymentType.text = result.payment_mode
            mInvoiceFragmentBinding.btConfirm.visibility = VISIBLE
            mInvoiceFragmentBinding.ivPaymentMode.setImageDrawable(resources.getDrawable(R.drawable.ic_credit_card))
            mInvoiceFragmentBinding.tvTaxiChangePayment.visibility = VISIBLE
            mTaxiMainViewModel.mAddTips.observe(this, Observer {
                if (it != null) {
                    mInvoiceFragmentBinding.tvAddTips.visibility = GONE
                    mInvoiceFragmentBinding.tvTipsAmount.visibility = VISIBLE
                    mTipsAmount = it.toString()
                    mInvoiceFragmentBinding.tvTipsAmount.text = result.currency.toString() + " " + it.toString()
                }
            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                Constant.PAYMENT_TYPE_REQUEST_CODE -> {
                    val payment_type:String = data?.extras?.get("payment_type").toString()
                    mCardId = data?.extras?.get("card_id").toString()
                    if (payment_type!!.equals(Constant.PaymentMode.CASH,true)) {
                        mInvoiceFragmentBinding.ivPaymentMode.setImageDrawable(resources.getDrawable(R.drawable.ic_money))
                        mInvoiceFragmentBinding.tvPaymentType.text = payment_type.toString()
                        mTaxiMainViewModel.paymentType.set(Constant.PaymentMode.CASH.toUpperCase())
                    } else {
                        mInvoiceFragmentBinding.ivPaymentMode.setImageDrawable(resources.getDrawable(R.drawable.ic_credit_card))
                        mInvoiceFragmentBinding.tvPaymentType.text = Constant.PaymentMode.CARD.toUpperCase()
                        mTaxiMainViewModel.paymentType.set(Constant.PaymentMode.CARD.toUpperCase())
                        mTaxiMainViewModel.card_id.set(mCardId)
                        val reqPaymentUpdateModel = ReqPaymentUpdateModel()
                        reqPaymentUpdateModel.requestId = mRequestId
                        reqPaymentUpdateModel.paymentMode = Constant.PaymentMode.CARD.toUpperCase()
                        mInvoiceViewModel.updatePayment(reqPaymentUpdateModel)
                    }
                }
            }
        }
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun test(){

    }

    override fun showRating() {
        val reqTips = ReqTips()
        reqTips.requestId = mRequestId
        reqTips.cardId = mCardId
        reqTips.tipsAmount = mTipsAmount
        mInvoiceViewModel.setCardPayment(reqTips)
        mInvoiceViewModel.getInvoiceResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                ViewUtils.showNormalToast(activity!!, it.message!!)
                val mRatingFragment: RatingFragment = RatingFragment.newInstance()
                mRatingFragment.show(baseActivity.supportFragmentManager, mRatingFragment.tag)
            }
        })
        mInvoiceViewModel.getErrorResponse().observe(this, Observer {
            if (it != null) {
                ViewUtils.showNormalToast(activity!!, it.toString())
            }
        })

    }
}
