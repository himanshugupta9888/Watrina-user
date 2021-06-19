package com.gox.basemodule.common.edit_address

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
import com.gox.basemodule.common.add_address.AddAddressNavigator
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.databinding.ActivityEditAddressActvityBinding
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.model.AddressModel
import com.gox.basemodule.utils.LocationCallBack
import com.gox.basemodule.utils.LocationUtils
import com.gox.basemodule.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_edit_address_actvity.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class EditAddressActivity : BaseActivity<ActivityEditAddressActvityBinding>(), OnMapReadyCallback, AddAddressNavigator, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener {

    lateinit var mViewDataBinding: ActivityEditAddressActvityBinding
    private var mGoogleMap: GoogleMap? = null
    private lateinit var fragmentMap: SupportMapFragment
    private var mLastKnownLocation: Location? = null
    private val reqAddAddressViewModel = ReqAddressModel()
    private var mAddressModel = AddressModel()
    private lateinit var mEditViewModel: EditViewModel
    val address_type = arrayOf("Home", "Work", "Other")
    override fun getLayoutId(): Int = R.layout.activity_edit_address_actvity

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityEditAddressActvityBinding
        mEditViewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)
        mAddressModel = intent.extras!!.get("address") as AddressModel

        observeLiveData(mEditViewModel.loading){
            baseLiveDataLoading.value = it
        }

        mViewDataBinding.editAddressToolbarLayout.title_toolbar.setTitle("Edit Address")
        mViewDataBinding.editAddressToolbarLayout.toolbar_back_img.setOnClickListener {
            finish()
        }
        mViewDataBinding.etFlatNumber.setText(mAddressModel.flatNumber.toString())
        mViewDataBinding.etLandmark.setText(mAddressModel.landmark.toString())
        mViewDataBinding.etStreetName.setText(mAddressModel.street.toString())
        mViewDataBinding.etAddressTitle.setText(mAddressModel.title.toString())
        mViewDataBinding.btnDone.setOnClickListener {
            reqAddAddressViewModel.addressId = mAddressModel.id.toString()
            reqAddAddressViewModel.method = "PATCH"
            reqAddAddressViewModel.flatNo = mViewDataBinding.etFlatNumber.text.toString()
            reqAddAddressViewModel.landmark = mViewDataBinding.etLandmark.text.toString()
            reqAddAddressViewModel.street = mViewDataBinding.etStreetName.text.toString()
            if (reqAddAddressViewModel.addressType.equals("Other") || reqAddAddressViewModel.addressType.equals("")) {
                reqAddAddressViewModel.title = mViewDataBinding.etAddressTitle.text.toString()
                if (reqAddAddressViewModel.title.equals("")) {
                    ViewUtils.showToast(this, R.string.valid_title, false)
                } else {
                    mEditViewModel.updateAddress(reqAddAddressViewModel)
                }
            } else {
                mEditViewModel.updateAddress(reqAddAddressViewModel)
            }


        }
        mEditViewModel.getEditResponse().observe(this, Observer {
            mEditViewModel.loading.value = false
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(this, it.message, true)
                    finish()

                }
            }
        })

        mEditViewModel.errorResponse.observe(this, Observer {
            mEditViewModel.loading.value = false
            ViewUtils.showToast(this, it, false)
        })

        val adapter = ArrayAdapter(
                this, // Context
                android.R.layout.simple_spinner_item, // Layout
                address_type // Array
        )

        txt_save_as.setOnClickListener(View.OnClickListener {
            address_tag_type.performClick()
        })

        // Set the drop down view resource
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        // Finally, data bind the spinner object with dapter
        mViewDataBinding.addressTagType.adapter = adapter;


        var position = address_type.map { it.toLowerCase() }.indexOf(mAddressModel.addressType?.toLowerCase())
        if(position == -1){
            position = address_type.size - 1
        }

        mViewDataBinding.addressTagType.post {
            mViewDataBinding.addressTagType.setSelection(position)
        }


        // Set an on item selected listener for spinner object
        mViewDataBinding.addressTagType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Display the selected item text on text view
                reqAddAddressViewModel.addressType = parent.getItemAtPosition(position).toString()
                txt_save_as.setText(parent.getItemAtPosition(position).toString())

                if (position == 2) {
                    mViewDataBinding.addressTypeInput.visibility = View.VISIBLE

                } else {
                    mViewDataBinding.addressTypeInput.visibility = View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback

            }
        }
        val compareValue = mAddressModel.addressType
        if (compareValue != null) {
            val spinnerPosition = adapter.getPosition(compareValue);
            mViewDataBinding.addressTagType.setSelection(spinnerPosition);
        }
        initialiseMap()
    }

    private fun initialiseMap() {
        fragmentMap = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        fragmentMap.getMapAsync(this@EditAddressActivity)

    }

    override fun onMapReady(mGoogleMap: GoogleMap?) {
        try {
            mGoogleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        this.mGoogleMap = mGoogleMap
        this.mGoogleMap?.setOnCameraMoveListener(this@EditAddressActivity)
        this.mGoogleMap?.setOnCameraIdleListener(this@EditAddressActivity)

        updateLocationUIWithPermissionCheck()
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun updateLocationUI() {
        mGoogleMap!!.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap!!.uiSettings.isCompassEnabled = false
        LocationUtils.getLastKnownLocation(this@EditAddressActivity, object : LocationCallBack.LastKnownLocation {
            override fun onSuccess(location: Location) {
                updateMapLocation(LatLng(
                        mAddressModel.latitude!!.toDouble(),
                        mAddressModel.longitude!!.toDouble()
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
                    mAddressModel.latitude!!.toDouble(),
                    mAddressModel.longitude!!.toDouble()
            ), true)
        }
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        ViewUtils.showToast(this@EditAddressActivity, R.string.location_permission_denied, false)
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onShowRationale(request: permissions.dispatcher.PermissionRequest) {
        ViewUtils.showRationaleAlert(this@EditAddressActivity, R.string.location_permission_rationale, request)
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
            if (address.size > 0) {
                mViewDataBinding.addAddressTv.text = address[0].getAddressLine(0).toString()
                //  mZipCode = address[0].postalCode
                // mLon = address[0].longitude.toString()
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

            }

        }


    }

}