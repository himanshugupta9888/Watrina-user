package com.gox.taximodule.ui.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.*
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.firebase.database.*
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.basemodule.common.chatmessage.ChatActivity
import com.gox.basemodule.data.*
import com.gox.basemodule.data.Constant.MapConfig.DEFAULT_LOCATION
import com.gox.basemodule.data.Constant.ModuleTypes.TRANSPORT
import com.gox.basemodule.socket.SocketListener
import com.gox.basemodule.socket.SocketManager
import com.gox.basemodule.utils.*
import com.gox.basemodule.utils.polyline.DirectionUtils
import com.gox.basemodule.utils.polyline.PolyLineListener
import com.gox.basemodule.utils.polyline.PolylineUtil
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.data.dto.ProviderModel
import com.gox.taximodule.data.dto.ServiceModel
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.data.dto.request.ReqRatingModel
import com.gox.taximodule.data.dto.response.ResCheckRequest
import com.gox.taximodule.databinding.TaxiActivityMainBinding
import com.gox.taximodule.ui.activity.locationpick.LocationPickActivity
import com.gox.taximodule.ui.adapter.ServiceTypesAdapter
import com.gox.taximodule.ui.communication.CommunicationListener
import com.gox.taximodule.ui.fragment.confirmpage.ConfirmPageFragment
import com.gox.taximodule.ui.fragment.invoice.InvoiceFragment
import com.gox.taximodule.ui.fragment.ratecard.RateCardFragment
import com.gox.taximodule.ui.fragment.rating.RatingFragment
import com.gox.taximodule.ui.fragment.reason.ReasonFragment
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.taxi_activity_main.*
import org.json.JSONObject
import permissions.dispatcher.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


@RuntimePermissions
class TaxiMainActivity : BaseActivity<TaxiActivityMainBinding>(),
        TaxiMainNavigator,
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        CommunicationListener,
        PolyLineListener {

    var isRateCardShown: Boolean = false
    var isSerViceCalled: Boolean = false
    private lateinit var mTaxiActivityMainBinding: TaxiActivityMainBinding
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private lateinit var fragmentMap: SupportMapFragment
    private lateinit var sheetBehavior: BottomSheetBehavior<FrameLayout>
    private var checkRequestTimer: Timer? = null
    private var mGoogleMap: GoogleMap? = null
    private var mLastKnownLocation: Location? = null
    private var mPolyline: Polyline? = null
    private var polyLine = ArrayList<LatLng>()
    private var srcLatLng: LatLng? = null
    private var destLatLng: LatLng? = null
    private var mServiceID: Int? = null
    private var mProviderId: Int? = null
    private var mRatingValue: Int? = null
    private var mComment: String? = null
    private var mAdminServiceId: Int? = null
    private var mProviderList = ArrayList<ProviderModel>()
    private var mRequestId: Int? = null
    private var userId: Int? = null
    private var providerId: Int? = null
    private var userName: String? = null
    private var providerName: String? = null
    private var mLocationPickFlag: Int = 0      //  0 for source and destination  // for extending trip StatusCode
    private var mIsShown: Boolean = false
    private var mRatingShown: Boolean = false
    private var isFireBaseInitialized: Boolean = true
    private var mServiceList: ArrayList<ServiceModel>? = null
    private var startLatLng = LatLng(0.0, 0.0)
    private var endLatLng = LatLng(0.0, 0.0)
    private var srcMarker: Marker? = null
    private var destMarker: Marker? = null
    private var polyUtil = PolyUtil()
    private var canDrawPolyLine: Boolean = true
    private var mShowService: Boolean = true
    private var mRoomConnected: Boolean = false
    private var reqId: Int = 0
    private var otpVerifyFlag: String? = null
    var enabled: Boolean? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private var polylineKey = ""
    private var isGust: Boolean? = false
    private var firebaseDatabase: DatabaseReference? = null

    private lateinit var mPlacesAutocompleteUtil: PlacesAutocompleteUtil
    override fun getLayoutId(): Int = R.layout.taxi_activity_main

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mTaxiActivityMainBinding = mViewDataBinding as TaxiActivityMainBinding
        mTaxiMainViewModel = ViewModelProviders.of(this).get(TaxiMainViewModel::class.java)
        mTaxiMainViewModel.navigator = this
        // mMenuId = intent.getIntExtra("menuId", 0)
        mServiceID = intent.getIntExtra("serviceId", 0)
        mTaxiActivityMainBinding.viewModel = mTaxiMainViewModel
        mTaxiMainViewModel.setRideTypeId(mServiceID!!)
        mTaxiActivityMainBinding.executePendingBindings()
        polylineKey = getString(R.string.google_map_key)
        mTaxiMainViewModel.currentStatus.value = ""
        checkRequestTimer = Timer()
        mTaxiMainViewModel.paymentType.set("CASH")
        sheetBehavior = from<FrameLayout>(container)
        mPlacesAutocompleteUtil = PlacesAutocompleteUtil(applicationContext)
        otpVerifyFlag = BaseApplication.getCustomPreference!!.getString(PreferenceKey.RIDE_OTP, "")!!
        mServiceList = ArrayList()

        mTaxiMainViewModel.loadingProgress.observe(this, Observer {
            loadingObservable.value = it
        })

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        enabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)


        SocketManager.onEvent(Constant.ROOM_NAME.STATUS, Emitter.Listener {
            if (it[0].toString().contains(TRANSPORT)) {
                mRoomConnected = true
            }
        })

        SocketManager.onEvent(Constant.ROOM_NAME.RIDE_REQ, Emitter.Listener {
            // checkRequestTimer!!.cancel()
            mTaxiMainViewModel.getCheckRequest()
        })

        SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION, Emitter.Listener {
            Log.e("SOCKET", "SOCKET_SK location received" + it[0])
            updateRouteViaSocket(it)

        })


        SocketManager.setOnSocketRefreshListener(object : SocketListener.connectionRefreshCallBack {
            override fun onRefresh() {
                SocketManager.emit(
                    Constant.ROOM_NAME.TRANSPORT_ROOM_NAME, Constant.ROOM_ID.getTransportRoom(
                        reqId
                    )
                )

                SocketManager.onEvent(Constant.ROOM_NAME.RIDE_REQ, Emitter.Listener {
                    // checkRequestTimer!!.cancel()
                    mTaxiMainViewModel.getCheckRequest()
                })


                SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION, Emitter.Listener {
                    updateRouteViaSocket(it)

                })
            }
        })

        isGust = preference.getValue(PreferenceKey.ISGUST, false) as Boolean

        mTaxiActivityMainBinding.fabTaxiMenuChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        mTaxiMainViewModel.getReason(Constant.ModuleTypes.TRANSPORT)

        if (BuildConfig.ENABLE_TIMER_CALL) {
            checkRequestTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mTaxiMainViewModel.getCheckRequest()
                }
            }, 0, 5000)
        } else mTaxiMainViewModel.getCheckRequest()

        mTaxiMainViewModel.getCheckRequestResponse().observe(this, Observer {
            mTaxiMainViewModel.loadingProgress.value = false
            if (it.statusCode == "200")
                if (it.responseData?.data != null && it.responseData?.data!!.isNotEmpty()) {
                    mShowService = false
                    println("RRR :: currentStatus.value = ${mTaxiMainViewModel.currentStatus.value}")
                    println("RRR :: responseData :: Status = ${it.responseData.data[0]!!.status}")
                    mTaxiActivityMainBinding.bottomSheetLayout.visibility = View.GONE

                    if (!mRoomConnected) {
                        reqId = it.responseData.data[0]?.id ?: 0
                        PreferenceHelper(BaseApplication.baseApplication).setValue(
                            PreferenceKey.TRANSPORT_REQ_ID,
                            reqId
                        )
                        if (reqId != 0) {
                            SocketManager.emit(
                                Constant.ROOM_NAME.TRANSPORT_ROOM_NAME,
                                Constant.ROOM_ID.getTransportRoom(
                                    reqId
                                )
                            )
                        }

                    }

                    if (mTaxiMainViewModel.currentStatus.value != it.responseData.data[0]!!.status) {
                        storeChatData(it)
                        when (mTaxiMainViewModel.currentStatus.value) {
                            getString(R.string.searching) -> {
                                stateWhenSearch()
                            }
                            getString(R.string.started) -> {
                                stateWhenStarted(it)
                            }
                            "ARRIVED" -> {
                                stateWhenArrived(it)
                            }
                            "PICKEDUP" -> {
                                stateWhenPickedUp(it)
                            }
                            "DROPPED" -> {
                                stateWhenDropped(it)
                            }
                            "COMPLETED" -> {
                                stateWhenCompleted(it)
                            }
                        }
                    }
                } else {
                    // Show Service List Layout
                    showServiceList()
                }
        })

        MapsInitializer.initialize(this@TaxiMainActivity)
        initialiseMap()
        initialiseMenus()

        mTaxiMainViewModel.getRequestId().observe(this, Observer {
            mRequestId = it
        })
        mTaxiActivityMainBinding.tvRatingSubmit.setOnClickListener {
            val reqRatingModel = ReqRatingModel()
            reqRatingModel.requesterId = mProviderId.toString()
            reqRatingModel.adminService = mAdminServiceId.toString()
            mRatingValue = mTaxiActivityMainBinding.rvUser.rating.toInt()
            mComment = mTaxiActivityMainBinding.commentEt.text.toString()
            reqRatingModel.rating = mRatingValue
            reqRatingModel.comment = mComment
            mTaxiMainViewModel.setRating(reqRatingModel)
        }
        mTaxiActivityMainBinding.btGetPricing.setOnClickListener {
            if (mTaxiMainViewModel.sourceLat.get() == "" || mTaxiMainViewModel.sourceLon.get() == "") {
                ViewUtils.showNormalToast(this, getString(R.string.Select_the_Source_place))
                return@setOnClickListener
            }
            if (mTaxiMainViewModel.destinationLat.get() == "" || mTaxiMainViewModel.destinationLon.get() == "") {
                ViewUtils.showNormalToast(this, getString(R.string.Select_the_destination_place))
                return@setOnClickListener
            }
            if (mTaxiMainViewModel.serviceType.get().equals("")) {
//                ViewUtils.showNormalToast(this, getString(R.string.SelectTheServiceType))
                return@setOnClickListener
            } else {
                /*   val confirmPageFragment: ConfirmPageFragment = ConfirmPageFragment.newInstance()
                   replaceFragment(R.id.main_container, confirmPageFragment, confirmPageFragment.tag!!, true)*/

                val confirmPageFragment: ConfirmPageFragment = ConfirmPageFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_container, confirmPageFragment, confirmPageFragment.tag
                )
                        .addToBackStack(confirmPageFragment.tag).commit()
            }
        }

        mTaxiActivityMainBinding.btnChange.setOnClickListener {
            val intent = Intent(this@TaxiMainActivity, ActivityCardList::class.java)
            intent.putExtra("activity_result_flag", "1")
            startActivityForResult(intent, Constant.PAYMENT_TYPE_REQUEST_CODE)
        }
        mTaxiActivityMainBinding.btsearchCancelRequest.setOnClickListener {
            ViewUtils.showTransportAlert(
                this,
                R.string.cancel_request_confirm_msg,
                object : ViewCallBack.Alert {
                    override fun onPositiveButtonClick(dialog: DialogInterface) {
                        val reqEstimateModel = ReqEstimateModel()
                        reqEstimateModel.id = mRequestId
                        mTaxiMainViewModel.cancelRideRequest(reqEstimateModel)
                    }

                    override fun onNegativeButtonClick(dialog: DialogInterface) {
                        dialog.dismiss()
                    }
                })
        }
        mTaxiMainViewModel.getCancelResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mTaxiActivityMainBinding.searchView.visibility = View.GONE
                mShowService = true
                ViewUtils.showToast(this, it.message, true)
                mTaxiMainViewModel.destinationaddressvalue.set(" ")
                finish()
            }
        })
        mTaxiMainViewModel.getShowFlag().observe(this, Observer {
            if (it != null) if (it == "1")
                if (sheetBehavior.state == STATE_COLLAPSED) sheetBehavior.state = STATE_EXPANDED
                else sheetBehavior.state = STATE_HIDDEN
        })

        mTaxiActivityMainBinding.editDestinationTrip.setOnClickListener {
            ViewUtils.showAlert(this, R.string.extend_trip_msg, object : ViewCallBack.Alert {
                override fun onPositiveButtonClick(dialog: DialogInterface) {

                    mLocationPickFlag = mRequestId!!
                    val intent = Intent(this@TaxiMainActivity, LocationPickActivity::class.java)
                    intent.putExtra("LocationPickFlag", mLocationPickFlag)
                    startActivityForResult(intent, LocationPickActivity.EDIT_LOCATION_REQUEST_CODE)
                    dialog.dismiss()
                }

                override fun onNegativeButtonClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }

            })
        }

        backStackChangeListener()
    }


    fun showServiceList() {
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.VISIBLE
        mTaxiActivityMainBinding.statusCardView.visibility = View.GONE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.GONE
        if (!isRateCardShown)
            mTaxiActivityMainBinding.bottomSheetLayout.visibility = View.VISIBLE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true

        if (!isSerViceCalled) {
            mTaxiMainViewModel.getServicesResponse().observe(this, Observer {
                if (it != null) {
                    if (it.statusCode == "200") {
                        isSerViceCalled = true
                        mProviderList.clear()
                        mProviderList.addAll(it.serviceData?.providersList!!)
                        val builder = LatLngBounds.Builder()
                        mServiceList!!.clear()
                        mServiceList!!.addAll(it.serviceData?.serviceList!!)
                        mTaxiActivityMainBinding.serviceAdapter = ServiceTypesAdapter(
                            this, mServiceList!!
                        )
                        mTaxiActivityMainBinding.serviceAdapter!!.notifyDataSetChanged()
                        mTaxiActivityMainBinding.serviceAdapter!!.setOnClickListener(
                            mOnAdapterClickListener
                        )
                        for (i in 0 until mProviderList.size) {
                            val latLngDestination = LatLng(
                                mProviderList[i].latitude!!,
                                mProviderList[i].longitude!!
                            )
                            builder.include(latLngDestination)
                            val markerView =
                                (this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                                    .inflate(R.layout.marker_custom, null)
                            val picture = markerView.findViewById(R.id.picture) as ImageView

                            Glide.with(this).load(R.drawable.ic_marker_car)
                                .apply(RequestOptions().dontAnimate())
                                .into(picture)

                            mGoogleMap!!.addMarker(
                                MarkerOptions().position(latLngDestination)
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap
                                            (
                                            createDrawableFromView(
                                                this@TaxiMainActivity,
                                                markerView
                                            )
                                        )
                                    )
                            )
                        }
                        //     mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
                    }
                }
            })
        }
    }

    private fun updateRouteViaSocket(it: Array<Any>) {
        runOnUiThread {
            val data1 = it[0] as JSONObject
            mTaxiMainViewModel.latitude.value = data1.getString("lat").toDouble()
            mTaxiMainViewModel.longitude.value = data1.getString("lng").toDouble()

            if (startLatLng.latitude != 0.0) endLatLng = startLatLng
            startLatLng = LatLng(
                data1.getString("lat").toDouble(),
                data1.getString("lng").toDouble()
            )

            if (endLatLng.latitude != 0.0 && polyLine.size > 0) {
                try {
                    CarMarkerAnimUtil().carAnimWithBearing(srcMarker!!, endLatLng, startLatLng)
                    polyLineRerouting(endLatLng, polyLine)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (polyLine == null || polyLine.size < 1)
                if (mTaxiMainViewModel.tempSrc.value != null)
                    drawRoute(
                        mTaxiMainViewModel.tempSrc.value!!,
                        mTaxiMainViewModel.tempDest.value!!
                    )

        }
    }

    private fun storeChatData(it: ResCheckRequest?) {

        val response = it?.responseData!!.data?.get(0)!!
        mTaxiMainViewModel.currentStatus.value = response.status
        mRequestId = response.id
        userId = response.user_id
        userName = response.user?.first_name + " " + response.user?.last_name
        providerName = response.provider?.first_name + " " + response.provider?.last_name
        providerId = response.provider_id
        mAdminServiceId = response.service_type?.admin_service_id
        //mTaxiMainViewModel.providerName.value = response.provider?.first_name + " " + response.provider?.last_name
        mTaxiMainViewModel.providerName.set(response.provider?.first_name + " " + response.provider?.last_name)


        mTaxiMainViewModel.setRequestId(mRequestId!!)
        preference.setValue(Constant.Chat.ADMIN_SERVICE, TRANSPORT)
        preference.setValue(Constant.Chat.USER_ID, userId)
        preference.setValue(Constant.Chat.REQUEST_ID, mRequestId)
        preference.setValue(Constant.Chat.PROVIDER_ID, providerId)
        preference.setValue(Constant.Chat.USER_NAME, userName)
        preference.setValue(Constant.Chat.PROVIDER_NAME, providerName)
    }

    private fun stateWhenCompleted(it: ResCheckRequest?) {
        mGoogleMap!!.clear()
        val response = it?.responseData!!.data?.get(0)!!
        if (response.paid == null || response.paid == 0) {
            val invoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(
                R.id.main_container, invoiceFragment, invoiceFragment.tag
            )
                    .addToBackStack(invoiceFragment.tag).commit()
            mIsShown = true
        } else {
            if (!mRatingShown) {
                supportFragmentManager.popBackStack()
                val mRatingFragment: RatingFragment = RatingFragment.newInstance()
                mRatingFragment.show(supportFragmentManager, mRatingFragment.tag)
                mRatingShown = true
            }
        }

        /*val invoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.main_container, invoiceFragment
                , invoiceFragment.tag)
                .addToBackStack(invoiceFragment.tag).commit()
        mIsShown = true*/
    }

    private fun stateWhenPickedUp(it: ResCheckRequest?) {
        val response = it?.responseData!!.data?.get(0)!!
        mGoogleMap!!.clear()
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
        mTaxiActivityMainBinding.statusCardView.visibility = View.VISIBLE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.GONE
        mTaxiActivityMainBinding.tvOTP.visibility = View.GONE
        mTaxiActivityMainBinding.fabLocation.visibility = View.GONE
        mTaxiActivityMainBinding.btShare.visibility = View.VISIBLE
        mTaxiActivityMainBinding.fabTaxiMenu.visibility = View.GONE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        mTaxiMainViewModel.setRide(false)
        mTaxiActivityMainBinding.tvSos.visibility = View.VISIBLE
        mTaxiActivityMainBinding.contentMain.iv_location.visibility = View.GONE
        mTaxiActivityMainBinding.tvCurrentDriverStatus.text = " " + getString(R.string.you_are_on_ride)
        mTaxiActivityMainBinding.tvDriverName.text = response.provider?.first_name + " " + response.provider?.last_name
        mTaxiActivityMainBinding.tvTaxiDriverRating.text = "%.2f".format(
            response.provider?.rating
                ?: 0.0
        )
        mTaxiActivityMainBinding.tvVehicleNumber.text = getString(R.string.vehicle_number).plus(
            response.service_type?.vehicle?.vehicle_no.toString()
        )
        mTaxiActivityMainBinding.tvVehicleModel.text = getString(R.string.vehicle_model).plus(
            response.service_type?.vehicle?.vehicle_make + " " + response.service_type?.vehicle?.vehicle_model
        )
        mTaxiActivityMainBinding.tvTaxiServiceType.text = getString(R.string.vehicle_type).plus(
            response.ride?.vehicle_name.toString()
        )
        mTaxiActivityMainBinding.btCancelRequest.visibility = View.GONE
        val srcLatLng = LatLng(response.s_latitude!!.toDouble(), response.s_longitude!!.toDouble())
        val destLatLng = LatLng(response.d_latitude!!.toDouble(), response.d_longitude!!.toDouble())
        drawRoute(srcLatLng, destLatLng)
        val dest = LatLng(response.d_latitude, response.d_longitude)
        val address = LocationUtils.getCurrentAddress(baseContext, dest)
        mTaxiActivityMainBinding.destinationAddress.text = address[0].getAddressLine(0)
        mTaxiActivityMainBinding.editDestinationTrip.visibility = View.GONE
        val sos = it.responseData.sos
        mTaxiActivityMainBinding.tvSos.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + sos)
            startActivity(intent)
        }
        if (response.service_type?.vehicle?.vechile_image.toString() != "null") {
            Glide.with(this)
                    .load(response.service_type?.vehicle?.vechile_image.toString())
                    .into(mTaxiActivityMainBinding.ivVehicleImage)
        } else {
            Glide.with(this)
                    .load(R.drawable.demo_car)
                    .into(mTaxiActivityMainBinding.ivVehicleImage)
        }
        Glide.with(this)
                .load(response.ride?.vehicle_image)
                .placeholder(R.drawable.placeholder_car)
                .into(mTaxiActivityMainBinding.ivVehicleImage)
        if (response.provider?.picture.toString() != "null") {
            Glide.with(this)
                    .load(response.provider?.picture.toString())
                    .into(mTaxiActivityMainBinding.civDriverProfile)
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile_place_holder)
                    .into(mTaxiActivityMainBinding.civDriverProfile)
        }
        val driverName = response.provider!!.first_name + " " + response.provider.last_name
        val vehicleName = response.service_type?.vehicle?.vehicle_make + " " +
                response.service_type?.vehicle?.vehicle_model
        val vehicleNumber = response.service_type?.vehicle?.vehicle_no
        val currentLat = response.track_latitude.toString()
        val currentLng = response.track_longitude.toString()
        val sourceAddress = response.s_address.toString()
        val destAddress = response.d_address.toString()
        val shareContent1 = getString(R.string.shareContent1)
        val shareContent2 = getString(R.string.shareContent2)
        val share_content3 = getString(R.string.share_ride_content3)
        val share_content4 = getString(R.string.share_ride_content4)
        val to = getString(R.string.sharecontentTo)
        val gmap_link = " https://maps.google.com/?q="
        val sb = StringBuilder()
        sb.append(shareContent1).append(driverName).append(shareContent2)
                .append(vehicleName).append(vehicleNumber).append(share_content3)
                .append(gmap_link).append(currentLat).append(",").append(currentLng).append(" ").append(
                share_content4
            )
                .append(sourceAddress).append(to).append(destAddress)
        val share = sb.toString()
        mTaxiActivityMainBinding.btShare.setOnClickListener {

            shareYourRide(share)
        }
        mTaxiActivityMainBinding.tvLocationChange.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.GONE
            mTaxiActivityMainBinding.locationStatus.visibility = View.VISIBLE
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )

        }
        mTaxiActivityMainBinding.tvStatus.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.VISIBLE
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )
            mTaxiActivityMainBinding.locationStatus.visibility = View.GONE
        }
        // to  show the map / with source and destination with marker including route.
    }

    @SuppressLint("SetTextI18n")
    private fun stateWhenStarted(it: ResCheckRequest?) {
        val response = it?.responseData!!.data?.get(0)!!
        mGoogleMap?.clear()
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mTaxiActivityMainBinding.statusCardView.visibility = View.VISIBLE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.GONE
        mTaxiActivityMainBinding.fabLocation.visibility = View.GONE
        mTaxiActivityMainBinding.btShare.visibility = View.GONE
        mTaxiActivityMainBinding.tvSos.visibility = View.GONE
        mTaxiActivityMainBinding.fabTaxiMenu.visibility = View.VISIBLE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        if (isFireBaseInitialized) {
            isFireBaseInitialized = false
            fireBaseLocationListener(response.provider!!.id!!)
        }
        mTaxiActivityMainBinding.tvDriverName.text = response.provider?.first_name + " " + response.provider?.last_name
        mTaxiActivityMainBinding.tvTaxiDriverRating.text = "%.2f".format(
            response.provider?.rating
                ?: 0.0
        )
        mTaxiActivityMainBinding.contentMain.iv_location.visibility = View.GONE
        mTaxiActivityMainBinding.tvVehicleNumber.text = getString(R.string.vehicle_number).plus(
            response.service_type?.vehicle?.vehicle_no.toString()
        )
        mTaxiActivityMainBinding.tvVehicleModel.text = getString(R.string.vehicle_model).plus(
            response.service_type?.vehicle?.vehicle_make + " " + response.service_type?.vehicle?.vehicle_model
        )
        mTaxiActivityMainBinding.tvTaxiServiceType.text = getString(R.string.vehicle_type).plus(
            response.ride?.vehicle_name.toString()
        )

        if (otpVerifyFlag.equals("1")) {
            mTaxiMainViewModel.setRide(true)
        } else {
            mTaxiMainViewModel.setRide(false)
        }

        if (isFireBaseInitialized) {
            isFireBaseInitialized = false
            fireBaseLocationListener(response.provider!!.id!!)
        }

        mTaxiActivityMainBinding.tvOTP.text = getString(R.string.otp) + ":" + response.otp
        mTaxiActivityMainBinding.tvCurrentDriverStatus.text = " " + getString(R.string.driver_has_accepted_your_request)
        Glide.with(this)
                .load(response.ride?.vehicle_image)
                .placeholder(R.drawable.placeholder_car)
                .into(mTaxiActivityMainBinding.ivVehicleImage)
        if (response.provider?.picture.toString() != "null") {
            Glide.with(this)
                    .load(response.provider?.picture.toString())
                    .into(mTaxiActivityMainBinding.civDriverProfile)
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile_place_holder)
                    .into(mTaxiActivityMainBinding.civDriverProfile)
        }
        if (response.peak == 0) {
            mTaxiActivityMainBinding.tvTaxiStatusNotes.visibility = View.GONE
        } else {
            mTaxiActivityMainBinding.tvTaxiStatusNotes.visibility = View.VISIBLE
        }
        val driverLatLng = LatLng(
            response.provider!!.latitude!!.toDouble(),
            response.provider.longitude!!.toDouble()
        )
        val userLatLng = LatLng(
            response.s_latitude!!.toDouble(),
            response.s_longitude!!.toDouble()
        )
        val phoneNumber = response.provider.mobile.toString()

        drawRoute(driverLatLng, userLatLng)

        mTaxiActivityMainBinding.fabTaxiMenuCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
        mTaxiActivityMainBinding.btCancelRequest.setOnClickListener {
            val mReasonFragment = ReasonFragment.newInstance(mRequestId!!)
            mReasonFragment.isCancelable = true
            mReasonFragment.show(supportFragmentManager, mReasonFragment.tag)
        }
        val dest = LatLng(response.d_latitude!!, response.d_longitude!!)
        val address = LocationUtils.getCurrentAddress(baseContext, dest)
        mTaxiActivityMainBinding.destinationAddress.text = address[0].getAddressLine(0)
        mTaxiActivityMainBinding.tvLocationChange.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.GONE
            mTaxiActivityMainBinding.locationStatus.visibility = View.VISIBLE
            mTaxiActivityMainBinding.editDestinationTrip.visibility = View.GONE
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )
            mTaxiActivityMainBinding.destinationAddress.text = address[0].getAddressLine(0)
        }
        mTaxiActivityMainBinding.tvStatus.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.VISIBLE
            mTaxiActivityMainBinding.locationStatus.visibility = View.GONE
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stateWhenArrived(it: ResCheckRequest?) {
        val response = it?.responseData!!.data?.get(0)!!
        mGoogleMap!!.clear()
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
        mTaxiActivityMainBinding.statusCardView.visibility = View.VISIBLE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.GONE
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.VISIBLE
        mTaxiActivityMainBinding.btCancelRequest.visibility = View.VISIBLE
        mTaxiActivityMainBinding.fabLocation.visibility = View.GONE
        mTaxiActivityMainBinding.btShare.visibility = View.GONE
        mTaxiActivityMainBinding.tvSos.visibility = View.GONE
        mTaxiActivityMainBinding.fabTaxiMenu.visibility = View.VISIBLE
        if (otpVerifyFlag.equals("1")) {
            mTaxiMainViewModel.setRide(true)
        } else {
            mTaxiMainViewModel.setRide(false)
        }
        mTaxiActivityMainBinding.btCancelRequest.setOnClickListener {
            val mReasonFragment = ReasonFragment.newInstance(mRequestId!!)
            mReasonFragment.isCancelable = true
            mReasonFragment.show(supportFragmentManager, mReasonFragment.tag)
        }
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        mTaxiActivityMainBinding.contentMain.iv_location.visibility = View.GONE
        val dest = LatLng(response.d_latitude!!, response.d_longitude!!)
        val address = LocationUtils.getCurrentAddress(baseContext, dest)
        mTaxiActivityMainBinding.destinationAddress.text = address[0].getAddressLine(0)
        mTaxiActivityMainBinding.tvLocationChange.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.GONE
            mTaxiActivityMainBinding.locationStatus.visibility = View.VISIBLE
            mTaxiActivityMainBinding.editDestinationTrip.visibility = View.GONE
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )

        }
        mTaxiActivityMainBinding.tvStatus.setOnClickListener {
            mTaxiActivityMainBinding.statusLayout.visibility = View.VISIBLE
            mTaxiActivityMainBinding.locationStatus.visibility = View.GONE
            mTaxiActivityMainBinding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiBlack
                )
            )
            mTaxiActivityMainBinding.tvLocationChange.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorTaxiGrey
                )
            )
        }
        //    mTaxiMainViewModel.setDriverStatus("Driver has arrived your location")
        //   ViewUtils.showNormalToast(this, "" + it.responseData.data[0].status)
        // to  show the map / with source and destination with marker including route.

        mTaxiActivityMainBinding.tvCurrentDriverStatus.text = " " + getString(R.string.driver_has_arrived_your_location)
        val driverLatLng = LatLng(
            response.provider?.latitude!!.toDouble(),
            response.provider.longitude!!.toDouble()
        )
        try {
            drawRoute(driverLatLng, srcLatLng!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mTaxiActivityMainBinding.tvDriverName.text = response.provider.first_name + " " + response.provider.last_name
        mTaxiActivityMainBinding.tvTaxiDriverRating.text = "%.2f".format(
            response.provider?.rating
                ?: 0.0
        )
        mTaxiActivityMainBinding.tvVehicleNumber.text = getString(R.string.vehicle_number).plus(
            response.service_type?.vehicle?.vehicle_no.toString()
        )
        mTaxiActivityMainBinding.tvVehicleModel.text = getString(R.string.vehicle_model).plus(
            response.service_type?.vehicle?.vehicle_make + " " + response.service_type?.vehicle?.vehicle_model
        )
        mTaxiActivityMainBinding.tvTaxiServiceType.text = getString(R.string.vehicle_type).plus(
            response.ride?.vehicle_name.toString()
        )
        // mTaxiMainViewModel.setRide(true)
        mTaxiActivityMainBinding.tvOTP.text = getString(R.string.otp) + ":" + response.otp

        mTaxiActivityMainBinding.btCancelRequest.setOnClickListener {
            val mReasonFragment = ReasonFragment.newInstance(mRequestId!!)
            mReasonFragment.isCancelable = true
            mReasonFragment.show(supportFragmentManager, mReasonFragment.tag)
        }
    }

    private fun stateWhenDropped(it: ResCheckRequest?) {
        val response = it?.responseData!!.data?.get(0)!!
        println("RRRR :: DROPPED")
        mGoogleMap!!.clear()
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
        mTaxiActivityMainBinding.statusCardView.visibility = View.GONE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.GONE
        mTaxiActivityMainBinding.btCancelRequest.visibility = View.GONE
        mTaxiActivityMainBinding.contentMain.iv_location.visibility = View.GONE
        mTaxiActivityMainBinding.tvSos.visibility = View.GONE
        mTaxiActivityMainBinding.btShare.visibility = View.GONE
        mTaxiActivityMainBinding.fabTaxiMenu.visibility = View.GONE
        if (response.user?.payment_mode.equals("CASH")) {
            if (!mIsShown) {
                /*   val invoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
                   replaceFragment(R.id.main_container, invoiceFragment, invoiceFragment.tag!!, false)*/
                val invoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_container, invoiceFragment, invoiceFragment.tag
                )
                        .addToBackStack(invoiceFragment.tag).commit()
                mIsShown = true
            }
        } else if (response.use_wallet == 1 && response.paid == 1) {
            if (!mIsShown) {
                val invoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_container,
                    invoiceFragment,
                    invoiceFragment.tag
                )
                        .addToBackStack(invoiceFragment.tag).commit()
                mIsShown = true
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        checkRequestTimer?.cancel()
    }

    private fun shareYourRide(content: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        share.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
        share.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(share, "Share"))
    }

    private fun fireBaseLocationListener(id: Int) {
        val myRef = FirebaseDatabase.getInstance("https://watrina-a96bb-default-rtdb.firebaseio.com/").getReference("providerId$id")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(LocationPointsEntity::class.java)
                if (value != null) {
                    mTaxiMainViewModel.latitude.value = value.lat
                    mTaxiMainViewModel.longitude.value = value.lng

                    if (startLatLng.latitude != 0.0) endLatLng = startLatLng
                    startLatLng = LatLng(value.lat, value.lng)

                    if (endLatLng.latitude != 0.0 && polyLine.size > 0) {
                        try {
                            CarMarkerAnimUtil().carAnimWithBearing(
                                srcMarker!!,
                                endLatLng,
                                startLatLng
                            )
                            polyLineRerouting(endLatLng, polyLine)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (polyLine == null || polyLine.size < 1)
                        if (mTaxiMainViewModel.tempSrc.value != null)
                            drawRoute(
                                mTaxiMainViewModel.tempSrc.value!!,
                                mTaxiMainViewModel.tempDest.value!!
                            )

                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("RRR :: error = $error")
            }
        })
    }

    private fun stateWhenSearch() {
        mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE
        mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
        mTaxiActivityMainBinding.statusCardView.visibility = View.GONE
        mTaxiActivityMainBinding.ratingView.visibility = View.GONE
        mTaxiActivityMainBinding.searchView.visibility = View.VISIBLE
        mTaxiActivityMainBinding.btShare.visibility = View.GONE
        mGoogleMap!!.uiSettings.isScrollGesturesEnabled = true
        /*   val mSearchFragment: SearchPageFragment = SearchPageFragment.newInstance()
           replaceFragment(R.id.main_container, mSearchFragment, mSearchFragment!!.tag, true)*/
    }

    override fun onResume() {
        super.onResume()
        mTaxiMainViewModel.getCheckRequest()
    }

    private fun polyLineRerouting(point: LatLng, polyLine: ArrayList<LatLng>) {
        println("----->     RRR TaxiDashBoardActivity.polyLineRerouting     <-----")
        println("RRR containsLocation = " + polyUtil.containsLocation(point, polyLine, true))
        println("RRR isLocationOnEdge = " + polyUtil.isLocationOnEdge(point, polyLine, true, 50.0))
        println(
            "RRR locationIndexOnPath = " + polyUtil.locationIndexOnPath(
                point,
                polyLine,
                true,
                50.0
            )
        )
        println(
            "RRR locationIndexOnEdgeOrPath = " + polyUtil.locationIndexOnEdgeOrPath(
                point,
                polyLine,
                false,
                true,
                50.0
            )
        )

        val index = polyUtil.locationIndexOnEdgeOrPath(point, polyLine, false, true, 50.0)
        if (index >= 0) {
            polyLine.subList(0, index + 1).clear()
//            polyLine.add(0, point)
            mPolyline!!.remove()
            val options = PolylineOptions()
            options.addAll(polyLine)
            mPolyline = mGoogleMap!!.addPolyline(
                options.width(5f).color
                    (ContextCompat.getColor(baseContext, R.color.colorTaxiBlack))
            )
            println("RRR mPolyline = " + polyLine.size)

            val size = polyLine.size - 1
            val results = FloatArray(1)
            var sum = 0f

            for (i in 0 until size) {
                Location.distanceBetween(
                    polyLine[i].latitude, polyLine[i].longitude,
                    polyLine[i + 1].latitude, polyLine[i + 1].longitude, results
                )
                sum += results[0]
            }

            println("RRR mPolyline.size = " + polyLine.size)
            println("RRR mPolyline::driverSpeed(m/s) = " + mTaxiMainViewModel.driverSpeed.value)
            println("RRR mPolyline::totalDistance(M) = $sum")

            val seconds = sum / mTaxiMainViewModel.driverSpeed.value!!
            println("RRR mPolyline::totalTime(S) = $seconds")

            val hours = seconds.toInt() / 3600
            var remainder = seconds.toInt() - hours * 3600
            val mins = remainder / 60
            remainder -= mins * 60
            val secs = remainder

            val etaMarker = (this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.marker_eta, null)
            val tvEta = etaMarker.findViewById(R.id.tvEta) as TextView
            tvEta.text = getETAFormat(hours, mins)


            mGoogleMap!!.addMarker(
                MarkerOptions().position(polyLine[polyLine.size - 1])
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            createDrawableFromView(
                                this,
                                etaMarker
                            )
                        )
                    )
            )

        } else {
            canDrawPolyLine = true
            drawRoute(
                LatLng(
                    mTaxiMainViewModel.latitude.value!!,
                    mTaxiMainViewModel.longitude.value!!
                ),
                mTaxiMainViewModel.polyLineDest.value!!
            )
        }
    }

    private fun getETAFormat(hours: Int, mins: Int): String {
        var etaFormat = "ETA\n"
        if (hours > 0) {
            if (hours == 1)
                etaFormat = "${DecimalFormat("00").format(hours)} hr"
            else
                etaFormat = "${DecimalFormat("00").format(hours)} hrs"
        }

        if (mins >= 0) {
            if (mins <= 1)
                etaFormat = etaFormat + " $mins min"
            else
                etaFormat = etaFormat + " ${DecimalFormat("00").format(mins)} mins"
        }
        return etaFormat
    }

    private fun initialiseMenus() {
        mTaxiActivityMainBinding.fabTaxiMenu.isIconAnimated = false
        mTaxiActivityMainBinding.fabTaxiMenu.setOnMenuToggleListener { opened ->
            run {
                if (opened) mTaxiActivityMainBinding.fabTaxiMenu.menuIconView.setImageResource(R.drawable.ic_taxi_close)
                else mTaxiActivityMainBinding.fabTaxiMenu.menuIconView.setImageResource(R.drawable.ic_taxi_three_dots_more_indicator)
            }
        }
    }

    private fun drawRoute(src: LatLng, dest: LatLng) {
        mTaxiMainViewModel.tempSrc.value = src
        mTaxiMainViewModel.tempDest.value = dest

        if (canDrawPolyLine) {
            canDrawPolyLine = false
            android.os.Handler().postDelayed({ canDrawPolyLine = true }, 5000)
        }

        PolylineUtil(this).execute(DirectionUtils().getDirectionsUrl(src, dest, polylineKey))
        mTaxiMainViewModel.polyLineSrc.value = src
        mTaxiMainViewModel.polyLineDest.value = dest
    }

    override fun whenDone(output: PolylineOptions) {
        mGoogleMap!!.clear()

        mPolyline = mGoogleMap!!.addPolyline(
            output.width(5f)
                .color(ContextCompat.getColor(baseContext, R.color.colorTaxiBlack))
        )

        polyLine = output.points as ArrayList<LatLng>

        val builder = LatLngBounds.Builder()

        for (latLng in polyLine) builder.include(latLng)

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))

        try {
            val src = R.drawable.ic_marker_car
            val dest = R.drawable.ic_marker_dest

            srcMarker = mGoogleMap!!.addMarker(
                MarkerOptions().position(polyLine[0])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, src)))
            )

            if (mTaxiMainViewModel.currentStatus.value!!.length > 2) {
                val size = polyLine.size - 1
                val results = FloatArray(1)
                var sum = 0f

                for (i in 0 until size) {
                    Location.distanceBetween(
                        polyLine[i].latitude, polyLine[i].longitude,
                        polyLine[i + 1].latitude, polyLine[i + 1].longitude, results
                    )
                    sum += results[0]
                }

                println("RRR mPolyline.size = " + polyLine.size)
                println("RRR mPolyline::driverSpeed(m/s) = " + mTaxiMainViewModel.driverSpeed.value)
                println("RRR mPolyline::totalDistance(M) = $sum")

                val seconds = sum / mTaxiMainViewModel.driverSpeed.value!!
                println("RRR mPolyline::totalTime(S) = $seconds")

                val hours = seconds.toInt() / 3600
                var remainder = seconds.toInt() - hours * 3600
                val mins = remainder / 60
                remainder -= mins * 60
                val secs = remainder


//            destMarker = mGoogleMap!!.addMarker(MarkerOptions().position(polyLine[polyLine.size - 1])
//                    .icon(BitmapDescriptorFactory.fromBitmap(etaMarker(etaText))))

                val etaMarker = (this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                        .inflate(R.layout.marker_eta, null)
                val tvEta = etaMarker.findViewById(R.id.tvEta) as TextView
                tvEta.text = getETAFormat(hours, mins)

                mGoogleMap!!.addMarker(
                    MarkerOptions().position(polyLine[polyLine.size - 1])
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                createDrawableFromView(
                                    this,
                                    etaMarker
                                )
                            )
                        )
                )
            } else mGoogleMap!!.addMarker(
                MarkerOptions().position(polyLine[polyLine.size - 1])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, dest)))
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun getDistanceTime(meters: Double, seconds: Double) {
        runOnUiThread {
            mTaxiMainViewModel.driverSpeed.value = meters / seconds
            println("RRR :::: speed = ${mTaxiMainViewModel.driverSpeed.value}")
        }
    }

    override fun whenFail(statusCode: String) {
        if (statusCode == "OVER_DAILY_LIMIT" || statusCode.contains("You have exceeded your daily request quota for this API")) {
            polylineKey = BaseApplication.getCustomPreference!!.getString(
                PreferenceKey.GOOGLE_API_KEY,
                ""
            ) as String
            PolylineUtil(this).execute(
                DirectionUtils().getDirectionsUrl(
                    mTaxiMainViewModel.tempSrc.value,
                    mTaxiMainViewModel.tempDest.value, polylineKey
                )
            )
        }
        println("RRR whenFail = $statusCode")
        when (statusCode) {
            //   "NOT_FOUND" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            //  "ZERO_RESULTS" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            "MAX_WAYPOINTS_EXCEEDED" -> Log.d("", getString(R.string.WayPointLlimitExceeded))
            "MAX_ROUTE_LENGTH_EXCEEDED" -> Log.d("", getString(R.string.RoadMapLimitExceeded))
            "INVALID_REQUEST" -> Log.d("", getString(R.string.InvalidInputs))
            "OVER_DAILY_LIMIT" -> Log.d(
                "",
                getString(R.string.MayBeInvalidAPIBillingPendingMethodDeprecated)
            )
            "OVER_QUERY_LIMIT" -> Log.d("", getString(R.string.TooManyRequestlimitExceeded))
            "REQUEST_DENIED" -> Log.d("", getString(R.string.DirectionsServiceNotEnabled))
            "UNKNOWN_ERROR" -> Log.d("", getString(R.string.ServerError))
            else -> Log.d("", statusCode)
        }
    }

    private fun bitmapFromVector(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
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
        if (isGust!!) {
            showServiceList()
        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun updateLocationUI() {

        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false

        LocationUtils.getLastKnownLocation(this, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
                mLastKnownLocation = location
                updateMapLocation(LatLng(location.latitude, location.longitude))
                val address = LocationUtils.getCurrentAddress(
                    baseContext,
                    LatLng(location.latitude, location.longitude)
                )
                if (address.size > 0) {
                    mTaxiMainViewModel.setAddress(address[0].getAddressLine(0))
                }
                srcLatLng = LatLng(location.latitude, location.longitude)
                mTaxiMainViewModel.sourceLat.set(location.latitude.toString())
                mTaxiMainViewModel.sourceLon.set(location.longitude.toString())
                mTaxiMainViewModel.getServices(
                    mServiceID!!, srcLatLng!!.latitude, srcLatLng!!.longitude, preference.getValue(
                        PreferenceKey.DEFAULTCITYID,
                        ""
                    ) as String
                )
                mPlacesAutocompleteUtil.getCurrentAddress(
                    srcLatLng!!,
                    getString(R.string.google_map_key)
                )
                // showServiceScreen()
            }

            override fun onFailure(messsage: String?) {
                updateMapLocation(DEFAULT_LOCATION)
            }
        })
    }

    fun updateMapLocation(location: LatLng, isAnimateMap: Boolean = false) {
        if (!isAnimateMap) {
            mGoogleMap?.moveCamera(newLatLngZoom(location, Constant.MapConfig.DEFAULT_ZOOM))
        } else {
            mGoogleMap?.animateCamera(newLatLngZoom(location, Constant.MapConfig.DEFAULT_ZOOM))
        }
    }

    override fun goToLocationPick() {
        mLocationPickFlag = 0
        val intent = Intent(this@TaxiMainActivity, LocationPickActivity::class.java)
        intent.putExtra("LocationPickFlag", mLocationPickFlag)
        startActivityForResult(
            intent,
            LocationPickActivity.DESTINATION_REQUEST_CODE
        )
    }

    override fun goToSourceLocationPick() {
        mLocationPickFlag = 0
        val intent = Intent(this@TaxiMainActivity, LocationPickActivity::class.java)
        intent.putExtra("LocationPickFlag", mLocationPickFlag)
        startActivityForResult(
            intent,
            LocationPickActivity.SOURCE_REQUEST_CODE
        )

    }

    override fun moveStatusFlow() {
        /*when (mFlowStatus) {
            "STATUS_FLOW" -> {
                mTaxiMainViewModel.setDriverStatus("Driver has arrived your location")
                mFlowStatus = "ARRIVED"
            }

            "ARRIVED" -> {
                mTaxiMainViewModel.setDriverStatus("You are on Ride")
                mFlowStatus = "STARTED"
                mTaxiMainViewModel.setRide(false)
            }

            "STARTED" -> {
                mTaxiActivityMainBinding.llStatusFlowTop.visibility = View.GONE
                mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE

            }
            "INVOICE" -> {
                val mInvoiceFragment: InvoiceFragment = InvoiceFragment.newInstance()
                replaceFragment(R.id.main_container, mInvoiceFragment, mInvoiceFragment.tag, false)
            }


        }*/
    }


    override fun goBack() {
        onBackPressed()
    }

    override fun showCurrentLocation() {
        if (mLastKnownLocation != null) updateMapLocation(
            LatLng(
                mLastKnownLocation!!.latitude,
                mLastKnownLocation!!.longitude
            ), true
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                LocationPickActivity.SOURCE_REQUEST_CODE -> {
                    srcLatLng = data!!.getParcelableExtra("SelectedLatLng")
                    mTaxiMainViewModel.setAddress(data.getStringExtra("SelectedLocation"))
                    mTaxiMainViewModel.sourceLat.set(srcLatLng!!.latitude.toString())
                    mTaxiMainViewModel.sourceLon.set(srcLatLng!!.longitude.toString())
                }

                LocationPickActivity.DESTINATION_REQUEST_CODE -> {
                    destLatLng = data!!.getParcelableExtra("SelectedLatLng")
                    mTaxiMainViewModel.setDestinationAddress(data.getStringExtra("SelectedLocation"))
                    mTaxiMainViewModel.destinationLat.set(destLatLng!!.latitude.toString())
                    mTaxiMainViewModel.destinationLon.set(destLatLng!!.longitude.toString())
                }
                LocationPickActivity.EDIT_LOCATION_REQUEST_CODE -> {
                    val editaddress = data?.getStringExtra("SelectedLocation").toString()
                    mTaxiMainViewModel.setDestinationAddress(editaddress)
                    mTaxiActivityMainBinding.destinationAddress.text = editaddress
                }
                Constant.PAYMENT_TYPE_REQUEST_CODE -> {
                    val payment_type: String = data!!.extras?.get("payment_type").toString()
                    val card_id = data!!.extras?.get("card_id").toString()
                    if (payment_type.equals("cash", true)) {
                        mTaxiActivityMainBinding.ivPaymentType.setImageDrawable(
                            resources.getDrawable(
                                R.drawable.ic_money
                            )
                        )
                        mTaxiActivityMainBinding.tvPaymentDetails.text =
                            payment_type.toString().toUpperCase()
                        mTaxiMainViewModel.paymentType.set("CASH")
                    } else {
                        mTaxiActivityMainBinding.ivPaymentType.setImageDrawable(
                            resources.getDrawable(
                                R.drawable.ic_credit_card
                            )
                        )
                        mTaxiActivityMainBinding.tvPaymentDetails.text = getString(R.string.card)
                        mTaxiMainViewModel.paymentType.set("CARD")
                        mTaxiMainViewModel.card_id.set(card_id)
                    }
                }
            }
            if (!TextUtils.isEmpty(mTaxiMainViewModel.addressvalue.get())
                    && !TextUtils.isEmpty(mTaxiMainViewModel.destinationaddressvalue.get()))
                if (srcLatLng != null && destLatLng != null) {
                    mTaxiActivityMainBinding.contentMain.iv_location.visibility = View.GONE
                    drawRoute(srcLatLng!!, destLatLng!!)
                }
        }
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(this, R.string.location_permission_denied, false)
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun onShowRationale(request: PermissionRequest) {
        ViewUtils.showRationaleAlert(this, R.string.location_permission_rationale, request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
        if (!enabled!!)
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun onCameraMove() {
        if (mShowService) {
            if (sheetBehavior.state == STATE_EXPANDED) {
                sheetBehavior.state = STATE_COLLAPSED
            }
        } else {
            sheetBehavior.state = STATE_HIDDEN
        }

    }


    override fun onCameraIdle() {

        if (srcLatLng != null) {
            if (mTaxiActivityMainBinding.contentMain.iv_location.visibility == View.VISIBLE) {
                val address =
                        LocationUtils.getCurrentAddress(
                            applicationContext,
                            mGoogleMap?.cameraPosition!!.target
                        )
                if (address.size > 0) {
                    mTaxiMainViewModel.setAddress(address[0].getAddressLine(0))

                    srcLatLng = mGoogleMap?.cameraPosition!!.target
                }
            }
        }
        if (mShowService) {
            sheetBehavior.state = STATE_EXPANDED
        } else {
            sheetBehavior.state = STATE_HIDDEN
        }

    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) run { supportFragmentManager.popBackStack() }
        else finish()
    }


    @SuppressLint("RestrictedApi")
    private fun backStackChangeListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_container)
            /*if (currentFragment == null) {
                fabLocation.visibility = View.VISIBLE
            } else {
                fabLocation.visibility = View.GONE
            }*/
        }
    }

    override fun onMessage(message: Any) {

        /* if (message is String) {
             if (TaxiRideRequest.getStatus().equals("STATUS_FLOW", true)) {
                 this.mFlowStatus = "STATUS_FLOW"
                 if (supportFragmentManager.findFragmentById(R.id.container) != null)
                     supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.container)!!)
                 mTaxiActivityMainBinding.llLocationLayout.visibility = View.GONE
                 mTaxiActivityMainBinding.llStatusFlowTop.visibility = View.VISIBLE
                 mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.VISIBLE
                 mTaxiActivityMainBinding.container.visibility = View.GONE
                 mTaxiMainViewModel.setRide(true)
             } else {
                 this.mFlowStatus = "EMPTY"
                 mTaxiActivityMainBinding.llLocationLayout.visibility = View.VISIBLE
                 mTaxiActivityMainBinding.llStatusFlowTop.visibility = View.GONE
                 mTaxiActivityMainBinding.llStatusFlowBottom.visibility = View.GONE
                 mTaxiActivityMainBinding.container.visibility = View.VISIBLE
             }
         }*/
    }

    private val mOnAdapterClickListener = object : OnViewClickListener {
        override fun onClick(position: Int) {
            val mService = mServiceList!![position]
            mTaxiMainViewModel.serviceType.set(mService.id.toString())

            /* mGoogleMap?.clear()

             val providerFilterList = mProviderList.filter { mProviderList ->
                 mProviderList.service!!.ride_vehicle!!.vehicle_name!!
                         .equals(mService.vehicleName)
             }

             for (i in 0 until providerFilterList.size) {
                 val latLngDestination = LatLng(providerFilterList[i].latitude!!
                         , providerFilterList[i].longitude!!)
                 val builder = LatLngBounds.Builder()
                 builder.include(latLngDestination)

                 val markerView = (baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                         as LayoutInflater)
                         .inflate(R.layout.xuber_marker_custom, null)


                 val picture = markerView.findViewById(R.id.picture) as ImageView

                 Glide.with(this@TaxiMainActivity)
                         .load(providerFilterList[i]
                                 .service!!
                                 .ride_vehicle!!
                                 .vehicle_marker)
                         .apply(RequestOptions()
                                 .dontAnimate())
                         .into(picture)


                 mGoogleMap!!.addMarker(MarkerOptions().position(latLngDestination)
                         .icon(BitmapDescriptorFactory.fromBitmap
                         (createDrawableFromView(this@TaxiMainActivity
                                 , markerView))))
             }

 */
            if (mService.isSelected) {
                mTaxiMainViewModel.setRateCard(mService)
                val rateCardFragment: RateCardFragment = RateCardFragment.newInstance()
                isRateCardShown = true
                mTaxiActivityMainBinding.bottomSheetLayout.visibility = View.GONE
                supportFragmentManager.beginTransaction().replace(
                    R.id.container, rateCardFragment, rateCardFragment.tag
                )
                        .addToBackStack(rateCardFragment.tag).commit()


            }
        }
    }


    /* private fun placeMarker() {

         mGoogleMap!!.clear()

         for (providerlist in providerListData.provider_service!!) {
             val markerView = (this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                     .inflate(R.layout.xuber_marker_custom, null)
             val picture = markerView.findViewById(R.id.picture) as ImageView
             Glide.with(this).load(providerlist.picture)
                     .apply(RequestOptions()
                             .placeholder(R.drawable.placeholder_car)
                             .error(R.drawable.placeholder_car)
                             .dontAnimate()).into(picture)
             val latLng1 = LatLng(providerlist.latitude.toDouble(), providerlist.longitude.toDouble())

             val marker = mGoogleMap!!.addMarker(MarkerOptions()
                     .position(latLng1)
                     .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@TaxiMainActivity
                             , markerView))))
             marker.setTag(providerlist)
         }
     }*/

    override fun showService() {
        isRateCardShown = false
        mTaxiActivityMainBinding.bottomSheetLayout.visibility = View.VISIBLE
    }
}
