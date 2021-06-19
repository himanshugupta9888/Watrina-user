package com.gox.basemodule.common.add_address

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.gox.basemodule.model.ReqAddressModel
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.databinding.ActivityAddAddressBinding
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.utils.LocationCallBack
import com.gox.basemodule.utils.LocationUtils
import com.gox.basemodule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class AddAddressActivity : BaseActivity<ActivityAddAddressBinding>(), OnMapReadyCallback, AddAddressNavigator, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener {
    lateinit var mViewDataBinding: ActivityAddAddressBinding
    override fun getLayoutId(): Int = R.layout.activity_add_address
    private lateinit var fragmentMap: SupportMapFragment
    private var mLastKnownLocation: Location? = null
    private var mGoogleMap: GoogleMap? = null
    private val reqAddAddressViewModel = ReqAddressModel()
    private lateinit var mAddAddressViewModel: AddAddressViewModel
    val addressType = arrayOf("Home", "Work", "Other")
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityAddAddressBinding
        mAddAddressViewModel = ViewModelProviders.of(this).get(AddAddressViewModel::class.java)
        mViewDataBinding.addAddressToolbarLayout.title_toolbar.title = "Add Address"
        mViewDataBinding.addAddressToolbarLayout.toolbar_back_img.setOnClickListener {
            finish()
        }
        mAddAddressViewModel.navigator = this
        mViewDataBinding.addressviewmodel = mAddAddressViewModel

        observeLiveData(mAddAddressViewModel.loading){
            baseLiveDataLoading.value = it
        }

        mViewDataBinding.btnDone.setOnClickListener {
            addAddress()
        }

        mAddAddressViewModel.getAddAddressResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mAddAddressViewModel.loading.value = false
                ViewUtils.showToast(applicationContext, it.message, true)
                finish()
            }
        })
        mAddAddressViewModel.errorResponse.observe(this, Observer {
            mAddAddressViewModel.loading.value = false
            ViewUtils.showToast(applicationContext, it.toString(), false)
        })
        initialiseMap()

        val adapter = ArrayAdapter(
                this, // Context
                android.R.layout.simple_spinner_item, // Layout
                addressType // Array
        )

        mViewDataBinding.txtSaveAs.setOnClickListener {
            address_tag_type.performClick()
        }

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with adapter
        mViewDataBinding.addressTagType.adapter = adapter

        // Set an on item selected listener for spinner object
        mViewDataBinding.addressTagType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Display the selected item text on text view
                reqAddAddressViewModel.addressType = parent.getItemAtPosition(position).toString()
                mViewDataBinding.txtSaveAs.setText(parent.getItemAtPosition(position).toString())
                if (position == 2) {
                    mViewDataBinding.addressTypeInput.visibility = View.VISIBLE
                    reqAddAddressViewModel.title = mViewDataBinding.etAddressTitle.text.toString()
                } else {
                    mViewDataBinding.addressTypeInput.visibility = View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback

            }
        }
    }

    private fun addAddress() {
        reqAddAddressViewModel.flatNo = mViewDataBinding.etFlatNumber.text.toString()
        reqAddAddressViewModel.street = mViewDataBinding.etStreetName.text.toString()
        reqAddAddressViewModel.landmark = mViewDataBinding.etLandmark.text.toString()
        reqAddAddressViewModel.title = mViewDataBinding.etAddressTitle.text.toString()

        if(reqAddAddressViewModel.flatNo.isNullOrEmpty()){
            ViewUtils.showToast(this, getString(R.string.valid_flat_number), false)
        }else if(reqAddAddressViewModel.street.isNullOrEmpty()){
            ViewUtils.showToast(this, getString(R.string.valid_street_name), false)
        }else if(reqAddAddressViewModel.landmark.isNullOrEmpty()){
            ViewUtils.showToast(this, getString(R.string.valid_landmark_name), false)
        }
        else if (reqAddAddressViewModel.addressType.equals("Other") || reqAddAddressViewModel.addressType.equals("")) {
            if (reqAddAddressViewModel.title.equals("")) {
                ViewUtils.showToast(this, R.string.valid_title, false)
            } else {
                mAddAddressViewModel.loading.value = true
                mAddAddressViewModel.addAddress(reqAddAddressViewModel)
            }
        }else{
            mAddAddressViewModel.loading.value = true
            mAddAddressViewModel.addAddress(reqAddAddressViewModel)
        }
    }

    private fun initialiseMap() {
        fragmentMap = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        fragmentMap.getMapAsync(this@AddAddressActivity)

    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        try {
            mGoogleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        this.mGoogleMap = mGoogleMap
        this.mGoogleMap?.setOnCameraMoveListener(this@AddAddressActivity)
        this.mGoogleMap?.setOnCameraIdleListener(this@AddAddressActivity)

        updateLocationUIWithPermissionCheck()
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun updateLocationUI() {

        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false


        LocationUtils.getLastKnownLocation(this@AddAddressActivity, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
                mLastKnownLocation = location
                updateMapLocation(LatLng(
                        location.latitude,
                        location.longitude
                ))
            }

            override fun onFailure(messsage: String?) {
                updateMapLocation(Constant.MapConfig.DEFAULT_LOCATION)
            }
        })
    }


    fun updateMapLocation(location: LatLng, isAnimateMap: Boolean = false) {
        if (!isAnimateMap) {
            mGoogleMap?.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(location, Constant.MapConfig.DEFAULT_ZOOM))
        } else {
            mGoogleMap?.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(location, Constant.MapConfig.DEFAULT_ZOOM))
        }


    }

    override fun showCurrentLocation() {
        if (mLastKnownLocation != null) {
            updateMapLocation(LatLng(
                    mLastKnownLocation!!.latitude,
                    mLastKnownLocation!!.longitude
            ), true)
        }
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(this@AddAddressActivity, R.string.location_permission_denied, false)
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onShowRationale(request: permissions.dispatcher.PermissionRequest) {
        ViewUtils.showRationaleAlert(this@AddAddressActivity, R.string.location_permission_rationale, request)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onCameraMove() {


    }

    override fun onCameraIdle() {

        val latLong = mGoogleMap?.cameraPosition!!.target
        if (latLong != null) {
            val address = LocationUtils.getCurrentAddress(applicationContext, latLong)
            Log.d("Address", "Addresss" + address.size)
            if (address.isNotEmpty()) {
                mViewDataBinding.addAddressTv.text = address[0].getAddressLine(0).toString()
                reqAddAddressViewModel.lat = address[0].latitude.toString()
                reqAddAddressViewModel.lon = address[0].longitude.toString()
                if(address[0].locality!=null){
                    reqAddAddressViewModel.city = address[0].locality.toString()
                }
                if(address[0].adminArea!=null){
                    reqAddAddressViewModel.state = address[0].adminArea.toString()
                }
                if(address[0].countryName!=null){
                    reqAddAddressViewModel.country = address[0].countryName.toString()
                }


                reqAddAddressViewModel.mapAddress = address[0].getAddressLine(0)

            }
        }


    }


}