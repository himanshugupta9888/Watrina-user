package com.gox.foodiemodule.ui.ordertracking

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.LocationPointsEntity
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.socket.SocketListener
import com.gox.basemodule.socket.SocketManager
import com.gox.basemodule.utils.CarMarkerAnimUtil
import com.gox.basemodule.utils.PolyUtil
import com.gox.basemodule.utils.polyline.DirectionUtils
import com.gox.basemodule.utils.polyline.PolyLineListener
import com.gox.basemodule.utils.polyline.PolylineUtil
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.OrderTrackModel
import com.gox.foodiemodule.databinding.ActivityOrderTrackingBinding
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailViewModel
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.lang.NullPointerException

class OrderTrackingActivity : BaseActivity<ActivityOrderTrackingBinding>(), OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, PolyLineListener {

    private lateinit var mActivityOrderTracking: ActivityOrderTrackingBinding
    private lateinit var fragmentMap: SupportMapFragment

    private var mViewModel: OrderTrackingViewModel? = null
    private var orderDetailViewModel: OrderDetailViewModel? = null
    private var orderTrackModel = OrderTrackModel()

    private var canDrawPolyLine: Boolean = true
    private var polyLine = ArrayList<LatLng>()
    private var mGoogleMap: GoogleMap? = null
    private var mPolyline: Polyline? = null
    private var srcMarker: Marker? = null
    private var polyUtil = PolyUtil()
    private var mLastUpdated = false

    private var startLatLng = LatLng(0.0, 0.0)
    private var endLatLng = LatLng(0.0, 0.0)

    override fun getLayoutId(): Int = R.layout.activity_order_tracking

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mActivityOrderTracking = mViewDataBinding as ActivityOrderTrackingBinding
        mViewModel = ViewModelProviders.of(this).get(OrderTrackingViewModel::class.java)
        orderDetailViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        orderTrackModel = intent.extras!!.get("location") as OrderTrackModel
        try {
            mViewModel!!.polyLineSrc.value = LatLng(orderTrackModel.storeLatitude!!, orderTrackModel.storeLongitude!!)
            mViewModel!!.polyLineDest.value = LatLng(orderTrackModel.deliveryLatitude!!, orderTrackModel.deliveryLongitude!!)
        } catch (NE:NullPointerException){
            NE.printStackTrace()
        }
        initialiseMap()
        if (orderTrackModel.providerId != null) {
            fireBaseLocationListener(orderTrackModel.providerId!!)
        }

        mActivityOrderTracking.toolbar.title = getString(R.string.map_track)
        setSupportActionBar(mActivityOrderTracking.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mActivityOrderTracking.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (orderTrackModel.orderId != 0) {
            SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION, Emitter.Listener {
                updateViaSocket(it)
            })
        }



        SocketManager.setOnSocketRefreshListener(object : SocketListener.connectionRefreshCallBack {
            override fun onRefresh() {
                SocketManager.emit(Constant.ROOM_NAME.ORDER_ROOM_NAME, Constant.ROOM_ID.getOrderRoom(orderTrackModel.orderId?:0))

                SocketManager.onEvent(Constant.ROOM_NAME.UPDATELOCATION, Emitter.Listener{
                    updateViaSocket(it)
                })
            }
        })

    }

    private fun updateViaSocket(it: Array<Any>) {
        if(mLastUpdated){
            return
        }else{
            mLastUpdated = true
        }

        runOnUiThread {
            Handler().postDelayed({
                mLastUpdated = false
            },5000)

            val data1 = it[0] as JSONObject
            orderTrackModel.storeLatitude = data1.getString("lat").toDouble()
            orderTrackModel.storeLongitude = data1.getString("lng").toDouble()

            mViewModel!!.latitude.value = orderTrackModel.storeLatitude
            mViewModel!!.longitude.value = orderTrackModel.storeLongitude

            mViewModel!!.polyLineSrc.value = LatLng(orderTrackModel.storeLatitude!!, orderTrackModel.storeLongitude!!)
            //mViewModel!!.polyLineDest.value = LatLng(orderTrackModel.deliveryLatitude!!, orderTrackModel.deliveryLongitude!!)

            if (startLatLng.latitude != 0.0) endLatLng = startLatLng
            startLatLng = LatLng(data1.getString("lat").toDouble(), data1.getString("lng").toDouble())

            if (endLatLng.latitude != 0.0 && polyLine.size > 0) {
                try {
                    CarMarkerAnimUtil().carAnimWithBearing(srcMarker!!, endLatLng, startLatLng)
                    polyLineRerouting(endLatLng, polyLine)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (polyLine.size < 1)
                drawRoute(mViewModel!!.polyLineSrc.value!!, mViewModel!!.polyLineDest.value!!)

        }
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
        drawRoute(mViewModel!!.polyLineSrc.value!!, mViewModel!!.polyLineDest.value!!)
    }

    override fun onCameraMove() {

    }

    override fun onCameraIdle() {

    }

    private fun drawRoute(src: LatLng, dest: LatLng) {
        if (canDrawPolyLine) {
            canDrawPolyLine = false
           Handler().postDelayed({ canDrawPolyLine = true }, 5000)
        }
        PolylineUtil(this).execute(DirectionUtils().getDirectionsUrl(src, dest,
                BaseApplication.getCustomPreference!!.getString(PreferenceKey.GOOGLE_API_KEY, "")))
    }

    override fun whenDone(output: PolylineOptions) {
        mGoogleMap!!.clear()

        mPolyline = mGoogleMap!!.addPolyline(output.width(5f)
                .color(ContextCompat.getColor(baseContext, R.color.black))
        )
        polyLine = output.points as ArrayList<LatLng>

        val builder = LatLngBounds.Builder()

        for (latLng in polyLine) builder.include(latLng)

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 250))
        try {
            srcMarker = mGoogleMap!!.addMarker(MarkerOptions().position(polyLine[0])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, R.drawable.ic_marker_foodie))))

            mGoogleMap!!.addMarker(MarkerOptions().position(polyLine[polyLine.size - 1])
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromVector(baseContext, R.drawable.ic_marker_stop))))

            CarMarkerAnimUtil().carAnimWithBearing(srcMarker!!, polyLine[1], polyLine[0])
        } catch (e: Exception) {
            print(e.localizedMessage)
        }
    }

    override fun getDistanceTime(meters: Double, seconds: Double) {
        runOnUiThread {
            //            mTaxiMainViewModel.driverSpeed.value = meters / seconds
//            println("RRR :::: speed = ${mTaxiMainViewModel.driverSpeed.value}")
        }
    }

    override fun whenFail(statusCode: String) {
        when (statusCode) {
            //    "NOT_FOUND" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            //     "ZERO_RESULTS" -> Toast.makeText(this, getString(R.string.NoRoadMapAvailable), Toast.LENGTH_SHORT).show()
            "MAX_WAYPOINTS_EXCEEDED" -> Toast.makeText(this, getString(R.string.WayPointLlimitExceeded), Toast.LENGTH_SHORT).show()
            "MAX_ROUTE_LENGTH_EXCEEDED" -> Toast.makeText(this, getString(R.string.RoadMapLimitExceeded), Toast.LENGTH_SHORT).show()
            "INVALID_REQUEST" -> Toast.makeText(this, getString(R.string.InvalidInputs), Toast.LENGTH_SHORT).show()
            "OVER_DAILY_LIMIT" -> Toast.makeText(this, getString(R.string.MayBeInvalidAPIBillingPendingMethodDeprecated), Toast.LENGTH_SHORT).show()
            "OVER_QUERY_LIMIT" -> Toast.makeText(this, getString(R.string.TooManyRequestlimitExceeded), Toast.LENGTH_SHORT).show()
            "REQUEST_DENIED" -> Toast.makeText(this, getString(R.string.DirectionsServiceNotEnabled), Toast.LENGTH_SHORT).show()
            "UNKNOWN_ERROR" -> Toast.makeText(this, getString(R.string.ServerError), Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, statusCode, Toast.LENGTH_SHORT).show()
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

    private fun fireBaseLocationListener(id: Int) {
        val myRef = FirebaseDatabase.getInstance().getReference("providerId$id")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(LocationPointsEntity::class.java)
                if (value != null) {
                    mViewModel!!.latitude.value = value.lat
                    mViewModel!!.longitude.value = value.lng

                    if (startLatLng.latitude != 0.0) endLatLng = startLatLng
                    startLatLng = LatLng(value.lat, value.lng)

                    if (endLatLng.latitude != 0.0 && polyLine.size > 0) try {
                        CarMarkerAnimUtil().carAnimWithBearing(srcMarker!!, endLatLng, startLatLng)
                        polyLineRerouting(endLatLng, polyLine)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun polyLineRerouting(point: LatLng, polyLine: ArrayList<LatLng>) {
        val index = polyUtil.locationIndexOnEdgeOrPath(point, polyLine, false, true, 50.0)
        if (index >= 0) {
            polyLine.subList(0, index + 1).clear()
//            polyLine.add(0, point)
            mPolyline!!.remove()
            val options = PolylineOptions()
            options.addAll(polyLine)
            mPolyline = mGoogleMap!!.addPolyline(options.width(5f).color
            (ContextCompat.getColor(baseContext, R.color.black)))
            println("RRR mPolyline = " + polyLine.size)
        } else {
            canDrawPolyLine = true
            drawRoute(LatLng(mViewModel!!.latitude.value!!, mViewModel!!.longitude.value!!),
                    mViewModel!!.polyLineDest.value!!)
        }
    }
}