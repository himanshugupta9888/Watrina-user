package com.gox.xubermodule.ui.fragment.providersmapfragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.LocationCallBack
import com.gox.basemodule.utils.LocationUtils
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.Addresses
import com.gox.xubermodule.data.model.ProviderListModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ProvidersMapFragmentBinding
import com.gox.xubermodule.ui.activity.locationpick.LocationPickNavigator
import com.gox.xubermodule.ui.activity.providerdetail.ProviderDetailActivity
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersViewModel
import com.gox.xubermodule.utils.Utils.getPrice
import de.hdodenhof.circleimageview.CircleImageView
import permissions.dispatcher.*
import java.util.*

@RuntimePermissions
class ProvidersMapFragment : BaseFragment<ProvidersMapFragmentBinding>(), LocationPickNavigator,
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var fragmentMap: SupportMapFragment
    private var address: String = ""

    private var canShowLocationList = false
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null
    private var mAddress: String? = null
    private var mAddressList: ArrayList<Addresses>? = null
    private var latLng = LatLng(0.0, 0.0)
    lateinit var providersActivityViewModel: ProvidersViewModel
    lateinit var providerListData: ProviderListModel.ResponseData

    lateinit var providersMapFragmentBinding: ProvidersMapFragmentBinding
    override fun getLayoutId(): Int = R.layout.providers_map_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        providersMapFragmentBinding = mViewDataBinding as ProvidersMapFragmentBinding
        providersActivityViewModel = ViewModelProviders.of(activity!!).get(ProvidersViewModel::class.java)
        /* providersActivityViewModel.getProviderResponse().observe(this, androidx.lifecycle.Observer {
             if (it.statusCode == "200") {
                 Log.d("ProvidersMapFragment", "VALUE" + it.message)
                 providerListData = it.responseData!!
             }
         })*/
        initialiseMap()

    }


    override fun onMapReady(mGoogleMap: GoogleMap?) {

        try {
            mGoogleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity as Context, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

        this.mGoogleMap = mGoogleMap
        this.mGoogleMap?.setOnCameraIdleListener(this)
        this.mGoogleMap!!.setOnInfoWindowClickListener { marker ->
            val providerService = marker.tag as ProviderListModel.ResponseData.ProviderService?
            Log.d("setOnInfoClick_D", "" + providerService!!.base_fare)
            XuberServiceClass.providerListModel = providerService!!
            openNewActivity(activity, ProviderDetailActivity::class.java, true)
//            openNewActivity(activity, ServiceFlowActivity::class.java, true)

        }
        updateLocationUIWithPermissionCheck()

    }

    private fun initialiseMap() {
        fragmentMap = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        fragmentMap.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun updateLocationUI() {
        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false

        LocationUtils.getLastKnownLocation(activity as Context, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
//                updateMapLocation(LatLng(location.latitude, location.longitude))
                if (!XuberServiceClass.sourceLat.isNullOrEmpty()&&!XuberServiceClass.sourceLng.isNullOrEmpty())
                updateMapLocation(LatLng(XuberServiceClass.sourceLat.toDouble(), XuberServiceClass.sourceLng.toDouble()))
                else
                    updateMapLocation(LatLng(location.latitude, location.longitude))
                getProviderList()

            }

            override fun onFailure(messsage: String?) {
                updateMapLocation(Constant.MapConfig.DEFAULT_LOCATION)
            }
        })
    }

    private fun getProviderList() {

        providersActivityViewModel.getProviderResponse().observe(this, androidx.lifecycle.Observer {
            if (it.statusCode == "200") {
                Log.d("ProvidersMapFragment", "VALUE" + it.message)
                providerListData = it.responseData!!
                getFindMatch()

            }
        })

    }

    fun updateMapLocation(mLatLng: LatLng) {
        mGoogleMap?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mLatLng, Constant.MapConfig.DEFAULT_ZOOM))
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(activity as Context, R.string.location_permission_denied, false)
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onShowRationale(request: PermissionRequest) {
        ViewUtils.showRationaleAlert(activity as Context, R.string.location_permission_rationale, request)
    }

    override fun onCameraIdle() {
        val address =
                LocationUtils.getCurrentAddress(activity!!.applicationContext, mGoogleMap?.cameraPosition!!.target)
        if (address.size > 0) {
            mAddress = address[0].getAddressLine(0).toString()
            latLng = mGoogleMap?.cameraPosition!!.target
            mLatitude = mGoogleMap?.cameraPosition!!.target.latitude
            mLongitude = mGoogleMap?.cameraPosition!!.target.longitude
        }
        canShowLocationList = false
        Handler().postDelayed({ canShowLocationList = true }, 2000)
    }

    override fun onRequestPermissionsResult(code: Int, p: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, p, results)
        onRequestPermissionsResult(code, results)
    }

    companion object {
        const val SOURCE_REQUEST_CODE = 101
        const val DESTINATION_REQUEST_CODE = 102
    }

    private fun getFindMatch() {

        mGoogleMap!!.clear()
//        val latLng1 = LatLng(13.057729177659835, 80.2531736344099)


        for (providerlist in providerListData.provider_service!!) {
            val markerView = (activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.xuber_marker_custom, null)
            val picture = markerView.findViewById(R.id.xuberPicture) as CircleImageView
            Glide.with(activity as Context).load(providerlist.picture)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_profile_place_holder).error(R.drawable.ic_profile_place_holder).dontAnimate()).into(picture)
            val latLng1 = LatLng(providerlist.latitude.toDouble(), providerlist.longitude.toDouble())

            val marker = mGoogleMap!!.addMarker(MarkerOptions()
                    .position(latLng1)
                    .title(providerlist.first_name)
                    .snippet(getPrice(providerlist, activity!!))
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(activity as Context, markerView))))
            marker.setTag(providerlist)
        }
    }

    fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}
