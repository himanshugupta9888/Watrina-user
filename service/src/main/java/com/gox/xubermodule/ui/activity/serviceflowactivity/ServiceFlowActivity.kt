package com.gox.xubermodule.ui.activity.serviceflowactivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.basemodule.common.chatmessage.ChatActivity
import com.gox.basemodule.data.*
import com.gox.basemodule.data.Constant.MapConfig.DEFAULT_LOCATION
import com.gox.basemodule.data.Constant.MapConfig.DEFAULT_ZOOM
import com.gox.basemodule.model.ReqPaymentUpdateModel
import com.gox.basemodule.socket.SocketListener
import com.gox.basemodule.socket.SocketManager
import com.gox.basemodule.utils.*
import com.gox.basemodule.utils.polyline.DirectionUtils
import com.gox.basemodule.utils.polyline.PolyLineListener
import com.gox.basemodule.utils.polyline.PolylineUtil
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReqTips
import com.gox.xubermodule.data.model.RequestCancelModel
import com.gox.xubermodule.data.model.ServiceCheckReqModel
import com.gox.xubermodule.databinding.ServiceFlowActivityBinding
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainActivity
import com.gox.xubermodule.ui.fragment.reason.XuberReasonFragment
import com.gox.xubermodule.utils.Utils.getLocalTime
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.service_flow_activity.*
import kotlinx.android.synthetic.main.xuber_tips_fragment.*
import org.json.JSONObject
import permissions.dispatcher.*
import java.util.*
import kotlin.collections.ArrayList

@RuntimePermissions
class ServiceFlowActivity : BaseActivity<ServiceFlowActivityBinding>(),
        ServiceFlowNavigator,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        PolyLineListener,
        Chronometer.OnChronometerTickListener {

    override fun getDistanceTime(meters: Double, seconds: Double) {
    }

    override fun goToLocationPick() {

    }

    override fun goToSourceLocationPick() {
    }

    private var localServiceTime: Long? = null
    private lateinit var mServiceFlowActivityBinding: ServiceFlowActivityBinding
    private lateinit var mServiceFlowViewModel: ServiceFlowViewModel
    private lateinit var fragmentMap: SupportMapFragment
    private lateinit var sheetBehavior: BottomSheetBehavior<FrameLayout>
    private var checkRequestTimer: Timer? = null
    private var mGoogleMap: GoogleMap? = null
    private var mLastKnownLocation: Location? = null
    private var mPolyline: Polyline? = null
    private var polyLine = ArrayList<LatLng>()
    private var srcLatLng: LatLng? = null
    private var mServiceID: Int? = null
    private var mRequestId: Int? = null
    private var mLocalFlag: String = ""
    private var userId: Int? = null
    private var providerId: Int? = null
    private var userName: String? = null
    private var providerName: String? = null
    var isInvoiceDialogShown: Boolean = false
    var isRatingDialogShown: Boolean = false
    var isTimerRunning: Boolean = false
    private var mRoomConnected: Boolean = false
    private var reqID: Int = 0
    private var mCardId: String? = null
    private var invoiceAlertDialog: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null
    private var startLatLng = LatLng(0.0, 0.0)
    private var endLatLng = LatLng(0.0, 0.0)
    private var srcMarker: Marker? = null
    private var polyUtil = PolyUtil()
    private var canDrawPolyLine: Boolean = true
    private var isFireBaseInitialized: Boolean = true
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private var otpVerifyFlag: String? = null
    private var mPaymentMode: String = ""
    override fun getLayoutId(): Int = R.layout.service_flow_activity


    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mServiceFlowActivityBinding = mViewDataBinding as ServiceFlowActivityBinding
        mServiceFlowViewModel = ViewModelProviders.of(this).get(ServiceFlowViewModel::class.java)
        mServiceFlowViewModel.navigator = this
        mServiceID = intent.getIntExtra("serviceId", 0)
        mServiceFlowActivityBinding.viewModel = mServiceFlowViewModel
        mServiceFlowActivityBinding.executePendingBindings()
        checkRequestTimer = Timer()
        otpVerifyFlag = BaseApplication.getCustomPreference!!.getString(PreferenceKey.SERVICE_OTP, "")!!
        sheetBehavior = from<FrameLayout>(container)

        if (BuildConfig.ENABLE_TIMER_CALL) {
            checkRequestTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mServiceFlowViewModel.getCheckRequest()
                }
            }, 0, 5000)
        } else {
            mServiceFlowViewModel.getCheckRequest()
        }

        SocketManager.onEvent(Constant.ROOM_NAME.STATUS, Emitter.Listener {
            if(it[0].toString().contains(Constant.ModuleTypes.SERVICE)){
                mRoomConnected = true
            }
        })

        SocketManager.onEvent(Constant.ROOM_NAME.SERVICE_REQ, Emitter.Listener {
            // checkRequestTimer!!.cancel()
            Log.e("SOCKET", "SOCKET_SK Service request " + it[0])
            mServiceFlowViewModel.getCheckRequest()
        })


        SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION,Emitter.Listener{
            updateViaSocket(it)
        })



        SocketManager.setOnSocketRefreshListener(object : SocketListener.connectionRefreshCallBack {
            override fun onRefresh() {
                SocketManager.emit(Constant.ROOM_NAME.SERVICE_ROOM_NAME, Constant.ROOM_ID.getServiceRoom(reqID))

                SocketManager.onEvent(Constant.ROOM_NAME.SERVICE_REQ, Emitter.Listener {
                    // checkRequestTimer!!.cancel()
                    Log.e("SOCKET", "SOCKET_SK Service request " + it[0])
                    mServiceFlowViewModel.getCheckRequest()
                })


                SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION,Emitter.Listener{
                    updateViaSocket(it)
                })
            }
        })

        mServiceFlowViewModel.currentStatus.value = ""
        mServiceFlowViewModel.getReasonList(Constant.ModuleTypes.SERVICE)
        mServiceFlowActivityBinding.fabXuberMenuChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        mServiceFlowViewModel.checkRequestResponse.observe(this, Observer {
            if (it.statusCode == "200") {
                if (it.responseData?.data!!.isNotEmpty()) {
//                    val mStatusCode = it.responseData.data[0]!!.status
                    // mServiceFlowActivityBinding.tvStatus.text = it.responseData.data[0]!!.service?.service_name
                    println("RRR :: currentStatus.value = ${mServiceFlowViewModel.currentStatus.value}")
                    println("RRR :: responseData :: Status = ${it.responseData.data[0]!!.status}")

                    if(!mRoomConnected){
                        reqID = it.responseData.data[0]?.id?:0
                        PreferenceHelper(BaseApplication.baseApplication).setValue(PreferenceKey.SERVICE_REQ_ID, reqID)
                        if (reqID != 0) {
                            SocketManager.emit(Constant.ROOM_NAME.SERVICE_ROOM_NAME, Constant.ROOM_ID.getServiceRoom(reqID))
                        }
                    }

                    srcLatLng = LatLng(it.responseData.data[0]?.s_latitude?:0.0, it.responseData.data[0]?.s_longitude?:0.0)
                    mServiceFlowViewModel.sourceLat.set(srcLatLng?.latitude.toString())
                    mServiceFlowViewModel.sourceLon.set(srcLatLng?.longitude.toString())

                    if (mServiceFlowViewModel.currentStatus.value != it.responseData.data[0]!!.status) {

                        mServiceFlowViewModel.currentStatus.value = it.responseData.data[0]!!.status
                        mRequestId = it.responseData.data[0]!!.id
                        storeChatData(it)


                        Log.d("REQUEST_TYPE", "REQUEST_TYPE" + mServiceFlowViewModel.currentStatus.value)

                        if (isFireBaseInitialized) {
                            isFireBaseInitialized = false
                            fireBaseLocationListener(it.responseData.data[0]!!.provider!!.id!!)
                        }

                        if (mServiceFlowViewModel.currentStatus.value.equals("SEARCHING")) {
                            stateWhenSearching()


                        } else if (mServiceFlowViewModel.currentStatus.value.equals("STARTED") || mServiceFlowViewModel.currentStatus.value.equals("ACCEPTED")) {

                            stateWhenStarted(it)


                        } else if (mServiceFlowViewModel.currentStatus.value.equals("ARRIVED")) {
                            stateWhenArrived(it)

                        } else if (mServiceFlowViewModel.currentStatus.value.equals("PICKEDUP")) {

                            stateWhenPickedUp(it)

                        } else if (mServiceFlowViewModel.currentStatus.value.equals("DROPPED")) {
                            stateWhenDropped(it)
                            // show order screen
                        } else if (mServiceFlowViewModel.currentStatus.value.equals("COMPLETED")) {
                            stateWhenCompleted(it)
                        } else {
                            mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE
                            mServiceFlowActivityBinding.llLocationLayout.visibility = View.VISIBLE
                            mServiceFlowActivityBinding.statusCardView.visibility = View.GONE
                        }
                    } else {

                    }
                } else {
                    mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE
                    mServiceFlowActivityBinding.llLocationLayout.visibility = View.VISIBLE
                    mServiceFlowActivityBinding.statusCardView.visibility = View.GONE
                    mServiceFlowActivityBinding.searchView.visibility = View.GONE
                    mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
                    /*if (!mServiceShown) {
                        showServiceScreen()
                    }*/

                    closeServiceFlow()
                }
            }
        })

        mServiceFlowViewModel.ratingResponseBody.observe(this, Observer {
            ViewUtils.showToast(this@ServiceFlowActivity, getString(R.string.rated_success), true)
            closeServiceFlow()
        })

        initialiseMap()
        initialiseMenus()

        /* mServiceFlowViewModel.getRequest().observe(this, Observer
         {
             mRequestId = it
         })*/

        mServiceFlowViewModel.errorResponse.observe(this, Observer<String> {
            ViewUtils.showToast(this, it, false)
        })

        mServiceFlowActivityBinding.btsearchCancelRequest.setOnClickListener {
            cancelRequest()
        }

        /*  mServiceFlowViewModel.successResponse.observe(this@ServiceFlowActivity, Observer {
              if (it != null) {
                  if (it.statusCode == "200") {
                      ViewUtils.showToast(this@ServiceFlowActivity, it.message, true)
                      closeServiceFlow()
                  }
              }
          })*/


    }

    private fun closeServiceFlow() {
        val intent = Intent(this, XuberMainActivity::class.java)
        intent.putExtra("finish", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }


    override fun onPause() {
        super.onPause()
        fragmentMap?.onPause()
    }

    private fun updateViaSocket(it: Array<Any>) {
        runOnUiThread {
            val data1 = it[0] as JSONObject
            mServiceFlowViewModel.latitude.value = data1.getString("lat").toDouble()
            mServiceFlowViewModel.longitude.value = data1.getString("lng").toDouble()

            if (startLatLng.latitude != 0.0) endLatLng = startLatLng
            startLatLng = LatLng(data1.getString("lat").toDouble(), data1.getString("lng").toDouble())

            if (endLatLng.latitude != 0.0 && polyLine.size > 0) {
                try {
                    CarMarkerAnimUtil().carAnimWithBearing(srcMarker!!, endLatLng, startLatLng)
                    polyLineRerouting(endLatLng, polyLine)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }/* else if (polyLine == null || polyLine.size < 1)
                        if (mServiceFlowViewModel.tempSrc.value != null)
                            drawRoute(mServiceFlowViewModel.tempSrc.value!!, mServiceFlowViewModel.tempDest.value!!)*/

        }
    }

    override fun onResume() {
        super.onResume()
        mServiceFlowViewModel.getCheckRequest()
        fragmentMap?.onResume()
    }


    private fun stateWhenCompleted(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!
        mServiceFlowActivityBinding.searchView.visibility = View.GONE
        mServiceFlowActivityBinding.btsearchCancelRequest.text = getString(R.string.completed)
        if (!isRatingDialogShown)
            showRatingAlertDialog(response)

        /*   val intent = Intent(this@ServiceFlowActivity, InvoiceActivity::class.java)
                             intent.putExtra("CheckRequestData", it.responseData.data[0])
                             startActivity(intent)*/
        //openNewActivity(this@ServiceFlowActivity, InvoiceActivity::class.java, true)


    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentMap?.onLowMemory()
    }


    private fun stateWhenDropped(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!
        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE
        mServiceFlowActivityBinding.llLocationLayout.visibility = View.GONE
        mServiceFlowActivityBinding.statusCardView.visibility = View.GONE
        mServiceFlowActivityBinding.statusCardView.visibility = View.VISIBLE
        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mServiceFlowActivityBinding.btsearchCancelRequest.visibility = View.VISIBLE
        mServiceFlowActivityBinding.searchView.visibility = View.GONE
        mServiceFlowActivityBinding.btsearchCancelRequest.text = getString(R.string.completed)
        mServiceFlowActivityBinding.btCancelRequest.visibility = View.VISIBLE
        mServiceFlowActivityBinding.tvOTP.visibility = View.GONE
        mServiceFlowActivityBinding.tvStatusDescription.text = getString(R.string.provider_completed)
        mServiceFlowActivityBinding.tvProviderName.text = response.provider?.first_name
        mServiceFlowActivityBinding.tvProviderRating.text = "%.2f".format(response.provider?.rating?:0.0)
        mServiceFlowActivityBinding.tvService.text = response.category?.service_category_name?:""
        mServiceFlowActivityBinding.tvServiceName.text = response.service!!.service_name
        mServiceFlowActivityBinding.fabXuberMenu.visibility = View.GONE
        /* val intent = Intent(this@ServiceFlowActivity, InvoiceActivity::class.java)
         intent.putExtra("CheckRequestData", it.responseData.data[0] as Serializable)
         startActivity(intent)*/

        if (!isInvoiceDialogShown)
            showInvoiceAlertDialog(response)


        /*     val invoiceActivity = InvoiceActivity()
             val fm = this@ServiceFlowActivity.supportFragmentManager
             invoiceActivity.show(fm, "invoicefragment")*/

    }

    private fun stateWhenPickedUp(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!

        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mServiceFlowActivityBinding.llLocationLayout.visibility = View.GONE
        mServiceFlowActivityBinding.statusCardView.visibility = View.VISIBLE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        mServiceFlowActivityBinding.btCancelRequest.visibility = View.GONE
        mServiceFlowActivityBinding.tvOTP.visibility = View.GONE
        mServiceFlowActivityBinding.cmXuberServiceTime.visibility = View.VISIBLE
        mServiceFlowActivityBinding.searchView.visibility = View.GONE
        mServiceFlowActivityBinding.fabXuberMenu.visibility = View.GONE

        mServiceFlowActivityBinding.tvProviderName.text = response.provider?.first_name
        mServiceFlowActivityBinding.tvProviderRating.text = "%.2f".format(response.provider?.rating?:0.0)
        mServiceFlowActivityBinding.tvService.text = response.category?.service_category_name?:""
        mServiceFlowActivityBinding.tvServiceName.text = response.service!!.service_name

        mServiceFlowActivityBinding.tvStatusDescription.text = getString(R.string.provider_pickedup)
        if (!isTimerRunning)
            startTheTimer(response.started_at)
        // to  show the map / with source and destination with marker including route.
    }

    @SuppressLint("SetTextI18n")
    private fun stateWhenArrived(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!

        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE
        mServiceFlowActivityBinding.llLocationLayout.visibility = View.GONE
        mServiceFlowActivityBinding.statusCardView.visibility = View.VISIBLE
        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mServiceFlowActivityBinding.searchView.visibility = View.GONE
        mServiceFlowActivityBinding.fabXuberMenu.visibility = View.GONE
        //                        mServiceFlowActivityBinding.btCancelRequest.visibility = View.GONE
        if (otpVerifyFlag.equals("1")) {
            mServiceFlowActivityBinding.tvOTP.visibility = View.VISIBLE
        } else {
            mServiceFlowActivityBinding.tvOTP.visibility = View.GONE
        }
        mServiceFlowActivityBinding.tvOTP.text = getString(R.string.otp) + ": " + response.otp
        mServiceFlowActivityBinding.tvProviderName.text = response.provider?.first_name
        mServiceFlowActivityBinding.tvProviderRating.text = "%.2f".format(response.provider?.rating?:0.0)
        mServiceFlowActivityBinding.tvService.text = response.category?.service_category_name?:""
        mServiceFlowActivityBinding.tvServiceName.text = response.service!!.service_name
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        mServiceFlowActivityBinding.tvStatusDescription.text = getString(R.string.provider_arrived)

        mServiceFlowActivityBinding.btCancelRequest.setOnClickListener {
            /* val mReasonFragment = XuberReasonFragment.newInstance(mRequestId!!)
             mReasonFragment.isCancelable = true
             mReasonFragment.show(supportFragmentManager, mReasonFragment.tag)*/

            val mReasonFragment: XuberReasonFragment = XuberReasonFragment.newInstance(mRequestId!!)
            supportFragmentManager.beginTransaction().replace(R.id.main_container, mReasonFragment
                    , mReasonFragment.tag)
                    .addToBackStack(mReasonFragment.tag).commit()
        }
        //    mServiceFlowViewModel.setDriverStatus("Driver has arrived your location")
        //   ViewUtils.showNormalToast(this, "" + it.responseData.data[0].status)
        // to  show the map / with source and destination with marker including route.
    }

    @SuppressLint("SetTextI18n")
    private fun stateWhenStarted(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!

        mLocalFlag = mServiceFlowViewModel.currentStatus.value!!
        mServiceFlowActivityBinding.llLocationLayout.visibility = View.GONE
        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mServiceFlowActivityBinding.statusCardView.visibility = View.VISIBLE
        mServiceFlowActivityBinding.fabXuberMenu.visibility = View.VISIBLE
        mServiceFlowActivityBinding.searchView.visibility = View.GONE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        mServiceFlowActivityBinding.tvStatusDescription.text = getString(R.string.provider_accept)
        Glide.with(this)
                .load(response.provider?.picture)
                .placeholder(R.drawable.ic_profile_place_holder)
                .into(mServiceFlowActivityBinding.civDriverProfile)

        mServiceFlowActivityBinding.tvProviderName.text = response.provider?.first_name
        mServiceFlowActivityBinding.tvProviderRating.text = "%.2f".format(response.provider?.rating?:0.0)
        mServiceFlowActivityBinding.tvService.text = response.category?.service_category_name?:""
        mServiceFlowActivityBinding.tvServiceName.text = response.service!!.service_name

        if (otpVerifyFlag.equals("1")) {
            mServiceFlowActivityBinding.tvOTP.visibility = View.VISIBLE
        } else {
            mServiceFlowActivityBinding.tvOTP.visibility = View.GONE
        }
        mServiceFlowActivityBinding.tvOTP.text = getString(R.string.otp) + ":" + it.responseData.data[0]!!.otp

        if (response.provider?.picture.toString() != "null") {
            Glide.with(this)
                    .load(it.responseData.data[0]!!.provider?.picture.toString())
                    .placeholder(R.drawable.ic_profile_place_holder)
                    .into(mServiceFlowActivityBinding.civDriverProfile)
        }

        val driverLatLng = LatLng(response.provider?.latitude!!.toDouble(),
                response.provider.longitude!!.toDouble())
        val phoneNumber = response.provider.mobile.toString()
        drawRoute(driverLatLng, srcLatLng!!)

        mServiceFlowActivityBinding.fabXuberMenuCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + phoneNumber)
            startActivity(intent)
        }

        mServiceFlowActivityBinding.btCancelRequest.setOnClickListener {
            val mReasonFragment = XuberReasonFragment.newInstance(mRequestId!!)
            mReasonFragment.isCancelable = true
            mReasonFragment.show(supportFragmentManager, mReasonFragment.tag)
        }
    }

    private fun storeChatData(it: ServiceCheckReqModel?) {
        val response = it?.responseData?.data?.get(0)!!
        mRequestId = response.id
        userId = response.user_id
        providerId = response.provider_id
        userName = response.user?.first_name + " " + response.user?.last_name
        providerName = response.provider?.first_name + " " + response.provider?.last_name

        if(mRequestId!=0) {
            PreferenceHelper(BaseApplication.baseApplication).setValue(PreferenceKey.SERVICE_REQ_ID, mRequestId)
            SocketManager.emit(Constant.ROOM_NAME.SERVICE_ROOM_NAME, Constant.ROOM_ID.getServiceRoom(mRequestId?:0))
        }else{
            Log.e("SOCKET", "SOCKET_SK service failed request id wrong")
        }

        preference.setValue(Constant.Chat.ADMIN_SERVICE, Constant.ModuleTypes.SERVICE)
        preference.setValue(Constant.Chat.USER_ID, userId)
        preference.setValue(Constant.Chat.REQUEST_ID, mRequestId)
        preference.setValue(Constant.Chat.PROVIDER_ID, providerId)
        preference.setValue(Constant.Chat.USER_NAME, userName)
        preference.setValue(Constant.Chat.PROVIDER_NAME, providerName)
    }

    private fun stateWhenSearching() {
        mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE
        mServiceFlowActivityBinding.llLocationLayout.visibility = View.GONE
        mServiceFlowActivityBinding.statusCardView.visibility = View.GONE
        mServiceFlowActivityBinding.searchView.visibility = View.VISIBLE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = false
    }


    @SuppressLint("SetTextI18n")
    private fun showRatingAlertDialog(data: ServiceCheckReqModel.ResponseData.Data?) {


        isRatingDialogShown = true
        val invoiceDialogView = LayoutInflater.from(this).inflate(R.layout.xuber_rating_fragment,
                null, false)

        val builder = AlertDialog.Builder(this@ServiceFlowActivity)
        builder.setView(invoiceDialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mAlertDialog?.isShowing?.let {
            mAlertDialog!!.hide()
        }

        /*if (mAlertDialog!!.isShowing) {
            mAlertDialog!!.hide()
        }*/
        alertDialog.show()

        alertDialog.findViewById<TextView>(R.id.tvProviderName)!!.text = data!!.provider!!.first_name + " " + data.provider!!.last_name

        Glide.with(this)
                .load(data.provider.picture)
                .placeholder(R.drawable.ic_profile_place_holder)
                .into(alertDialog.findViewById<CircleImageView>(R.id.civ_driver_profile)!!)


        alertDialog.findViewById<TextView>(R.id.btSubmitRating)!!
                .setOnClickListener {
                    val rating = alertDialog.findViewById<RatingBar>(R.id.rv_user)!!.rating
                    val comment = alertDialog.findViewById<EditText>(R.id.comment_et)!!.text.toString()

                    submitRatting(data, rating, comment)
                    alertDialog.dismiss()
                }

        alertDialog.show()
    }

    private fun submitRatting(data: ServiceCheckReqModel.ResponseData.Data, rating: Float, comment: String) {

        mServiceFlowViewModel.submitRating(data.admin_service_id.toString(), rating.toInt(), comment + "", data.id.toString())
    }

    private fun showInvoiceAlertDialog(data: ServiceCheckReqModel.ResponseData.Data?) {
        isInvoiceDialogShown = true
        val invoiceDialogView = LayoutInflater.from(this).inflate(R.layout.invoice_activity,
                null, false)

        invoiceAlertDialog = AlertDialog.Builder(this@ServiceFlowActivity).setView(invoiceDialogView)

        //finally creating the alert dialog and displaying it
        // val alertDialog = invoiceAlertDialog!!.create



        mAlertDialog = invoiceAlertDialog!!.create()
        mAlertDialog!!.show()


        val rlWallet:RelativeLayout? = mAlertDialog?.findViewById(R.id.rlWallet)
        val rlPromoCode:RelativeLayout? = mAlertDialog?.findViewById(R.id.rlPromoCode)
        val rlExtraCharges:RelativeLayout? = mAlertDialog?.findViewById(R.id.rlExtraCharges)

        val tvWalletDeduction:TextView? = mAlertDialog?.findViewById(R.id.tvWalletDeduction)
        val tvPromoCodeDeduction:TextView? = mAlertDialog?.findViewById(R.id.tvPromoCodeDeduction)
        val tvExtraCharges:TextView? = mAlertDialog?.findViewById(R.id.extra_charge_value_tv)

        mAlertDialog!!.findViewById<TextView>(R.id.basefare_value_tv)!!.text = (Constant.currency +
                data!!.payment!!.fixed)
        mAlertDialog!!.findViewById<TextView>(R.id.bookingid_value_tv)!!.text = (data.booking_id)
        mAlertDialog!!.findViewById<TextView>(R.id.pay_amount_value_tv)!!.text = (Constant.currency
                + data.payment!!.payable)
        mAlertDialog!!.findViewById<TextView>(R.id.taxfare_value_tv)!!.text = (Constant.currency
                + data.payment.tax)

        rlWallet?.visibility = if (data.payment.wallet ?: 0.0 > 0.0) View.VISIBLE else View.GONE
        tvWalletDeduction?.text = (Constant.currency + data.payment.wallet)

        rlPromoCode?.visibility = if (data.payment.discount ?: 0.0 > 0.0) View.VISIBLE else View.GONE
        tvPromoCodeDeduction?.text = (getString(R.string.promocode_applied)+ " (-${Constant.currency}${data.payment.discount})")

        rlExtraCharges?.visibility = if (data.payment.extra_charges ?: 0.0 > 0.0) View.VISIBLE else View.GONE
        tvExtraCharges?.text = (Constant.currency + data.payment.extra_charges)

        mAlertDialog!!.findViewById<TextView>(R.id.total_value_tv)!!.text = (Constant.currency
                + data.payment.total)
        val btConfirmPayment:Button = mAlertDialog!!.findViewById<Button>(R.id.confrimpayment)!!
        btConfirmPayment.setOnClickListener {
            confirmPayment(data)
        }


        if (data.payment_mode.equals("card", true)) {
//            invoiceAlertDialog.findViewById<TextView>(R.id.xuber_tips_value_tv)!!.text = (Constant.currency
//                    + data.payment.tips)
            mAlertDialog!!.findViewById<TextView>(R.id.xuber_tips_value_tv)!!.visibility = View.VISIBLE

        }

        mAlertDialog!!.findViewById<TextView>(R.id.xuber_tips_value_tv)!!.setOnClickListener {
            showTipsAlert()
        }



        if (data.after_image == null && data.before_image == null) {
            mAlertDialog!!.findViewById<LinearLayout>(R.id.image_layout)!!.visibility = View.GONE
        }

        if (data.after_image != null) {
            Glide.with(this)
                    .load(data.after_image)
                    .placeholder(R.drawable.image_placeholder)
                    .into(mAlertDialog!!.findViewById<CircleImageView>(R.id.after_img)!!)
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile_place_holder)
                    .placeholder(R.drawable.image_placeholder)
                    .into(mAlertDialog!!.findViewById<CircleImageView>(R.id.after_img)!!)
        }

        if (data.before_image != null) {
            Glide.with(this)
                    .load(data.before_image)
                    .placeholder(R.drawable.image_placeholder)
                    .into(mAlertDialog!!.findViewById<CircleImageView>(R.id.before_img)!!)
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile_place_holder)
                    .placeholder(R.drawable.image_placeholder)
                    .into(mAlertDialog!!.findViewById<CircleImageView>(R.id.before_img)!!)
        }

        //  }


        val paymentTv = mAlertDialog!!.findViewById<TextView>(R.id.payment_type_tv)
        val changePayment = mAlertDialog!!.findViewById<TextView>(R.id.payment_change_type_tv)
        if (data.paid?:0 == 0){
            changePayment?.visibility = View.VISIBLE
        }else{
            changePayment?.visibility = View.GONE
        }
        changePayment?.setOnClickListener {
            val intent = Intent(this@ServiceFlowActivity, ActivityCardList::class.java)
            intent.putExtra("activity_result_flag", "1")
            intent.putExtra("payment_type", data.payment_mode)
            startActivityForResult(intent, Constant.PAYMENT_TYPE_REQUEST_CODE)

        }
        mServiceFlowViewModel.paymentType.observe(this@ServiceFlowActivity, Observer {
            if (it != null) {
                paymentTv!!.text = it.toString()
            }
        })
        mServiceFlowViewModel.paymentChangeSuccessResponse.observe(this@ServiceFlowActivity, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(this, it.message, true)
                    mServiceFlowViewModel.currentStatus.value = ""
                    mServiceFlowViewModel.getCheckRequest()
                }
            }
        })
        mServiceFlowViewModel.paymentResponse.observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(this, it.message, true)
                    if(!isRatingDialogShown)
                        showRatingAlertDialog(data)
                    mAlertDialog!!.dismiss()
                }
            }
        })
        mServiceFlowViewModel.errorResponse.observe(this@ServiceFlowActivity, Observer {
            ViewUtils.showToast(this, it, false)
        })
        if (data.payment_mode.toString().equals("card", true)) {
//            ivPaymentType.setImageDrawable(getDrawable(R.drawable.ic_xuber_money))
            if(data.paid?:0 == 0){
                btConfirmPayment.text = getString(R.string.confirm_payment)
            }else{
                btConfirmPayment.text = getString(R.string.done)
            }
            paymentTv!!.text = getString(R.string.card)
            val img = getDrawable(R.drawable.ic_xuber_credit_card)
            img?.setBounds(60, 0, 60, 60)
            paymentTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)
        } else {
            btConfirmPayment.text = getString(R.string.done)
            paymentTv!!.text = getString(R.string.cash)
            val img = getDrawable(R.drawable.ic_xuber_money)
            img?.setBounds(60, 0, 60, 60)
            paymentTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

        }
        mAlertDialog!!.setCancelable(false)
        mAlertDialog!!.show()

    }

    private fun confirmPayment(data: ServiceCheckReqModel.ResponseData.Data) {
        if (data.payment_mode.equals("card", true) || mPaymentMode != "") {

            if ((mServiceFlowViewModel.checkRequestResponse.value!!.responseData?.data?.get(0)?.paid ?: 0) == 1) {
                if(!isRatingDialogShown)
                    showRatingAlertDialog(data)
                mAlertDialog!!.dismiss()
            }else{
                val reqTips = ReqTips()
                reqTips.requestId = data?.id
                if (mCardId != null) {
                    reqTips.cardId = mCardId
                }
                mServiceFlowViewModel.setCardPayment(reqTips)
            }
        }else {
            if ((mServiceFlowViewModel.checkRequestResponse.value!!.responseData?.data?.get(0)?.paid ?: 0) == 1) {
                if(!isRatingDialogShown)
                    showRatingAlertDialog(data)
                mAlertDialog!!.dismiss()
            }else{
                ViewUtils.showToast(this,getString(R.string.waiting_for_payment_confirmation),false)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        checkRequestTimer?.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                Constant.PAYMENT_TYPE_REQUEST_CODE -> {
                    val payment_type:String = data?.extras?.get("payment_type").toString()
                    if (!payment_type!!.equals(Constant.PaymentMode.CASH,true)) {
                        setPayment(data)
                    }
                }
            }
        }
    }

    private fun setPayment(data: Intent?) {
        isInvoiceDialogShown = false
        mAlertDialog?.isShowing?.let {
            mAlertDialog!!.hide()
        }
        val payment_type = data!!.extras?.get("payment_type")
        val cardID = data?.extras?.get("card_id").toString()

        val reqPaymentUpdateModel = ReqPaymentUpdateModel()
        reqPaymentUpdateModel.requestId = mRequestId
        reqPaymentUpdateModel.paymentMode = Constant.PaymentMode.CARD.toUpperCase()
        reqPaymentUpdateModel.cardId = cardID
        mCardId = cardID
        mServiceFlowViewModel.paymentType.value = Constant.PaymentMode.CARD.toUpperCase()
        mPaymentMode = Constant.PaymentMode.CARD.toUpperCase()
        mServiceFlowViewModel.changePayment(reqPaymentUpdateModel)
    }

    private fun cancelRequest() {

        ViewUtils.showAlert(this, R.string.cancel_request_confirm_msg, object : ViewCallBack.Alert {
            override fun onPositiveButtonClick(dialog: DialogInterface) {

                val requestCancelModel = RequestCancelModel()
                requestCancelModel.requestId = mRequestId
                mServiceFlowViewModel.cancelRideRequest(requestCancelModel)

                mServiceFlowViewModel.successResponse.observe(this@ServiceFlowActivity, Observer {
                    if (it != null) {
                        if (it.statusCode == "200") {
                            dialog.dismiss()
                            ViewUtils.showToast(this@ServiceFlowActivity, it.message, true)
                            closeServiceFlow()
                        }
                    }
                })

            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {
                dialog.dismiss()
            }
        })

    }


    private fun showTipsAlert() {

        val invoiceDialogView = LayoutInflater.from(this).inflate(R.layout.xuber_tips_fragment,
                null, false)

        val builder = AlertDialog.Builder(this@ServiceFlowActivity)
        builder.setView(invoiceDialogView)

        //finally creating the alert dialog and displaying it
        val alertDialog = builder.create()
        alertDialog.show()
        tvAddTips.setOnClickListener {
            ViewUtils.showToast(this@ServiceFlowActivity, "Added sucessfully", true)
            alertDialog.dismiss()
        }

    }


    private fun initialiseMenus() {
/*        mServiceFlowActivityBinding.fabTaxiMenu.isIconAnimated = false
        mServiceFlowActivityBinding.fabTaxiMenu.setOnMenuToggleListener { opened ->
            run {
                if (opened)
                    mServiceFlowActivityBinding.fabTaxiMenu.menuIconView.setImageResource(R.drawable.ic_taxi_close)
                else
                    mServiceFlowActivityBinding.fabTaxiMenu.menuIconView.setImageResource(R.drawable.ic_taxi_three_dots_more_indicator)
            }
        }*/
    }

    private fun fireBaseLocationListener(id: Int) {
        val myRef = FirebaseDatabase.getInstance("https://watrina-a96bb-default-rtdb.firebaseio.com/").getReference("providerId$id")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("RRR :: dataSnapshot = $dataSnapshot")
                val value = dataSnapshot.getValue(LocationPointsEntity::class.java)

                if (value != null) {
                    mServiceFlowViewModel.latitude.value = value.lat
                    mServiceFlowViewModel.longitude.value = value.lng

                    if (startLatLng.latitude != 0.0) endLatLng = startLatLng
                    startLatLng = LatLng(value.lat, value.lng)

                    if (endLatLng.latitude != 0.0 && polyLine.isNotEmpty()) try {
                        CarMarkerAnimUtil().carAnim(srcMarker!!, endLatLng, startLatLng)
                        polyLineRerouting(endLatLng, polyLine)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("RRR :: error = $error")

            }
        })
    }

    private fun polyLineRerouting(point: LatLng, polyLine: ArrayList<LatLng>) {
        println("----->     RRR TaxiDashBoardActivity.polyLineRerouting     <-----")
        println("RRR containsLocation = " + polyUtil.containsLocation(point, polyLine, true))
        println("RRR isLocationOnEdge = " + polyUtil.isLocationOnEdge(point, polyLine, true, 50.0))
        println("RRR locationIndexOnPath = " + polyUtil.locationIndexOnPath(point, polyLine, true, 50.0))
        println("RRR locationIndexOnEdgeOrPath = " + polyUtil.locationIndexOnEdgeOrPath(point, polyLine, false, true, 50.0))

        val index = polyUtil.locationIndexOnEdgeOrPath(point, polyLine, false, true, 50.0)
        if (index >= 0) {
            polyLine.subList(0, index + 1).clear()
//            polyLine.add(0, point)
            mPolyline!!.remove()
            val options = PolylineOptions()
            options.addAll(polyLine)
            mPolyline = mGoogleMap!!.addPolyline(options.width(5f).color
            (ContextCompat.getColor(baseContext, R.color.colorXuberPrimary)))
            println("RRR mPolyline = " + polyLine.size)
        } else {
            canDrawPolyLine = true
            drawRoute(LatLng(mServiceFlowViewModel.latitude.value!!, mServiceFlowViewModel.longitude.value!!),
                    mServiceFlowViewModel.polyLineDest.value!!)
        }
    }

    private fun drawRoute(src: LatLng, dest: LatLng) {

        if (canDrawPolyLine) {
            canDrawPolyLine = false
            Handler().postDelayed({ canDrawPolyLine = true }, 20000)
            PolylineUtil(this).execute(DirectionUtils().getDirectionsUrl(src, dest,
                    BaseApplication.getCustomPreference!!.getString(PreferenceKey.GOOGLE_API_KEY, "")))
        }
        mServiceFlowViewModel.polyLineSrc.value = src
        mServiceFlowViewModel.polyLineDest.value = dest
    }

    override fun whenDone(output: PolylineOptions) {
        mGoogleMap!!.clear()

        mPolyline = mGoogleMap!!.addPolyline(output.width(5f)
                .color(ContextCompat.getColor(baseContext, R.color.colorXuberPrimary))
        )
        polyLine = output.points as ArrayList<LatLng>

        val builder = LatLngBounds.Builder()

        for (latLng in polyLine) builder.include(latLng)

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        try {
            srcMarker = mGoogleMap!!.addMarker(MarkerOptions().position(polyLine[0])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, R.drawable.ic_marker_xuber))))

            CarMarkerAnimUtil().carAnim(srcMarker!!, polyLine[0], polyLine[1])

            mGoogleMap!!.addMarker(MarkerOptions().position(polyLine[polyLine.size - 1])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, R.drawable.ic_marker_stop))))
        } catch (e: Exception) {
            print(e.localizedMessage)
        }
    }

    override fun whenFail(statusCode: String) {
        println("RRR whenFail = $statusCode")
        when (statusCode) {
            //   "NOT_FOUND" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            //  "ZERO_RESULTS" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            "MAX_WAYPOINTS_EXCEEDED" -> Log.d("", getString(R.string.WayPointLlimitExceeded))
            "MAX_ROUTE_LENGTH_EXCEEDED" -> Log.d("", getString(R.string.RoadMapLimitExceeded))
            "INVALID_REQUEST" -> Log.d("", getString(R.string.InvalidInputs))
            "OVER_DAILY_LIMIT" -> Log.d("", getString(R.string.MayBeInvalidAPIBillingPendingMethodDeprecated))
            "OVER_QUERY_LIMIT" -> Log.d("", getString(R.string.TooManyRequestlimitExceeded))
            "REQUEST_DENIED" -> Log.d("", getString(R.string.DirectionsServiceNotEnabled))
            "UNKNOWN_ERROR" -> Log.d("", getString(R.string.ServerError))
            else -> Log.d("", statusCode)
        }
    }

    private fun bitmapFromVector(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun initialiseMap() {
        fragmentMap = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        fragmentMap.getMapAsync(this)
    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        try {
            mGoogleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        this.mGoogleMap = mGoogleMap
        this.mGoogleMap?.setOnCameraMoveListener(this)
        this.mGoogleMap?.setOnCameraIdleListener(this)

        updateLocationUIWithPermissionCheck()
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun updateLocationUI() {

        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false

        LocationUtils.getLastKnownLocation(this, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
                mLastKnownLocation = location
                updateMapLocation(LatLng(location.latitude, location.longitude))
                val address = LocationUtils.getCurrentAddress(baseContext, LatLng(location.latitude, location.longitude))
                if (address.size > 0) {
                    mServiceFlowViewModel.setAddress(address[0].getAddressLine(0))

                }
            }

            override fun onFailure(messsage: String?) {
                updateMapLocation(DEFAULT_LOCATION)
            }
        })
    }

    fun updateMapLocation(location: LatLng, isAnimateMap: Boolean = false) {
        if (!isAnimateMap) {
            mGoogleMap?.moveCamera(newLatLngZoom(location, DEFAULT_ZOOM))
        } else {
            mGoogleMap?.animateCamera(newLatLngZoom(location, DEFAULT_ZOOM))
        }
    }


    override fun moveStatusFlow() {
        /*when (mFlowStatus) {
            "STATUS_FLOW" -> {
                mServiceFlowViewModel.setDriverStatus("Driver has arrived your location")
                mFlowStatus = "ARRIVED"
            }

            "ARRIVED" -> {
                mServiceFlowViewModel.setDriverStatus("You are on Ride")
                mFlowStatus = "STARTED"
                mServiceFlowViewModel.setRide(false)
            }

            "STARTED" -> {
                mServiceFlowActivityBinding.llStatusFlowTop.visibility = View.GONE
                mServiceFlowActivityBinding.llStatusFlowBottom.visibility = View.GONE

            }
            "INVOICE" -> {
                val mInvoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
                replaceFragment(R.id.main_container, mInvoiceFragment, mInvoiceFragment.tag, false)
            }


        }*/
    }

    override fun goBack() {
        onBackPressed()
        finish()
    }

    override fun showCurrentLocation() {
        if (mLastKnownLocation != null) updateMapLocation(LatLng(mLastKnownLocation!!.latitude,
                mLastKnownLocation!!.longitude), true)
    }


    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(this, R.string.location_permission_denied, false)
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onShowRationale(request: PermissionRequest) {
        ViewUtils.showRationaleAlert(this, R.string.location_permission_rationale, request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCameraMove() {
        if (sheetBehavior.state == STATE_EXPANDED) {
            sheetBehavior.state = STATE_COLLAPSED
        }
    }


    override fun onCameraIdle() {

        if (srcLatLng != null) {
            /*          if (mServiceFlowActivityBinding.contentMain.iv_location.visibility == View.VISIBLE) {
                          val address =
                                  LocationUtils.getCurrentAddress(applicationContext, mGoogleMap?.cameraPosition!!.target)
                          if (address.size > 0) {
                              mServiceFlowViewModel.setAddress(address[0].getAddressLine(0))

                              srcLatLng = mGoogleMap?.cameraPosition!!.target
                              Handler().postDelayed({
                                  if (mLocalFlag.equals("STARTED")) {
                                      sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                  } else {
                                      sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                  }
                              }, 400)
                          }
                      }*/
        }

    }


    override fun onBackPressed() {
        /*if (supportFragmentManager.backStackEntryCount > 1) run { supportFragmentManager.popBackStack() }
        else finish()*/
        finish()
    }


    override fun onChronometerTick(chronometer: Chronometer?) {
        val time = Date().time - chronometer!!.base
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - (h * 3600000).toLong() - (m * 60000).toLong()).toInt() / 1000
        val formatedTime = (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
        Log.e("Chrono", "---" + "---" + h + "---" + m + "---" + s)
        chronometer.text = formatedTime


    }

    private fun startTheTimer(started_at: String?) {

        if (!started_at.isNullOrEmpty()) {

            localServiceTime = getLocalTime(started_at, "yyyy-dd-MM HH:mm:ss")
            // val timeinMilliSec = localServiceTime
            /*val timeinMilli = Date().time - (localServiceTime!!)
            val h = (timeinMilli / 3600000).toInt()
            val m = (timeinMilli - h * 3600000).toInt() / 60000
            val s = (timeinMilli - (h * 3600000).toLong() - (m * 60000).toLong()).toInt() / 1000
            val formatedTime = (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s*/

            cmXuberServiceTime.base = SystemClock.elapsedRealtime() - localServiceTime!!
            cmXuberServiceTime.start()
            cmXuberServiceTime.base = localServiceTime!!
            cmXuberServiceTime.text = getString(R.string.start_timer)
            cmXuberServiceTime.start()
            isTimerRunning = true
            cmXuberServiceTime.onChronometerTickListener = this@ServiceFlowActivity

        } else {
            cmXuberServiceTime.base = SystemClock.elapsedRealtime()
            cmXuberServiceTime.text = getString(R.string.start_timer)
            cmXuberServiceTime.start()
        }
    }


}