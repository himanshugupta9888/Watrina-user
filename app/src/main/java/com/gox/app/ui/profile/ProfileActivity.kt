package com.gox.app.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.*
import com.gox.app.databinding.ActivityEditProfileBinding
import com.gox.app.ui.changepasswordactivity.ChangePasswordActivity
import com.gox.app.ui.cityListActivity.CityListActivity
import com.gox.app.ui.verifyotp.VerifyOTPActivity
import com.gox.app.utils.Country
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.Constant.APP_REQUEST_CODE
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.utils.ValidationUtils
import com.gox.basemodule.utils.ViewUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.Serializable

class ProfileActivity : BaseActivity<ActivityEditProfileBinding>(), ProfileNavigator {

    lateinit var mViewDataBinding: ActivityEditProfileBinding
    private lateinit var mProfileViewmodel: ProfileViewModel
    private var mCropImageUri: Uri? = null
    private var localPath: Uri? = null
    private var mProfileData: ProfileResponse? = null
    private var mMobileNumberFlag = 0
    override fun getLayoutId(): Int = R.layout.activity_edit_profile
    private lateinit var city: List<City>
    private var cityList: ArrayList<City> = ArrayList()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    private var mCountryId: String? = null
    private lateinit var permissions: Array<String>

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        this.mViewDataBinding = mViewDataBinding as ActivityEditProfileBinding
        mViewDataBinding.toolbarLayout.title_toolbar.setTitle(R.string.profile)
        mViewDataBinding.toolbarLayout.toolbar_back_img.setOnClickListener { view ->
            finish()
        }
        mProfileViewmodel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        mViewDataBinding.profileviewmodel = mProfileViewmodel
        mProfileViewmodel.navigator = this
        baseLiveDataLoading = mProfileViewmodel.loadingProgress
        mProfileViewmodel.getProfile()
        mProfileViewmodel.getProfileCountryList()
        mProfileViewmodel.getProfileResponse().observe(this, Observer {
            mProfileData = it
            if (it.profileData?.picture != null) {
                Glide.with(this@ProfileActivity)
                        .load(it.profileData?.picture)
                        .into(mViewDataBinding.profileImage)
            } else {
                Glide.with(this@ProfileActivity)
                        .load(R.drawable.ic_profile_place_holder)
                        .into(mViewDataBinding.profileImage)
            }

            if (mProfileData?.profileData?.loginBy == "MANUAL") {
                mViewDataBinding.changePasswordTv.visibility = View.VISIBLE

            } else {
                mViewDataBinding.changePasswordTv.visibility = View.GONE
            }

            if (!mProfileData?.let { it.profileData?.let { it.countryCode } }.isNullOrEmpty()) {

            }

        })
        mProfileViewmodel.updateProfileResponse().observe(this, Observer {
            ViewUtils.showToast(this, it.message, true)
            finish()
        })

        mProfileViewmodel.countryListResponse.observe(this@ProfileActivity, Observer<CountryListResponse> {
            Log.d("_D", "country_code :" + it.responseData[0].country_code)
            /*val intent = Intent(this@ProfileActivity, CountryListActivity::class.java)
            intent.putExtra("selectedfrom", "country")
            intent.putExtra("countrylistresponse", it as Serializable)
            startActivityForResult(intent, Constant.COUNTRYLIST_REQUEST_CODE)*/

            for (i in 0 until it.responseData.size) {
                if (it.responseData[i].id.toString() == mCountryId) {
                    cityList = it.responseData[i].city as ArrayList<City>
                }

            }
        })


        mProfileViewmodel.sendOTPResponse.observe(this, Observer {
            mProfileViewmodel.loadingProgress.value = false
            ViewUtils.showToast(this, getString(R.string.otp_success), true)
            Handler().postDelayed({
                val intent = Intent(this, VerifyOTPActivity::class.java)
                intent.putExtra("country_code", mProfileViewmodel.mCountryCode.get().toString().replace("+", ""))
                intent.putExtra("mobile", mProfileViewmodel.mMobileNumber.get().toString())
                startActivityForResult(intent, Constant.APP_REQUEST_CODE)
            }, 1000)

        })

        mProfileViewmodel.errorResponse.observe(this, Observer {
            mProfileViewmodel.loadingProgress.value = false
            ViewUtils.showToast(this, it, true)
        })

        val listCountry: List<CountryModel> = Country.getAllCountries()


//        var monthList: List<String> = listCountry.filter { s -> s == "January" }


        mViewDataBinding.genderRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById(checkedId) as RadioButton
            if (checkedRadioButton.isChecked)
                mProfileViewmodel.gender.set(checkedRadioButton.text.toString())

        }
        mProfileViewmodel.mProfileResponse.observe(this@ProfileActivity, Observer { response ->

            val countryDetail: List<CountryModel> = listCountry.filter { countryModel ->
                countryModel.code == response.profileData!!
                        .countryName!!.countryCode
            }
            Log.d("_D_countryList", countryDetail[0].code + "")

            mProfileViewmodel.mUserName.set(response.profileData!!.firstName)
            mProfileViewmodel.lastName.set(response.profileData!!.lastName)
            mProfileViewmodel.mMobileNumber.set(response.profileData!!.mobile)
            mProfileViewmodel.mEmail.set(response.profileData!!.email)
            mCountryId = response.profileData?.countryName?.id
            if (response.profileData!!.gender.equals("MALE")) {
                mViewDataBinding.rbMale.isChecked = true
            } else {
                mViewDataBinding.rbFemale.isChecked = true
            }
            mProfileViewmodel.mCity.set(response.profileData!!.cityName?.cityName!!)
            mProfileViewmodel.mCountry.set(response.profileData!!.countryName!!.CountryName)
            mProfileViewmodel.mCountryCode.set(" +" + response.profileData!!.countryCode)

//            val drawableId = preferenceHelper.getValue(PreferenceKey.COUNTRY_FLAG, 0) as Int
            val drawableId = countryDetail[0].flag
            val dr = ContextCompat.getDrawable(this, drawableId)
            val bitmap = (dr as BitmapDrawable).bitmap
            var width: Int = 0
            var height: Int = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                width = dpsToPixels(this@ProfileActivity, 5)
                height = dpsToPixels(this@ProfileActivity, 5)
            } else {
                width = dpsToPixels(this@ProfileActivity, 15)
                height = dpsToPixels(this@ProfileActivity, 15)
            }
            val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
            mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)

            mProfileViewmodel.mCountryId.set(response.profileData!!.countryName!!.id)
            mProfileViewmodel.mCityId.set(response.profileData!!.cityName?.id)
            mProfileViewmodel.mProfileImage.set(response.profileData!!.picture)

        })


        setOnclickListteners()
    }

    private fun dpsToPixels(activity: Activity, dps: Int): Int {
        val r = activity.resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), r.displayMetrics).toInt()
    }

    override fun goToChangePasswordActivity() {
        openNewActivity(this@ProfileActivity, ChangePasswordActivity::class.java, true)
    }

    fun setOnclickListteners() {
        mViewDataBinding.profileLayout.setOnClickListener {
            checkPermission()
        }
        mViewDataBinding.saveEditprofileBtn.setOnClickListener {

            if (mProfileData?.profileData?.mobile!!.toString().equals(mViewDataBinding.phonenumberRegisterEt.text.toString()) || (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_SMS, 0) == 0)) {
                mMobileNumberFlag = 1//same
                if (localPath?.path != null) {
                    val pictureFile = File(localPath?.path)
                    val requestFile = RequestBody.create(
                            MediaType.parse("*/*"),
                            pictureFile)

                    val fileBody = MultipartBody.Part.createFormData("picture", pictureFile.name, requestFile)
                    mProfileViewmodel.updateProfile(fileBody)
                } else {
                    mProfileViewmodel.updateProfileWithOutImage()
                }
            } else {
                mMobileNumberFlag = 2 // different mobile number
                verifyMobileNumber()
            }
        }
    }

    private fun verifyMobileNumber() {
        /*val intent = Intent(this@ProfileActivity, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,
                AccountKitActivity.ResponseType.CODE) // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        val phoneNumber = PhoneNumber(mProfileViewmodel.mCountryCode.get().toString(), mProfileViewmodel.mMobileNumber.get().toString(), mProfileViewmodel.mCountryCode.get().toString().replace("+", ""))
        configurationBuilder.setInitialPhoneNumber(phoneNumber)
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build())
        startActivityForResult(intent, Constant.APP_REQUEST_CODE)*/

        mProfileViewmodel.loadingProgress.value = true
        mProfileViewmodel.sendOTP()
    }

    private fun checkPermission() {
        Dexter.withActivity(this@ProfileActivity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        CropImage.startPickImageActivity(this@ProfileActivity)
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        //close activity
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        //close activity
                        ViewUtils.showToast(applicationContext, "Unable to perform this action", false)
                        //finish()
                    }

                }).check()
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
                val imageUri = CropImage.getPickImageResultUri(this, data)

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                    }
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri)
                }

            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    mViewDataBinding.profileImage.setImageURI(result.uri)
                    localPath = result.uri
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: ", Toast.LENGTH_LONG).show()
                }
            }
            if (requestCode == Constant.COUNTRYLIST_REQUEST_CODE) {
                setCountry(data)
                mViewDataBinding.cityRegisterEt.isEnabled = true
            }
            if (requestCode == Constant.CITYLIST_REQUEST_CODE) {
                setCity(data)
            }
            if (requestCode == APP_REQUEST_CODE) {
                accountKitOtpVerified(data)
            }
        }


        // handle result of CropImageActivity


    }


    private fun setCountry(data: Intent?) {
        val selectedCountry = data?.extras?.get("selected_list") as? CountryResponseData
        Log.d("countrylist", selectedCountry?.country_name + "")
        city = (selectedCountry?.city as? List<City>)!!
        mViewDataBinding.countryRegisterEt.setText(selectedCountry.country_name)
        mProfileViewmodel.mCountryId.set(selectedCountry.id.toString())
    }

    private fun accountKitOtpVerified(data: Intent?) {
        mMobileNumberFlag = 1
        if (localPath?.path != null) {
            val pictureFile = File(localPath?.path)
            val requestFile = RequestBody.create(
                    MediaType.parse("*/*"),
                    pictureFile)

            val fileBody = MultipartBody.Part.createFormData("picture", pictureFile.name, requestFile)
            mProfileViewmodel.updateProfile(fileBody)
        } else {
            mProfileViewmodel.updateProfileWithOutImage()
        }
    }

    private fun setCity(data: Intent?) {
        val selectedCity = data?.extras?.get("selected_list") as? City
        Log.d("statelist", selectedCity?.city_name + "")
        mViewDataBinding.cityRegisterEt.setText(selectedCity?.city_name)
        mProfileViewmodel.mCityId.set(selectedCity?.id.toString())
    }

    private fun startCropImageActivity(imageUri: Uri) = CropImage.activity(imageUri)
            .setFixAspectRatio(true)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setMultiTouchEnabled(true)
            .start(this)


    override fun profileUpdateValidation(email: String, phonenumber: String, firstname: String
                                         , country: String, city: String): Boolean {

        if (TextUtils.isEmpty(firstname)) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_firstname, false)
            mProfileViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(phonenumber) && ValidationUtils.isMinLength(phonenumber, 6)) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_invalid_phonenumber, false)
            mProfileViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(email) && !ValidationUtils.isValidEmail(email)) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_invalid_email_address, false)
            mProfileViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(country)) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_select_country, false)
            mProfileViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(city)) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_select_city, false)
            mProfileViewmodel.loadingProgress.value = false
            return false
        }
        return true
    }

    override fun goToCityListActivity(countryId: ObservableField<String>) {
        if (TextUtils.isEmpty(countryId.toString())) {
            ViewUtils.showToast(this@ProfileActivity, R.string.error_select_country, false)
        } else {
            val intent = Intent(this@ProfileActivity, CityListActivity::class.java)
            intent.putExtra("selectedfrom", "city")
            intent.putExtra("citylistresponse", cityList as Serializable)
            startActivityForResult(intent, Constant.CITYLIST_REQUEST_CODE)
        }
    }
}
