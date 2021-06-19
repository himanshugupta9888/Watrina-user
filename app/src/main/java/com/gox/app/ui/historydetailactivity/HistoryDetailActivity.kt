@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.gox.app.ui.historydetailactivity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gox.app.R
import com.gox.app.adapter.DisputeReasonListAdapter
import com.gox.app.adapter.ReasonListClicklistner
import com.gox.app.data.repositary.remote.model.*
import com.gox.app.databinding.ActivityCurrentorderDetailLayoutBinding
import com.gox.app.databinding.DisputeStatusBinding
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.app.utils.CommanMethods
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.utils.AppUtils
import com.gox.basemodule.utils.ViewUtils
import okhttp3.ResponseBody


class HistoryDetailActivity : BaseActivity<ActivityCurrentorderDetailLayoutBinding>(),
        CurrentOrderDetailsNavigator {


    lateinit var mViewDataBinding: ActivityCurrentorderDetailLayoutBinding
    lateinit var transpotResponseData: HistoryDetailModel.HistoryDetailResponseData.Transport
    lateinit var dashboardViewModel: UserDashboardViewModel
    private var mselectedDisputeName: String? = null
    lateinit var history_type: String
    lateinit var selectedId: String
    private var isShowDisputeCreated: Boolean = false
    var lostitem = ""

    override fun getLayoutId(): Int = R.layout.activity_currentorder_detail_layout
    lateinit var historyDetailViewModel: HistoryDetailViewModel
    var servicetype: String? = null
    var userPicture: String? = ""


    override fun initView(mViewDataBinding: ViewDataBinding?) {


        Log.d("_D_EXT", "LOG " + intent.extras?.get("selected_trip_id"))

        selectedId = (intent.extras?.get("selected_trip_id") as String)
        history_type = (intent.extras?.get("history_type") as String)
        servicetype = (intent.extras?.get("servicetype") as String?)!!

        servicetype = servicetype?.let { it.toLowerCase() }

        this.mViewDataBinding = mViewDataBinding as ActivityCurrentorderDetailLayoutBinding
        historyDetailViewModel = HistoryDetailViewModel()
        dashboardViewModel = ViewModelProviders.of(this).get(UserDashboardViewModel::class.java)

        this.mViewDataBinding.currentOrderDetailModel = historyDetailViewModel
        historyDetailViewModel.navigator = this
        loadingObservable.value = true

        observeLiveData(historyDetailViewModel.loadingProgress) {
            loadingObservable.value = it
        }

        historyDetailViewModel.historyDetailResponse.observe(this@HistoryDetailActivity,
                Observer<HistoryDetailModel> {
                    loadingObservable.value = false
                    if (servicetype.toString().equals(Constant.ModuleTypes.TRANSPORT, true)) {
                        transpotResponseData = it.responseData.transport
                        userPicture = it.responseData.transport.user!!.picture
                        setTransportHistoryDetail(it.responseData.transport, Constant.ModuleTypes.TRANSPORT)
                    } else if (servicetype.toString().equals(Constant.ModuleTypes.SERVICE, true)) {
//                        serviceResponseData = it.responseData.service
                        transpotResponseData = it.responseData.service
                        setTransportHistoryDetail(transpotResponseData, Constant.ModuleTypes.SERVICE)

                    } else if (servicetype.toString().equals(Constant.ModuleTypes.ORDER, true)) {
//                        orderResponseData = it.responseData.order
                        transpotResponseData = it.responseData.order
                        setTransportHistoryDetail(transpotResponseData, Constant.ModuleTypes.ORDER)

                    }
                })

        historyDetailViewModel.errorResponse.observe(this, Observer {
            loadingObservable.value = false
            ViewUtils.showToast(applicationContext, it.toString(), false)
        })
        historyDetailViewModel.historyUpcomingDetailResponse.observe(this@HistoryDetailActivity,
                Observer<HistoryDetailModel> {
                    loadingObservable.value = false


                    if (servicetype.toString().equals(Constant.ModuleTypes.TRANSPORT, true)) {
                        transpotResponseData = it.responseData.transport
                        setUpcomingHistoryDetail(it.responseData, Constant.ModuleTypes.TRANSPORT)
                    } else if (servicetype.toString().equals(Constant.ModuleTypes.SERVICE, true)) {
//                        serviceResponseData = it.responseData.service
                        transpotResponseData = it.responseData.service
                        setUpcomingHistoryDetail(it.responseData, Constant.ModuleTypes.SERVICE)


                    } else if (servicetype.toString().equals(Constant.ModuleTypes.ORDER, true)) {
//                        orderResponseData = it.responseData.order
                        transpotResponseData = it.responseData.order
                        setUpcomingHistoryDetail(it.responseData, Constant.ModuleTypes.ORDER)


                    }

                })

        historyDetailViewModel.disputeListData.observe(this@HistoryDetailActivity,
                Observer<DisputeListModel> {
                    loadingObservable.value = false
                    Log.d("_D_Detailview", it.responseData[0].dispute_name)
                    setDisputeListData(it.responseData)
                })
        historyDetailViewModel.addDisputeResponse.observe(this@HistoryDetailActivity,
                Observer<ResponseBody> {
                    loadingObservable.value = false
                    mViewDataBinding.disputeBtn.text = getString(R.string.dispute_status)
                    isShowDisputeCreated = true
                    ViewUtils.showToast(this@HistoryDetailActivity, "Dispute Created Sucessfully", true)
                })
        historyDetailViewModel.addLostItemResponse.observe(this@HistoryDetailActivity,
                Observer<ResponseData> {
                    loadingObservable.value = false
                    mViewDataBinding.historydetailLossItemImgv.visibility = GONE

                    mViewDataBinding.lostItemList.text = lostitem
                    ViewUtils.showToast(this@HistoryDetailActivity, "LossItem Created Sucessfully", true)

                })
        historyDetailViewModel.disputeStatusResponse.observe(this@HistoryDetailActivity,
                Observer<DisputeStatusModel> {
                    loadingObservable.value = false
                    showDisputeStatus(it.responseData)
                })
        historyDetailViewModel.getErrorObservable().observe(this@HistoryDetailActivity,
                Observer<String> { message ->
                    loadingObservable.value = false
                    ViewUtils.showToast(this@HistoryDetailActivity, message, false)
                })

        historyDetailViewModel.cancelSuccessResponse.observe(this@HistoryDetailActivity
                , Observer<ResCommonSuccessModel> {
            if (it.statusCode == "200") {
                ViewUtils.showToast(this, it.message, true)
                finish()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (history_type.equals("upcoming")) {
            historyDetailViewModel.getUpcomingHistoryDeatail(selectedId.toString(), servicetype as String)
        } else if (history_type.equals("past")) {
            historyDetailViewModel.getHistoryDeatail(selectedId.toString(), servicetype as String)
        }
    }


    private fun setTransportHistoryDetail(transpotResponseData: HistoryDetailModel.HistoryDetailResponseData.Transport, serviceType: String) {

        mViewDataBinding.upcmngCancelBtn.visibility = View.GONE
        mViewDataBinding.historydetailStatusValueTv.text = transpotResponseData.status
        mViewDataBinding.historydetailPaymentmodeValTv.text = transpotResponseData.payment_mode
        mViewDataBinding.scheduleTimeLayout.visibility = View.GONE
        mViewDataBinding.scheduletimeView.visibility = View.GONE
        if (transpotResponseData.provider_vehicle != null) {

        }
        Glide.with(this@HistoryDetailActivity).load(transpotResponseData.provider!!.picture)
                .placeholder(R.drawable.ic_profile_place_holder)
                .into(mViewDataBinding.providerCimgv)

        mViewDataBinding.providerNameTv.text = (transpotResponseData.provider.first_name + " " +
                transpotResponseData.provider.last_name)

        mViewDataBinding.rvUser.rating = transpotResponseData.rating?.provider_rating?.toFloat()!!


        when (serviceType) {
            Constant.ModuleTypes.TRANSPORT -> {
                mViewDataBinding.tvHistoryCommentName.text = resources.getString(R.string.comments_for_trip)
            }

            Constant.ModuleTypes.SERVICE -> {
                mViewDataBinding.tvHistoryCommentName.text = getString(R.string.comments_for_service)
            }

            Constant.ModuleTypes.ORDER -> {
                mViewDataBinding.tvHistoryCommentName.text = getString(R.string.comments_for_order)
            }
        }

        if (transpotResponseData.rating!!.user_comment != null && !transpotResponseData.rating.user_comment!!.isEmpty()) {
            mViewDataBinding.itemLayout.visibility = View.VISIBLE
            mViewDataBinding.idHistrydetailCommentValTv.text = transpotResponseData.rating.user_comment
        } else {
            mViewDataBinding.itemLayout.visibility = View.GONE
        }

        if (transpotResponseData.dispute != null) {
            mViewDataBinding.disputeBtn.text = getString(R.string.dispute_status)
            isShowDisputeCreated = true
        }

        when (serviceType) {
            Constant.ModuleTypes.TRANSPORT -> {
                mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.booking_id
                mViewDataBinding.historydetailSrcValueTv.text = transpotResponseData.s_address
                mViewDataBinding.historydetailDestValueTv.text = transpotResponseData.d_address
                mViewDataBinding.vechileTypeTv.text = (transpotResponseData.provider_vehicle?.vehicle_model
                        ?: "" + "("
                        + transpotResponseData.provider_vehicle?.vehicle_no ?: "" + ")")
                mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                        .assigned_at!!, "Req_time") + "")
                mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.assigned_at,
                        "Req_Date_Month") + "")
                if (transpotResponseData.lost_item != null) {
                    mViewDataBinding.historydetailLossItemImgv.visibility = View.GONE
                    mViewDataBinding.lostItemTitle.text = getString(R.string.lost_item_created)
                    mViewDataBinding.lostItemList.text = transpotResponseData.lost_item.lost_item_name
                    mViewDataBinding.adminLosedItemTv.visibility = VISIBLE
                    mViewDataBinding.adminLosedItemCommentTv.visibility = VISIBLE
                    mViewDataBinding.adminLosedItemCommentTv.text = transpotResponseData.lost_item.comments
                    mViewDataBinding.lostItemStatusTv.visibility = VISIBLE
                    mViewDataBinding.lostItemStatusTv.text = transpotResponseData.lost_item.status

                } else {
                    mViewDataBinding.lostItemStatusTv.visibility = View.GONE
                }
            }
            Constant.ModuleTypes.ORDER -> {
                mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.store_order_invoice_id
                mViewDataBinding.historydetailSrcValueTv.text = transpotResponseData.pickup!!.store_location
                mViewDataBinding.historydetailDestValueTv.text = transpotResponseData.delivery!!.map_address.toString()
                mViewDataBinding.vechileTypeTv.text = (transpotResponseData.pickup.store_name)
                mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.created_at!!,
                        "Req_Date_Month") + "")
                mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                        .created_at, "Req_time") + "")
                mViewDataBinding.itemLayout.visibility = View.GONE
                mViewDataBinding.lossSomething.visibility = View.GONE
                mViewDataBinding.orderItemLayout.visibility = View.GONE
                mViewDataBinding.historydetailPaymentmodeValTv.text = transpotResponseData.order_invoice!!.payment_mode
//                mViewDataBinding.idOrderItemValTv.text = transpotResponseData.items?.get(0)?.product?.item_name
            }
            Constant.ModuleTypes.SERVICE -> {
                mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.booking_id
                mViewDataBinding.historydetailSrcValueTv.text = transpotResponseData.s_address + ""
                mViewDataBinding.historydetailDestValueTv.visibility = View.GONE
                mViewDataBinding.vechileTypeTv.text = (transpotResponseData.service!!.service_name)
                mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.assigned_at!!, "Req_time") + "")
                mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.assigned_at!!,
                        "Req_Date_Month") + "")
                mViewDataBinding.lossSomething.visibility = View.GONE
                mViewDataBinding.destLayout.visibility = View.GONE
                mViewDataBinding.locationView.visibility = View.GONE
            }
        }


    }

    private fun setUpcomingHistoryDetail(transpotResponseData: HistoryDetailModel.HistoryDetailResponseData, serviceType: String) {
        mViewDataBinding.bottomLayout.visibility = View.GONE
        mViewDataBinding.itemLayout.visibility = View.GONE
        mViewDataBinding.llUserName.visibility = View.GONE
        mViewDataBinding.idHistrydetailCommentValTv.visibility = View.GONE
        mViewDataBinding.lossSomething.visibility = View.GONE
        mViewDataBinding.scheduleTimeLayout.visibility = View.VISIBLE
        mViewDataBinding.scheduletimeView.visibility = View.VISIBLE

        /* mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.booking_id
         mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.assigned_at!!,
                 "Req_Date_Month") + "")
         mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                 .assigned_at!!, "Req_time") + "")
         mViewDataBinding.historydetailSrcValueTv.text = this.transpotResponseData.s_address
         mViewDataBinding.historydetailDestValueTv.text = transpotResponseData.d_address
         mViewDataBinding.historydetailStatusValueTv.text = transpotResponseData.status
         mViewDataBinding.historydetailPaymentmodeValTv.text = transpotResponseData.payment_mode
         mViewDataBinding.vechileTypeTv.text = (transpotResponseData.provider_vehicle!!.vehicle_model + "("
                 + transpotResponseData.provider_vehicle!!.vehicle_no + ")")*/


        when (serviceType) {
            Constant.ModuleTypes.TRANSPORT -> {
                mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.transport.booking_id
                mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.transport.assigned_at!!,
                        "Req_Date_Month") + "")
                mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                        .transport.assigned_at!!, "Req_time") + "")
                mViewDataBinding.scheduletimeValueTv.text = transpotResponseData.transport.schedule_time!!
                mViewDataBinding.historydetailSrcValueTv.text = this.transpotResponseData.s_address
                mViewDataBinding.historydetailDestValueTv.text = transpotResponseData.transport.d_address
                mViewDataBinding.historydetailStatusValueTv.text = transpotResponseData.transport.status
                mViewDataBinding.historydetailPaymentmodeValTv.text = transpotResponseData.transport.payment_mode
                mViewDataBinding.vechileTypeTv.text = (transpotResponseData.transport.ride!!.vehicle_name)

            }
            Constant.ModuleTypes.ORDER -> {

            }
            Constant.ModuleTypes.SERVICE -> {

                mViewDataBinding.currentorderetailSourceTv.text = getString(R.string.service_location)
                mViewDataBinding.destLayout.visibility = View.GONE
                mViewDataBinding.locationView.visibility = View.GONE


                /*mViewDataBinding.scheduletimeValueTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData.service.schedule_time!!, "Req_Date_Month") + "") + "," +(CommanMethods.getLocalTimeStamp(transpotResponseData.service.schedule_time!!, "Req_time") + "")*/

                mViewDataBinding.scheduletimeValueTv.text = transpotResponseData.service.schedule_time!!
                mViewDataBinding.currentorderdetailTitleTv.text = transpotResponseData.service.booking_id
                mViewDataBinding.currentorderdetailDateTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                        .service.assigned_at!!, "Req_Date_Month") + "")
                mViewDataBinding.timeCurrentorderdetailTv.text = (CommanMethods.getLocalTimeStamp(transpotResponseData
                        .service.assigned_at, "Req_time") + "")
                mViewDataBinding.historydetailSrcValueTv.text = this.transpotResponseData.s_address
                mViewDataBinding.historydetailStatusValueTv.text = transpotResponseData.service.status
                mViewDataBinding.historydetailPaymentmodeValTv.text = transpotResponseData.service.payment_mode
                mViewDataBinding.vechileTypeTv.text = transpotResponseData.service.service?.service_name.toString()

            }
        }


    }

    override fun goBack() {

        finish()
    }

    override fun onClickDispute() {

        if (isShowDisputeCreated) {

            when (servicetype?.toUpperCase()) {

                "RIDE" -> {
                    historyDetailViewModel.getDisputeStatus(transpotResponseData.id!!, Constant.HistoryDisputeAPIType.TRANSPORT)

                }

                Constant.ModuleTypes.TRANSPORT -> {
                    historyDetailViewModel.getDisputeStatus(transpotResponseData.id!!, Constant.HistoryDisputeAPIType.TRANSPORT)

                }

                "SERVICES" -> {
                    historyDetailViewModel.getDisputeStatus(transpotResponseData.id!!, Constant.HistoryDisputeAPIType.SERVICES)

                }

                Constant.ModuleTypes.SERVICE -> {
                    historyDetailViewModel.getDisputeStatus(transpotResponseData.id!!, Constant.HistoryDisputeAPIType.SERVICES)

                }
                Constant.ModuleTypes.ORDER -> {
                    historyDetailViewModel.getDisputeStatus(transpotResponseData.id!!, Constant.HistoryDisputeAPIType.ORDER)

                }

            }

        } else {
            getDisputeList()

        }
    }

    private fun showDisputeStatus(disputeStatusResponseData: DisputeStatusData) {
        val inflate = DataBindingUtil.inflate<DisputeStatusBinding>(LayoutInflater.from(baseContext)
                , R.layout.dispute_status, null, false)

        inflate.userDisputeTitle.text = (disputeStatusResponseData.dispute_type)
        inflate.userDisputeComment.text = (disputeStatusResponseData.dispute_name)
        inflate.userDisputeStatus.text = (disputeStatusResponseData.status)

        Glide.with(this).load(userPicture).placeholder(R.drawable.ic_profile_place_holder).into(inflate.usrPicture)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(inflate.root)
        dialog.show()
    }

    override fun onClickViewRecepit() {

        showInvoiceAlertDialog()
    }

    private fun showInvoiceAlertDialog() {
        val invoiceDialogView = LayoutInflater.from(this).inflate(R.layout.view_recepit,
                null, false);

        val builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
        builder.setView(invoiceDialogView)


        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        alertDialog.findViewById<ImageView>(R.id.cancel_dialog_img)!!
                .setOnClickListener { alertDialog.dismiss() }

        val tvDeliveryCharge: TextView? = alertDialog.findViewById(R.id.deliverycharges_tv)
        val tvTips: TextView? = alertDialog.findViewById(R.id.tvTips)
        val tvPackagingCharge: TextView? = alertDialog.findViewById(R.id.packing_charge_tv)
        val tvItemPrice: TextView? = alertDialog.findViewById(R.id.gross_pay_tv)
        val tvTaxFare: TextView? = alertDialog.findViewById(R.id.taxfare_tv)
        val tvTotalPayable: TextView? = alertDialog.findViewById(R.id.total_charge_value_tv)
        val tvBaseFare: TextView? = alertDialog.findViewById(R.id.basefare_tv)
        val tvWalletFare: TextView? = alertDialog.findViewById(R.id.wallet_tv)
        val tvHourlyFare: TextView? = alertDialog.findViewById(R.id.hourlyfare_tv)
        val tvDiscountApplied: TextView? = alertDialog.findViewById(R.id.disscount_applied_tv)
        val tvDistanceFare: TextView? = alertDialog.findViewById(R.id.tvDistanceFare)
        val tvTollCharge: TextView? = alertDialog.findViewById(R.id.tvTollCharge)
        val tvExtraCharge: TextView? = alertDialog.findViewById(R.id.tvExtraCharge)
        val tvTotalAmount: TextView? = alertDialog.findViewById(R.id.tvTotalAmount)
        val tvRoundOff: TextView? = alertDialog.findViewById(R.id.tvRoundOff)

        val rlPackage: RelativeLayout? = alertDialog.findViewById(R.id.packngCharges_layout)
        val rlDeliveryCharge: RelativeLayout? = alertDialog.findViewById(R.id.deliverycharges_layout)
        val rlItemPrice: RelativeLayout? = alertDialog.findViewById(R.id.gross_pay_layout)
        val rlBaseFare: RelativeLayout? = alertDialog.findViewById(R.id.basefare_layout)
        val rlHourlyFare: RelativeLayout? = alertDialog.findViewById(R.id.hourlyfare_layout)
        val rlDistanceFare: RelativeLayout? = alertDialog.findViewById(R.id.distanceFare_layout)
        val rlTips: RelativeLayout? = alertDialog.findViewById(R.id.rlTips)
        val rlTollCharge: RelativeLayout? = alertDialog.findViewById(R.id.rlTollCharge)
        val rlExtraCharges: RelativeLayout? = alertDialog.findViewById(R.id.rlExtraCharges)
        val rlWalletDeduction: RelativeLayout? = alertDialog.findViewById(R.id.rlWalletDeduction)
        val rlRoundOff: RelativeLayout? = alertDialog.findViewById(R.id.rlRoundOff)
        val rlDiscount: RelativeLayout? = alertDialog.findViewById(R.id.rlDiscount)
        val rlTotal: RelativeLayout? = alertDialog.findViewById(R.id.rlTotal)


        when {
            (transpotResponseData.payment!!.extra_charges ?: 0.0) > 0 -> {
                rlExtraCharges?.visibility = View.VISIBLE
            }
            else -> {
                rlExtraCharges?.visibility = View.GONE
            }
        }

        when {
            (transpotResponseData.payment!!.tips ?: 0.0) > 0 -> {
                rlTips?.visibility = View.VISIBLE
            }
            else -> {
                rlTips?.visibility = View.GONE
            }
        }

        when {
            (transpotResponseData.payment!!.toll_charge ?: 0.0) > 0 -> {
                rlTollCharge?.visibility = View.VISIBLE
            }
            else -> {
                rlTollCharge?.visibility = View.GONE
            }
        }

        when {
            (transpotResponseData.payment!!.wallet ?: 0.0) > 0 -> {
                rlWalletDeduction?.visibility = View.VISIBLE
            }
            else -> {
                rlWalletDeduction?.visibility = View.GONE
            }
        }

        when (servicetype?.toUpperCase()) {

            Constant.ModuleTypes.ORDER -> {
                when {
                    (transpotResponseData.order_invoice?.promocode_amount ?: 0.0) > 0 -> {
                        rlDiscount?.visibility = View.VISIBLE
                    }
                    else -> {
                        rlDiscount?.visibility = View.GONE
                    }
                }
            }

            else -> {
                when {

                    (transpotResponseData.payment!!.discount ?: 0.0) > 0 -> {
                        rlDiscount?.visibility = View.VISIBLE
                    }
                    else -> {
                        rlDiscount?.visibility = View.GONE
                    }
                }
            }
        }


        when {
            ((transpotResponseData.payment!!.round_of ?: 0.0) != 0.0) -> {
                rlRoundOff?.visibility = View.VISIBLE
            }
            else -> {
                rlRoundOff?.visibility = View.GONE
            }
        }

        when {
            ((transpotResponseData.order_invoice?.delivery_amount ?: 0.0) > 0.0) -> {
                tvDeliveryCharge?.visibility = View.VISIBLE
            }
            else -> {
                tvDeliveryCharge?.visibility = View.GONE
            }
        }

        when {
            ((transpotResponseData.order_invoice?.store_package_amount ?: 0.0) > 0.0) -> {
                rlPackage?.visibility = View.VISIBLE
            }
            else -> {
                rlPackage?.visibility = View.GONE
            }
        }

        /*when {
            ((transpotResponseData.order_invoice!!.item_price?:0.0) > 0.0) -> {
                rlItemPrice?.visibility = View.VISIBLE
            }
            else -> {
                rlItemPrice?.visibility = View.GONE
            }
        }*/

        when {
            ((transpotResponseData.order_invoice!!.delivery_amount ?: 0.0) > 0.0) -> {
                rlDeliveryCharge?.visibility = View.VISIBLE
            }
            else -> {
                rlDeliveryCharge?.visibility = View.GONE
            }
        }


        when (servicetype?.toUpperCase()) {
            Constant.ModuleTypes.ORDER -> {
                rlBaseFare?.visibility = View.GONE
                rlHourlyFare?.visibility = View.GONE
                rlDistanceFare?.visibility = View.GONE
                rlTollCharge?.visibility = View.GONE
                rlTotal?.visibility = View.GONE
                rlTips?.visibility = View.GONE
                rlExtraCharges?.visibility = View.GONE
                rlRoundOff?.visibility = View.GONE

                val itemPrice: Double = transpotResponseData.order_invoice?.items?.sumByDouble {
                    it?.item_price ?: 0.0
                } ?: 0.0

                tvItemPrice?.text = AppUtils.getNumberFormat()?.format(itemPrice) ?: "0.0"
                tvTaxFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.order_invoice?.tax_amount
                        ?: 0.0) ?: "0.0"
                tvPackagingCharge?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.order_invoice?.store_package_amount
                        ?: 0.0) ?: "0.0"
                tvDeliveryCharge?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.order_invoice?.delivery_amount
                        ?: 0.0) ?: "0.0"
                tvDiscountApplied?.text = "-${AppUtils.getNumberFormat()?.format(transpotResponseData.order_invoice?.promocode_amount)}"
                tvTotalPayable?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.order_invoice?.total_amount
                        ?: 0.0) ?: "0.0"

            }
            Constant.ModuleTypes.TRANSPORT -> {
                rlPackage?.visibility = View.GONE
                rlDeliveryCharge?.visibility = View.GONE
                rlItemPrice?.visibility = View.GONE
                rlExtraCharges?.visibility = View.GONE

                tvBaseFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.fixed)
                        ?: "0.0"
                tvTaxFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.tax)
                        ?: "0.0"
                tvHourlyFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.hour)
                        ?: "0.0"
                tvDistanceFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.distance)
                        ?: "0.0"
                tvTips?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.tips)
                        ?: "0.0"
                tvTollCharge?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.toll_charge)
                        ?: "0.0"
                tvWalletFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.wallet)
                        ?: "0.0"
                tvTotalAmount?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.total)
                        ?: "0.0"
                tvRoundOff?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.round_of)
                        ?: "0.0"
                tvDiscountApplied?.text = "-${AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.discount)}"
                tvTotalPayable?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.payable)
                        ?: "0.0"
            }


            Constant.ModuleTypes.SERVICE -> {
                rlPackage?.visibility = View.GONE
                rlDeliveryCharge?.visibility = View.GONE
                rlItemPrice?.visibility = View.GONE
                rlTollCharge?.visibility = View.GONE

                tvBaseFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.fixed)
                        ?: "0.0"
                tvTaxFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.tax)
                        ?: "0.0"
                tvHourlyFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.hour)
                        ?: "0.0"
                tvDistanceFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.distance)
                        ?: "0.0"
                tvTips?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.tips)
                        ?: "0.0"
                tvExtraCharge?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.extra_charges)
                        ?: "0.0"
                tvWalletFare?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.wallet)
                        ?: "0.0"
                tvRoundOff?.text = AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.round_of)
                        ?: "0.0"
                tvDiscountApplied?.text = "-${AppUtils.getNumberFormat()?.format(transpotResponseData.payment!!.discount)
                        ?: "0.0"}"
//                tvTips?.text = (Constant.currency + transpotResponseData.payment!!.tips)

                val total: Double? = (transpotResponseData.payment?.fixed
                        ?: 0.0).plus(transpotResponseData.payment?.tax
                        ?: 0.0).plus(transpotResponseData.payment?.hour
                        ?: 0.0).plus(transpotResponseData.payment!!.distance
                        ?: 0.0).plus(transpotResponseData.payment!!.tips
                        ?: 0.0).plus(transpotResponseData.payment!!.extra_charges ?: 0.0)
                tvTotalAmount?.text = AppUtils.getNumberFormat()?.format(total) ?: "0.0"

                val payable: Double? = (total ?: 0.0).minus(transpotResponseData.payment!!.wallet
                        ?: 0.0).minus(transpotResponseData.payment!!.discount
                        ?: 0.0).plus(transpotResponseData.payment!!.round_of ?: 0.0)

                tvTotalPayable?.text = AppUtils.getNumberFormat()?.format(payable) ?: "0.0"
            }
        }



        alertDialog.show()
    }

    override fun onClickLossItem() {
        val inflate = DataBindingUtil.inflate<com.gox.app.databinding.LossitemCommentDialogBinding>(LayoutInflater.from(baseContext)
                , R.layout.lossitem_comment_dialog, null, false)


        val dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
        dialog.setContentView(inflate.root)
        dialog.show()

        inflate.sendBtn.setOnClickListener {
            loadingObservable.value = true
             lostitem = inflate.lossitemEt.text.toString()
            historyDetailViewModel.addLossItem(transpotResponseData.id!!, lostitem)
            dialog.dismiss()

        }
    }


    override fun onClickCancelBtn() {

        val builder = AlertDialog.Builder(this@HistoryDetailActivity)
        builder.setTitle(getString(R.string.upcoming_ride))
        builder.setMessage(getString(R.string.cancel_request_prompt))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            //api call
            loadingObservable.value = true
            historyDetailViewModel.getCancelRequest(servicetype as String, transpotResponseData.id.toString())

//            ViewUtils.showToast(baseContext, "success canceled", true)

        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun getDisputeList() {

        when (servicetype?.toUpperCase()) {
            Constant.ModuleTypes.TRANSPORT -> {
                historyDetailViewModel.getDisputeList("ride")

            }
            Constant.ModuleTypes.SERVICE -> {

                historyDetailViewModel.getDisputeList("services")

            }
            Constant.ModuleTypes.ORDER -> {
                historyDetailViewModel.getDisputeList(servicetype!!)

            }
        }

    }

    private fun setDisputeListData(disputeListData: List<DisputeListData>) {
        val inflate = DataBindingUtil.inflate<com.gox.app.databinding
        .DisputeResonDialogBinding>(LayoutInflater.from(baseContext)
                , R.layout.dispute_reson_dialog, null, false)
        inflate.disputeReasonListAdapter = DisputeReasonListAdapter(historyDetailViewModel, disputeListData)
        inflate.disputeReasonListAdapter!!.setOnClickListener(mOnAdapterClickListener)
        //disputeReasonListAdapter.setOnClickListener(mOnAdapterClickListener)


        historyDetailViewModel.getSelectedValue().observe(this, Observer {
            mselectedDisputeName = it
        })

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(inflate.root)
        dialog.show()
        inflate.applyFilter.setOnClickListener {
            loadingObservable.value = true
            dialog.dismiss()
            createDisputeRequest()
        }
    }


    private fun createDisputeRequest() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("id", transpotResponseData.id.toString())
        hashMap.put("dispute_type", "user")
        hashMap.put("provider_id", transpotResponseData.provider_id.toString())
        hashMap.put("dispute_name", mselectedDisputeName!!)
        hashMap.put("user_id", transpotResponseData.user_id.toString())
        if (servicetype!!.equals(Constant.ModuleTypes.ORDER, true))
            hashMap.put("store_id", transpotResponseData.store_id.toString())

        if (servicetype.equals(Constant.ModuleTypes.TRANSPORT, true)) {
            servicetype = "ride"
        } else if (servicetype.equals(Constant.ModuleTypes.SERVICE, true)) {
            servicetype = "service"
        } else {
            servicetype = "order"
        }
        historyDetailViewModel.addDispute(hashMap
                , servicetype!!)

    }


    private val mOnAdapterClickListener = object : ReasonListClicklistner {
        override fun reasonOnItemClick(disputeName: String) {
            historyDetailViewModel.setSelectedValue(disputeName)
        }
    }
}


