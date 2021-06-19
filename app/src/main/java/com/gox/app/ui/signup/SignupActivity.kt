package com.gox.app.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.*
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.PhoneNumber
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.City
import com.gox.app.data.repositary.remote.model.CountryListResponse
import com.gox.app.data.repositary.remote.model.CountryResponseData
import com.gox.app.data.repositary.remote.model.SignupResponse
import com.gox.app.databinding.ActivitySignupBinding
import com.gox.app.ui.cityListActivity.CityListActivity
import com.gox.app.ui.countryListActivity.CountryListActivity
import com.gox.app.ui.countrypicker.CountryCodeActivity
import com.gox.app.ui.dashboard.UserDashboardActivity
import com.gox.app.ui.signin.SignInActivity
import com.gox.app.ui.splash.SplashActivity
import com.gox.app.ui.terms_conditions.TermsAndConditionsActivity
import com.gox.app.ui.verifyotp.VerifyOTPActivity
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.Constant.APP_REQUEST_CODE
import com.gox.basemodule.data.Constant.CITYLIST_REQUEST_CODE
import com.gox.basemodule.data.Constant.COUNTRYCODE_PICKER_REQUEST_CODE
import com.gox.basemodule.data.Constant.COUNTRYLIST_REQUEST_CODE
import com.gox.basemodule.data.setValue
import com.gox.basemodule.utils.ValidationUtils
import com.gox.basemodule.utils.ViewUtils
import com.gox.basemodule.utils.ViewUtils.convertToBitmap
import kotlinx.android.synthetic.main.activity_signup.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.*
import java.security.MessageDigest
import java.util.*


class SignupActivity : BaseActivity<ActivitySignupBinding>(), SignupNavigator, View.OnFocusChangeListener {

    lateinit var mViewDataBinding: ActivitySignupBinding
    private lateinit var signupViewmodel: SignupViewModel
    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var localPath: Uri? = null
    private var cityList: ArrayList<City> = arrayListOf()
    private var countryCode: String = ""

    private companion object {
        const val REQUEST_GOOGLE_LOGIN = 123
    }


    override fun getLayoutId(): Int = R.layout.activity_signup

    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    override fun initView(mViewDataBinding: ViewDataBinding?) {

        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_signin_server_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            terms_conditions_tv.text = Html.fromHtml(getString(R.string.i_accept_the_following_terms_and_conditions), Html.FROM_HTML_MODE_LEGACY)
        else
            terms_conditions_tv.text = Html.fromHtml(getString(R.string.i_accept_the_following_terms_and_conditions))

        generateHash()
        this.mViewDataBinding = mViewDataBinding as ActivitySignupBinding
        signupViewmodel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        this.mViewDataBinding.signupviewmodel = signupViewmodel
        signupViewmodel.navigator = this
        mViewDataBinding.signupviewmodel = signupViewmodel
        baseLiveDataLoading = signupViewmodel.loadingProgress
//        setDefaultCountry()
        detectDefaultCountry()

        signupViewmodel.countryListResponse.observe(this@SignupActivity, Observer<CountryListResponse> {
            Log.d("_D", "country_code :" + it.responseData[0].country_code)

            val intent = Intent(this@SignupActivity, CountryListActivity::class.java)
            intent.putExtra("selectedfrom", "country")
            intent.putExtra("countrylistresponse", it as Serializable)
            startActivityForResult(intent, COUNTRYLIST_REQUEST_CODE)
        })


        signupViewmodel.sendOTPResponse.observe(this, Observer {
            signupViewmodel.loadingProgress.value = false
            ViewUtils.showToast(this@SignupActivity, getString(R.string.otp_success), true)
            Handler().postDelayed({
                val intent = Intent(this,VerifyOTPActivity::class.java)
                intent.putExtra("country_code",signupViewmodel.country_code.value!!.replace("+",""))
                intent.putExtra("mobile",signupViewmodel.phonenumber.value!!.toString())
                startActivityForResult(intent,APP_REQUEST_CODE)
            },1000)

        })

        signupViewmodel.signupResponse.observe(this@SignupActivity, Observer<SignupResponse> {
            Log.d("_D", it.responseData.access_token)
            signupViewmodel.loadingProgress.value = false
            preferenceHelper.setValue(PreferenceKey.ACCESS_TOKEN, it.responseData.access_token)
            moveToDashBoard()
        })


        signupViewmodel.errorResponse.observe(this@SignupActivity, Observer<String> { error ->
            signupViewmodel.loadingProgress.value = false
            ViewUtils.showToast(this@SignupActivity, error, false)
        })

        this.mViewDataBinding.genderRadiogroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById(checkedId) as RadioButton
            if (checkedRadioButton.isChecked)
                signupViewmodel.gender.set(checkedRadioButton.text.toString())

        }
        this.mViewDataBinding.profileLayout.setOnClickListener {
            checkPermission()
        }

        this.mViewDataBinding.emailidRegisterEt.setOnFocusChangeListener(this@SignupActivity)
        this.mViewDataBinding.phonenumberRegisterEt.setOnFocusChangeListener(this@SignupActivity)

        socialLoginControl()
        referralCodeControl()
    }

    private fun moveToDashBoard() {
        val intent = Intent(this, UserDashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun referralCodeControl() {
        if (BaseApplication.getCustomPreference!!.getString(PreferenceKey.REFERRAL, "1").equals("1"))
            referral_code_input_layout.visibility = View.VISIBLE
        else
            referral_code_input_layout.visibility = View.GONE

    }


    private fun socialLoginControl() {
        if (BaseApplication.getCustomPreference!!.getString(PreferenceKey.SOCIAL_LOGIN, "1").equals("1")) {
            tvLabelSocial.visibility = View.VISIBLE
            llcSocial.visibility = View.VISIBLE
        } else {
            tvLabelSocial.visibility = View.GONE
            llcSocial.visibility = View.GONE
        }
    }


    private fun checkPermission() {
        Dexter.withActivity(this@SignupActivity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        CropImage.startPickImageActivity(this@SignupActivity)

                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        //close activity
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        //close activity
                        ViewUtils.showToast(applicationContext, "Unable to perform this action", false)
                        //  finish()
                    }

                }).check()
    }


    fun setDefaultCountry(){
        val dr = ContextCompat.getDrawable(this, R.drawable.flag_australia as Int)
        val bitmap = (dr as BitmapDrawable).bitmap
        var width:Int=0
        var height:Int=0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            width = dpsToPixels(this@SignupActivity, 28)
            height = dpsToPixels(this@SignupActivity, 28)
        }else{
            width = dpsToPixels(this@SignupActivity, 15)
            height = dpsToPixels(this@SignupActivity, 15)
        }
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
    }
    private fun detectDefaultCountry() {
        val resultIntent = Intent()
//        val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        val countryModel = Country.getCountryByISO(tm.networkCountryIso)
//        if (countryModel == null) {
//            resultIntent.putExtra("countryName", "Australia")
//            resultIntent.putExtra("countryCode", "+61")
//            resultIntent.putExtra("countryFlag", R.drawable.flag_australia)
//        } else {
//            resultIntent.putExtra("countryName", countryModel.name)
//            resultIntent.putExtra("countryCode", countryModel.dialCode)
//            resultIntent.putExtra("countryFlag", countryModel.flag)
//        }

        resultIntent.putExtra("countryName", "Australia")
        resultIntent.putExtra("countryCode", "+61")
        resultIntent.putExtra("countryFlag", R.drawable.flag_australia)
        handleCountryCodePickerResult(resultIntent)
    }
    private fun handleCountryCodePickerResult(data: Intent) {
        mViewDataBinding.countrycodeRegisterEt.setText(data.extras?.get("countryCode").toString())
        val countryFlag = data.extras?.get("countryFlag") as Int
        val dr = ContextCompat.getDrawable(this, countryFlag)
        preferenceHelper.setValue(PreferenceKey.COUNTRY_FLAG, countryFlag)
        val bitmap = (dr as BitmapDrawable).bitmap
        var width: Int = 0
        var height: Int = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            width = dpsToPixels(this@SignupActivity, 8)
            height = dpsToPixels(this@SignupActivity, 8)
        } else {
            width = dpsToPixels(this@SignupActivity, 15)
            height = dpsToPixels(this@SignupActivity, 15)
        }
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
        signupViewmodel.country_code.value = data.extras?.get("countryCode").toString()
    }


    private fun dpsToPixels(activity: Activity, dps: Int): Int    {
        val r = activity.resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), r.displayMetrics).toInt()
    }

    private fun generateHash() {
        try {
            val info = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
            Log.d("_genratehash", e.message)
        }

    }

    //do registration
    override fun signup() {

    }

    // move to signin page
    override fun openSignin() {
        openNewActivity(this@SignupActivity, SignInActivity::class.java, true)
    }

    override fun gotoTermsAndCondition() {
        openNewActivity(this@SignupActivity, TermsAndConditionsActivity::class.java, false)
    }

    override fun signupValidation(
            email: String,
            phonenumber: String,
            firstname: String,
            lastname: String,
            gender: String,
            password: String,
            country: String,
            state: String,
            city: String,
            termsandcondition: Boolean
    ): Boolean {

        if (TextUtils.isEmpty(firstname)) {
            ViewUtils.showToast(this@SignupActivity, R.string.error_firstname, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(lastname)) {
            ViewUtils.showToast(this@SignupActivity, R.string.error_lastname, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(phonenumber) || ValidationUtils.isMinLength(phonenumber, 5)) {
            ViewUtils.showToast(this@SignupActivity, R.string.error_invalid_phonenumber, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(email) && !ValidationUtils.isValidEmail(email)) {
            ViewUtils.showToast(this, R.string.error_invalid_email_address, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(gender)) {
            ViewUtils.showToast(this, R.string.error_gender, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (signupViewmodel.loginby.equals("manual", true) && TextUtils.isEmpty(password) && ValidationUtils.isMinLength(password, 6)) {
            ViewUtils.showToast(this, R.string.error_invalid_password, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(country)) {
            ViewUtils.showToast(this, R.string.error_select_country, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (TextUtils.isEmpty(city)) {
            ViewUtils.showToast(this, R.string.error_select_city, false)
            signupViewmodel.loadingProgress.value = false
            return false
        } else if (!termsandcondition) {
            ViewUtils.showToast(this, R.string.error_accept_terms_and_conditions, false)
            signupViewmodel.loadingProgress.value = false
            return false

        }
        return true

    }


    override fun goToCityListActivity(countryStateId: String) {

        if (TextUtils.isEmpty(countryStateId)) {
            ViewUtils.showToast(this, R.string.error_select_country, false)
        } else {

            val intent = Intent(this, CityListActivity::class.java)
            intent.putExtra("selectedfrom", "cityList")
            intent.putExtra("citylistresponse", cityList as Serializable)
            startActivityForResult(intent, CITYLIST_REQUEST_CODE)
        }

    }


    override fun showDialog() {

        showDialog()
    }

    override fun hideDialog() {

        hideDialog()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                COUNTRYLIST_REQUEST_CODE -> {

                    if (data != null) setCountry(data)
                }

                CITYLIST_REQUEST_CODE -> {
                    setCity(data)
                }
                COUNTRYCODE_PICKER_REQUEST_CODE -> if (data != null) {

                    setCountryCode(data)

                }
                APP_REQUEST_CODE -> {

                    accountKitOtpVerified()


                }
                REQUEST_GOOGLE_LOGIN -> {
                    setGoogleData(data)
                }
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                    val imageUri = CropImage.getPickImageResultUri(this, data)
                    // For API >= 23 we need to check specifically that we have permissions to read external storage.
                    if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                        // request permissions and handle the result in onRequestPermissionsResult()

                        localPath = imageUri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                        }
                    } else {
                        // no permissions required or already grunted, can start crop image activity
                        startCropImageActivity(imageUri)
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        mViewDataBinding.profileImage.setImageURI(result.uri)
                        localPath = result.uri
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                        Log.d("_D_croppingfailed", "" + result.error)

                        //                    Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

    }

    private fun setCountry(data: Intent) {
        val selectedCountry = data.extras?.get("selected_list") as CountryResponseData
        Log.d("countrylist", selectedCountry?.country_name + "")
        mViewDataBinding.countryRegisterEt.setText(selectedCountry?.country_name)
        signupViewmodel.country_id = selectedCountry?.id.toString()
        cityList.clear()
        selectedCountry?.city?.let {
            cityList = it as ArrayList<City>
        }
    }

    private fun setCity(data: Intent?) {
        val selectedCity = data?.extras?.get("selected_list") as? City
        Log.d("statelist", selectedCity?.city_name + "")
        mViewDataBinding.cityRegisterEt.setText(selectedCity?.city_name)
        signupViewmodel.city_id = selectedCity?.id.toString()
    }

    private fun setCountryCode(data: Intent) {
        mViewDataBinding.countrycodeRegisterEt.setText(data.extras?.get("countryCode").toString())
        val dr = ContextCompat.getDrawable(this, data.extras?.get("countryFlag") as Int)
        preferenceHelper.setValue(PreferenceKey.COUNTRY_FLAG, data.extras?.get("countryFlag") as Int)
        val bitmap = (dr as BitmapDrawable).bitmap
        var width:Int=0
        var height:Int=0
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            width = dpsToPixels(this@SignupActivity, 8)
            height = dpsToPixels(this@SignupActivity, 8)
        }else{
            width = dpsToPixels(this@SignupActivity, 15)
            height = dpsToPixels(this@SignupActivity, 15)
        }
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
        mViewDataBinding.countrycodeRegisterEt.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null)
        signupViewmodel.country_code.value = data.extras?.get("countryCode").toString()
        countryCode = data.extras?.get("countryCode").toString()
    }

    private fun accountKitOtpVerified() {
        /*val loginResult: AccountKitLoginResult = data!!.getParcelableExtra(AccountKitLoginResult.RESULT_KEY)
        if (loginResult.getError() != null) {
            Log.d("_D_fbaccountkit", loginResult.getError().toString())
        } else if (loginResult.wasCancelled()) {
            Log.d("_D_fbaccountkit", "Fb login cancelled")
        } else {


        }*/

        signupViewmodel.loadingProgress.value = true

        if (localPath?.path != null) {
            val pictureFile = File(localPath?.path)
            val requestFile = RequestBody.create(
                    MediaType.parse("*/*"),
                    pictureFile)

            val fileBody = MultipartBody.Part.createFormData("picture", pictureFile.name, requestFile)
            signupViewmodel.verifiedPhoneNumber(fileBody)
        } else {
            signupViewmodel.verifiedPhoneNumber(null)
        }
    }

    private fun setGoogleData(data: Intent?) {
        val TAG = "REQUEST_GOOGLE_LOGIN"
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            mViewDataBinding.etRegisterFirstName.setText(account?.givenName)
            mViewDataBinding.etRegisterLastName.setText(account?.familyName)
            mViewDataBinding.emailidRegisterEt.setText(account?.email)
            mViewDataBinding.etRegisterFirstName.setFocusable(false)
            mViewDataBinding.etRegisterLastName.setFocusable(false)
            mViewDataBinding.emailidRegisterEt.setFocusable(false)
            mViewDataBinding.tvLabelSocial.visibility = View.GONE
            mViewDataBinding.llcSocial.visibility = View.GONE
            mViewDataBinding.pwdTextinputlayout.visibility = View.GONE
            val imgUrl = account?.photoUrl.toString()
            signupViewmodel.loginby = "google"
            signupViewmodel.checkPhoneEmailVerify(account?.email!!)
            signupViewmodel.social_uniqueid = account?.id.toString()

            Glide.with(this@SignupActivity)
                    .load(imgUrl)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {

                            val bitmap = convertToBitmap(resource, 80, 80)
                            localPath = Uri.fromFile(convertToFile(bitmap))

                            Log.d("_D_URI", "" + localPath)
                            return false

                        }
                    })
                    .into(mViewDataBinding.profileImage)
            val runnable = {
                try {
                    val scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE
                    val accessToken = GoogleAuthUtil.getToken(applicationContext, Objects.requireNonNull(Objects.requireNonNull(account)!!.account), scope, Bundle())
                    Log.d(TAG, "accessToken:$accessToken")


                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                }
            }
            AsyncTask.execute { run(runnable) }

        } catch (e: ApiException) {
            Log.w(TAG, "signup:failed code = " + e.statusCode)
        }
    }


    override fun openCountryPicker() {
        val intent = Intent(this@SignupActivity, CountryCodeActivity::class.java)
        startActivityForResult(intent, COUNTRYCODE_PICKER_REQUEST_CODE)
    }


    override fun verifyPhoneNumber() {
        if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_SMS, 0) == 1) {
            signupViewmodel.loadingProgress.value = true
            signupViewmodel.sendOTP()
        } else {
            signupViewmodel.loadingProgress.value = true
            accountKitOtpVerified()
        }
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setFixAspectRatio(true)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setMultiTouchEnabled(true)
                .start(this)
    }

    override fun googleSignin() {
        startActivityForResult(mGoogleSignInClient.signInIntent, SignupActivity.REQUEST_GOOGLE_LOGIN)
    }

    override fun facebookSignin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    setFacebookData(loginResult)
                    println("Token received " + loginResult.accessToken.token)


                }

            }

            private fun setFacebookData(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(loginResult.accessToken) { _: JSONObject, graphResponse: GraphResponse ->
                    mViewDataBinding.tvLabelSocial.visibility = View.GONE
                    mViewDataBinding.llcSocial.visibility = View.GONE
                    mViewDataBinding.pwdTextinputlayout.visibility = View.GONE

                    mViewDataBinding.etRegisterFirstName.setText(graphResponse.jsonObject.getString("first_name"))
                    mViewDataBinding.etRegisterLastName.setText(graphResponse.jsonObject.getString("last_name"))
                    if(!graphResponse.jsonObject.getString("email").isNullOrEmpty())
                        mViewDataBinding.emailidRegisterEt.setText(graphResponse.jsonObject.getString("email"))
                    mViewDataBinding.etRegisterFirstName.isFocusable = false
                    mViewDataBinding.etRegisterLastName.isFocusable = false
                    mViewDataBinding.emailidRegisterEt.isFocusable = false
                    signupViewmodel.checkPhoneEmailVerify(graphResponse.jsonObject.getString("email"))

                    signupViewmodel.loginby = "facebook"
                    signupViewmodel.social_uniqueid = graphResponse.jsonObject.getString("id")

        //                    mBinding.emailidRegisterEt.setText(graphResponse.jsonObject.getString("gender"))


                }

                val parameters = Bundle()
//                parameters.putString("fields", "id,first_name,last_name,email");
                parameters.putString("fields", "id,name,email,link,gender,first_name,last_name,verified")
                request.setParameters(parameters);
                request.executeAsync();
            }


            override fun onCancel() {
                Log.d("_D_fb_cancel", "canceled")
            }

            override fun onError(exception: FacebookException) {
                exception.printStackTrace()
                val s = exception.message
                if (exception is FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null)
                        LoginManager.getInstance().logOut()
                } else if (s!!.contains("GraphQLHttpFailureDomain"))
                    ViewUtils.showToast(this@SignupActivity, R.string.fb_session_expired, false)
            }
        })
    }


    fun convertToFile(bitmap: Bitmap): File {
        var f: File = File(getCacheDir(), "temp")
        f.createNewFile()

//Convert bitmap to byte array
        var bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        var bitmapdata = bos.toByteArray()

//write the bytes in file
        var fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return f
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {

        if (v!!.id == R.id.emailid_register_et) {
            if (!signupViewmodel.email.get()!!.isEmpty() && !this.mViewDataBinding.emailidRegisterEt.isFocused) {
                if (!ValidationUtils.isValidEmail(signupViewmodel.email.get()!!)) {
                    ViewUtils.showToast(this@SignupActivity, R.string.error_invalid_email_address, false)
                } else {
                    signupViewmodel.checkPhoneEmailVerify("email")
                }
            }
        } else if (v.id == R.id.phonenumber_register_et) {
            signupViewmodel.checkPhoneEmailVerify("phone")

        }
    }
}
