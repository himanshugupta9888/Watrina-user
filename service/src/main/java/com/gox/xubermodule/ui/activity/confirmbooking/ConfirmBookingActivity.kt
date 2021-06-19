package com.gox.xubermodule.ui.activity.confirmbooking

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View.*
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.common.payment.managepayment.ManagePaymentActivity
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.SendRequestModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ActivityServiceConfirmBookingBinding
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainActivity
import com.gox.xubermodule.ui.activity.providerdetail.ProviderDetailActivity
import com.gox.xubermodule.ui.activity.serviceflowactivity.ServiceFlowActivity
import com.gox.xubermodule.ui.fragment.coupon.XuberCouponFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_service_confirm_booking.*
import kotlinx.android.synthetic.main.toolbar_service_category.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ConfirmBookingActivity : BaseActivity<ActivityServiceConfirmBookingBinding>(), ConfirmBookingNavigator {

    private lateinit var confirmBookingBinding: ActivityServiceConfirmBookingBinding
    private var confirmBookingViewModel: ConfirmBookingViewModel? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    override fun getLayoutId() = R.layout.activity_service_confirm_booking
    private var mCropImageUri: Uri? = null
    private var mCardId: String? = null
    var promo_id = 0
    private var file: MultipartBody.Part? = null
    private var localPath: Uri? = null
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        confirmBookingBinding = mViewDataBinding as ActivityServiceConfirmBookingBinding
        confirmBookingViewModel = ViewModelProviders.of(this).get(ConfirmBookingViewModel::class.java)
        confirmBookingBinding.confirmBookingViewModel = confirmBookingViewModel
        confirmBookingViewModel?.navigator = this
        ivBack.setOnClickListener { onBackPressed() }
        service_name.text = getString(R.string.confirm_booking)
        setServiceDetaials()
        confirmBookingViewModel!!.getPromoCodesList()
        confirmBookingViewModel?.getSelectedPromo()?.observe(this, Observer {
            if (it != null) {
                apply_coupon.text = it.promoCode
                apply_btn.text = getString(R.string.xuber_remove)
                promo_id = it.id!!
            }
        })


        observeLiveData(confirmBookingViewModel!!.loading) {
            loadingObservable.value = it
        }

        apply_btn.setOnClickListener {
            apply_coupon.text = ""
            apply_btn.text = getString(R.string.apply)
            promo_id = 0
        }

        observeLiveData(confirmBookingViewModel!!.errorResponse) {
            ViewUtils.showToast(this@ConfirmBookingActivity, it, false)
        }

        confirmBookingViewModel?.sendRequestModel?.observe(this, Observer<SendRequestModel> {
            ViewUtils.showToast(this@ConfirmBookingActivity, it.responseData.message, true)
            if (!XuberServiceClass.date.isNullOrEmpty()) {
                XuberServiceClass.date = ""
                XuberServiceClass.time = ""
            }
            if (it.responseData.message.contains("Schedule", true)) {
                val intent = Intent(this, XuberMainActivity::class.java)
                intent.putExtra("finish", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                goToServiceFlowScreen()
            }

        })

        btnChange.setOnClickListener {
            val intent = Intent(this@ConfirmBookingActivity, ActivityCardList::class.java)
            intent.putExtra("activity_result_flag", "1")
            startActivityForResult(intent, Constant.PAYMENT_TYPE_REQUEST_CODE)
        }
        apply_coupon.setOnClickListener {
            val mTaxiCouponFragment = XuberCouponFragment.newInstance()
            mTaxiCouponFragment.show(supportFragmentManager, mTaxiCouponFragment.tag)
        }
        inst_img.setOnClickListener { checkPermission() }
        create_req_btn.setOnClickListener {
            when (inst_lt.visibility) {
                VISIBLE -> {
                    when {
                        no_img_lt.visibility == VISIBLE -> {
                            ViewUtils.showToast(this, getString(R.string.select_img), false)
                            return@setOnClickListener
                        }
                        inst_edt.text.isEmpty() -> {
                            ViewUtils.showToast(this, getString(R.string.enter_inst), false)
                            return@setOnClickListener
                        }
                        else -> createReq(true)
                    }
                }
                GONE -> {
                    createReq(false)
                }
            }
        }
    }

    private fun goToServiceFlowScreen() {
        loadingObservable.value = false
        val intent = Intent(this@ConfirmBookingActivity, ServiceFlowActivity::class.java)
        startActivity(intent);
        finish()
    }

    private fun createReq(inst: Boolean) {
        val cash = "CASH"
        val card = "CARD"

        val hashMap: HashMap<String, RequestBody> = HashMap()
        hashMap["s_latitude"] = (RequestBody.create(MediaType.parse("text/plain"), XuberServiceClass.sourceLat))
        hashMap["s_longitude"] = (RequestBody.create(MediaType.parse("text/plain"), (XuberServiceClass.sourceLng)))
        hashMap["s_address"] = (RequestBody.create(MediaType.parse("text/plain"), (XuberServiceClass.address)))
        hashMap["service_id"] = (RequestBody.create(MediaType.parse("text/plain"), XuberServiceClass.subServiceID.toString()))
        hashMap["payment_mode"] = (RequestBody.create(MediaType.parse("text/plain"), cash))
        if (XuberServiceClass.date.isNotEmpty()) {
            hashMap["schedule_date"] = (RequestBody.create(MediaType.parse("text/plain"), (XuberServiceClass.date)))
            hashMap["schedule_time"] = (RequestBody.create(MediaType.parse("text/plain"), (XuberServiceClass.time)))
        }
        if (tvPaymentDetails.text.toString().equals("CASH")) {
            hashMap["payment_mode"] = (RequestBody.create(MediaType.parse("text/plain"), cash))
        } else {
            hashMap["payment_mode"] = (RequestBody.create(MediaType.parse("text/plain"), card))
        }
        if (wallet_lt.visibility == VISIBLE)
            hashMap["use_wallet"] = (RequestBody.create(MediaType.parse("text/plain")
                    , preference.getValue(PreferenceKey.WALLET_BALANCE, 0).toString()))
        hashMap["id"] = (RequestBody.create(MediaType.parse("text/plain")
                , XuberServiceClass.providerListModel.id))
        //hashMap.put("id",(RequestBody.create(MediaType.parse("text/plain")
        //hashMap.put("id", (RequestBody.create(MediaType.parse("text/plain"),XuberServiceClass.providerListModel.id)
        if (promo_id != 0) {
            hashMap["promocode_id"] = (RequestBody.create(MediaType.parse("text/plain"), promo_id.toString()))
        }
        if (inst) {
            hashMap["allow_description"] = (RequestBody.create(MediaType.parse("text/plain"), (inst_edt.text.toString())))
        }

        hashMap["price"] = (RequestBody.create(MediaType.parse("text/plain"), price_txt.text.toString().replace(preference.getValue(PreferenceKey.CURRENCY
                , "$") as String, "")))


        /*  hashMap.put("price", (price_txt.text.toString().replace(preference.getValue(PreferenceKey.CURRENCY
                  , "$") as String, "")).toDouble())*/

        hashMap["quantity"] = (RequestBody.create(MediaType.parse("text/plain"), (XuberServiceClass.quantity)))
        if (wallet_lt.visibility == VISIBLE && wallet_chk_box.isChecked)
            hashMap["use_wallet"] = (RequestBody.create(MediaType.parse("text/plain"), "1"))
        else
            hashMap["use_wallet"] = (RequestBody.create(MediaType.parse("text/plain"), "0"))
        loadingObservable.value = true
        var file: MultipartBody.Part? = null
        if (localPath != null) {
            val pictureFile = File(localPath?.path)
            val requestFile = RequestBody.create(
                    MediaType.parse("*/*"),
                    pictureFile)
            file = MultipartBody.Part.createFormData("allow_image", pictureFile.name, requestFile)
        }
        println("RRRR :: hashMap = ${hashMap}")
        confirmBookingViewModel?.sendRequest(file, hashMap)
    }

    private fun setServiceDetaials() {
        when (XuberServiceClass.allowQuantity) {
            "1" -> {
                qty_lbl.visibility = VISIBLE
                qty_txt.visibility = VISIBLE
                qty_txt.text = XuberServiceClass.quantity
            }
            else -> {
                qty_lbl.visibility = INVISIBLE
                qty_txt.visibility = INVISIBLE
            }
        }

        name_txt.text = XuberServiceClass.serviceName
        when (XuberServiceClass.providerListModel.fare_type) {
            "HOURLY" -> {
                price_lbl.text = (getString(R.string.price_per_hour))
                price_txt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + (XuberServiceClass.providerListModel.per_mins)
            }
            "FIXED" -> {
                price_lbl.text = (getString(R.string.fixed_price))
                price_txt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + (XuberServiceClass.providerListModel.base_fare)
            }
            "DISTANCETIME" -> {
                price_lbl.text = (getString(R.string.distance_min))
                price_txt.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + (XuberServiceClass.providerListModel.per_miles)
            }
        }
        val amount = preference.getValue(PreferenceKey.WALLET_BALANCE, 0) as Int
        if (amount > 0) {
            wallet_lt.visibility = VISIBLE
            wallet_view.visibility = VISIBLE
            wallet_amount.text = (preference.getValue(PreferenceKey.CURRENCY, "$") as String) + amount.toString()
        }
        when (XuberServiceClass.allowDesc) {
            "1" -> {
                inst_lbl.visibility = VISIBLE
                inst_lt.visibility = VISIBLE
            }
            "0" -> {
                inst_lbl.visibility = GONE
                inst_lt.visibility = GONE
            }
        }
    }

    private fun checkPermission() {
        Dexter.withActivity(this@ConfirmBookingActivity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        CropImage.startPickImageActivity(this@ConfirmBookingActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                Constant.PAYMENT_TYPE_REQUEST_CODE -> {
                    val paymentType = data?.extras?.get("payment_type").toString()
                    val card_id = data?.extras?.get("card_id").toString()
                    if (paymentType.equals("cash", true)) {
                        ivPaymentType.setImageDrawable(getDrawable(R.drawable.ic_xuber_money))
                        tvPaymentDetails.text = paymentType.toString().toUpperCase()

                    } else {
                        ivPaymentType.setImageDrawable(getDrawable(R.drawable.ic_xuber_credit_card))
                        tvPaymentDetails.text = "CARD"
                        mCardId = card_id
                    }
                }
            }
        }
        if (resultCode == Activity.RESULT_OK) {
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
        }
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                inst_img.setImageURI(result.uri)
                no_img_lt.visibility = GONE
                localPath = result.uri
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: ", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setFixAspectRatio(true)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setMultiTouchEnabled(true)
                .start(this)
    }


    override fun onBackPressed() {
        openNewActivity(this, ProviderDetailActivity::class.java, true)
    }

    override fun logout(isTrue: Boolean) {
        ViewUtils.showAlert(this, R.string.gust_message,object : ViewCallBack.Alert{
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                val intent = Intent(this@ConfirmBookingActivity,Class.forName("com.gox.app.ui.signup.SignupActivity"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("EXIT", true)
                startActivity(intent)
                dialog.dismiss()
            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {

            }

        })
    }
}
