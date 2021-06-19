package com.gox.xubermodule.ui.activity.locationpick

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.location.Location
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PlacesModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.*
import com.gox.basemodule.utils.LocationUtils.getLastKnownLocation
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.Addresses
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ActivityServiceLocationPickBinding
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersActivity
import com.gox.xubermodule.ui.activity.selectlocation.SelectLocationActivity
import com.gox.xubermodule.ui.adapter.OnViewClickListener
import kotlinx.android.synthetic.main.activity_service_location_pick.*
import kotlinx.android.synthetic.main.toolbar_location_pick.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class ServiceLocationPickActivity : BaseActivity<ActivityServiceLocationPickBinding>(),
        LocationPickNavigator,
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    private var mLocationPickViewModel: LocationPickViewModel? = null
    private var mGoogleMap: GoogleMap? = null
    private var latLng = LatLng(0.0, 0.0)

    private lateinit var mActivityLocationPickBinding: ActivityServiceLocationPickBinding
    private lateinit var mPlacesAutocompleteUtil: PlacesAutocompleteUtil
    private lateinit var prediction: ArrayList<PlacesModel>
    private lateinit var fragmentMap: SupportMapFragment
    private var countryCode: String = "US"
    private var canShowLocationList = false
    private var address: String = ""

    private var mAddressList: ArrayList<Addresses>? = null

    override fun getLayoutId(): Int = R.layout.activity_service_location_pick

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mActivityLocationPickBinding = mViewDataBinding as ActivityServiceLocationPickBinding
        mLocationPickViewModel = ViewModelProviders.of(this).get(LocationPickViewModel::class.java)
        mLocationPickViewModel?.navigator = this
        mPlacesAutocompleteUtil = PlacesAutocompleteUtil(applicationContext)
        mActivityLocationPickBinding.viewModel = mLocationPickViewModel
        mActivityLocationPickBinding.executePendingBindings()

        mAddressList = ArrayList()

        initialiseMap()

        etLocationPick.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val textVal: String = p0.toString()
                if (textVal.isEmpty()) {
                    rvAutoCompletePlaces.visibility = View.GONE
//                    llAddressContainer.visibility = View.VISIBLE
                } else if (textVal.length > 3)
                    if (canShowLocationList) {
                        prediction = ArrayList()
                        mPlacesAutocompleteUtil.findAutocompletePredictions(textVal, countryCode,
                                object : PlacesAutocompleteUtil.PlaceSuggestion {
                                    override fun onPlaceReceived(mPlacesList: ArrayList<PlacesModel>?) {
                                        rvAutoCompletePlaces.visibility = View.VISIBLE
//                                        llAddressContainer.visibility = View.GONE
                                        prediction = mPlacesList!!
                                        println("RRR :: prediction = ${prediction.size}")
                                        val adapter = PlacesAdapter(prediction)
                                        mViewDataBinding.placemodel = adapter
                                        mViewDataBinding.placemodel!!.notifyDataSetChanged()
                                        adapter.notifyDataSetChanged()
                                        adapter.setOnClickListener(mOnAdapterClickListener)
                                    }
                                })
                    }
            }
        })

        btnDone.setOnClickListener {
            address = etLocationPick.text.toString()
            if (address.isEmpty()) ViewUtils.showToast(applicationContext, "Enter Valid Address", false)
            else {


                XuberServiceClass.sourceLat = latLng.latitude.toString()
                XuberServiceClass.sourceLng = latLng.longitude.toString()
                XuberServiceClass.address = mActivityLocationPickBinding.etLocationPick.text.toString()
                openNewActivity(this@ServiceLocationPickActivity, ProvidersActivity::class.java, true)


            }
//            openNewActivity(this@ServiceLocationPickActivity, ProvidersActivity::class.java, false)


        }

        rlHomeAddressContainer.setOnClickListener {
            mAddressList!!.forEach {
                when (it.address_type) {
                    "Home" -> {
                        setResult(Activity.RESULT_OK, Intent()
                                .putExtra("SelectedLocation", tvHomeAddress.text)
                                .putExtra("SelectedLatLng", LatLng(it.latitude.toDouble(), it.longitude.toDouble())))
                        finish()
                    }
                    "Work" -> {
                        setResult(Activity.RESULT_OK, Intent()
                                .putExtra("SelectedLocation", tvWorkAddress.text)
                                .putExtra("SelectedLatLng", LatLng(it.latitude.toDouble(), it.longitude.toDouble())))
                        finish()
                    }
                }
            }
        }

        rlWorkAddressContainer.setOnClickListener {
            mAddressList!!.forEach {
                when (it.address_type) {
                    "Home" -> {
                        rlHomeAddressContainer.visibility = View.VISIBLE
                        tvHomeAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else ", " + it.flat_no}" +
                                " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}," +
                                " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                    }
                    "Work" -> {
                        rlWorkAddressContainer.visibility = View.VISIBLE
                        tvWorkAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else ", " + it.flat_no}" +
                                " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}," +
                                " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                        setResult(Activity.RESULT_OK, Intent()
                                .putExtra("SelectedLocation", tvWorkAddress.text.toString())
                                .putExtra("SelectedLatLng", LatLng(it.latitude.toDouble(), it.longitude.toDouble())))
                        finish()

                    }
                }
            }

        }

        ivBack.setOnClickListener { onBackPressed() }
        ivClear.setOnClickListener { etLocationPick.setText("") }

        mLocationPickViewModel = ViewModelProviders.of(this).get(LocationPickViewModel::class.java)
        mLocationPickViewModel!!.getAddressList()

        mLocationPickViewModel!!.getAddressesResponse().observe(this, Observer {
            if (it != null) {
                mAddressList!!.clear()
                if (it.statusCode == "200") {
                    mAddressList!!.addAll(it.responseData)
                    println("RRR :: mAddressList = ${Gson().toJson(mAddressList)}")

                    mAddressList!!.forEach {
                        when (it.address_type) {
                            "Home" -> {
                                rlHomeAddressContainer.visibility = View.VISIBLE
                                tvHomeAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else it.flat_no}" +
                                        " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                        " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                        " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}" +
                                        " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                            }
                            "Work" -> {
                                rlWorkAddressContainer.visibility = View.VISIBLE
                                tvWorkAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else it.flat_no}" +
                                        " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                        " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                        " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}" +
                                        " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                            }
                        }
                    }
                }
            }
        })
    }

    private val mOnAdapterClickListener = object : OnViewClickListener {
        override fun onClick(position: Int) {
            if (prediction.size > 0) try {
                val mPlace = prediction[position]
                val mLatLng = mPlacesAutocompleteUtil.getPlaceName(mPlace.mPlaceId)
                if (mLatLng.latitude != 0.0 && mLatLng.longitude != 0.0) {
                    canShowLocationList = false
                    mActivityLocationPickBinding.rvAutoCompletePlaces.visibility = View.GONE
//                    llAddressContainer.visibility = View.VISIBLE
                    etLocationPick.setText(prediction[position].mFullAddress)
                    latLng = mLatLng
                }
                Handler().postDelayed({ canShowLocationList = true }, 2000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        this.mGoogleMap?.setOnCameraIdleListener(this)
        updateLocationUIWithPermissionCheck()

    }


    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun updateLocationUI() {
        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false

        getLastKnownLocation(this, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
                updateMapLocation(LatLng(location.latitude, location.longitude))
                countryCode = LocationUtils.getCountryCode(applicationContext, LatLng(location.latitude, location.longitude))
            }

            override fun onFailure(messsage: String?) {
                updateMapLocation(Constant.MapConfig.DEFAULT_LOCATION)
            }
        })
    }

    fun updateMapLocation(mLatLng: LatLng) {
        mGoogleMap?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mLatLng, Constant.MapConfig.DEFAULT_ZOOM))
    }


    override fun onCameraIdle() {
        val address =
                LocationUtils.getCurrentAddress(applicationContext, mGoogleMap?.cameraPosition!!.target)
        if (address.size > 0) {
            etLocationPick.setText(address[0].getAddressLine(0))
            latLng = mGoogleMap?.cameraPosition!!.target
        }
        mActivityLocationPickBinding.rvAutoCompletePlaces.visibility = View.GONE
//        llAddressContainer.visibility = View.VISIBLE
        canShowLocationList = false
        Handler().postDelayed({ canShowLocationList = true }, 2000)
    }

    @OnPermissionDenied(ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(this@ServiceLocationPickActivity, R.string.location_permission_denied, false)
    }

    @SuppressLint("NoCorrespondingNeedsPermission")
    @OnShowRationale(ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onShowRationale(request: permissions.dispatcher.PermissionRequest) {
        ViewUtils.showRationaleAlert(this@ServiceLocationPickActivity, R.string.location_permission_rationale, request)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onBackPressed() {
        openNewActivity(this,SelectLocationActivity::class.java,true)
    }
}
