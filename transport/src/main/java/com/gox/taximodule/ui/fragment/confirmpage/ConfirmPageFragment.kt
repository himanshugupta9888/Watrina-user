package com.gox.taximodule.ui.fragment.confirmpage

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.utils.LocationUtils
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.ReqSomeOneModel
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.data.dto.request.ReqScheduleRide
import com.gox.taximodule.databinding.ConfirmPageFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.fragment.bookforsomeone.BookForSomeOneFragment
import com.gox.taximodule.ui.fragment.coupon.TaxiCouponFragment
import com.gox.taximodule.ui.fragment.scheduleride.ScheduleFragment


class ConfirmPageFragment : BaseFragment<ConfirmPageFragmentBinding>(), ConfirmPageNavigator {
    private lateinit var mConfirmPageViewModel: ConfirmPageViewModel
    private lateinit var mConfirmPageFragmentBinding: ConfirmPageFragmentBinding
    private lateinit var mTaxiViewModel: TaxiMainViewModel
    private val reqEstimateModel = ReqEstimateModel()
    private var mSomeOneModel: ReqSomeOneModel? = null
    private var mScheduleDateTime: ReqScheduleRide? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private var mSomeOneName: String? = null
    private var mSomeOnePhone: String? = null
    private var mSomeOneEmail: String? = null
    private var mWalletAmount: String = "0"

    companion object {
        fun newInstance() = ConfirmPageFragment()
    }

    override fun getLayoutId(): Int = R.layout.confirm_page_fragment

    @SuppressLint("SetTextI18n")
    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mConfirmPageFragmentBinding = mViewDataBinding as ConfirmPageFragmentBinding

        mConfirmPageViewModel = ViewModelProviders.of(this).get(ConfirmPageViewModel::class.java)
        mTaxiViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        mConfirmPageViewModel.navigator = this
        observeLiveData(mConfirmPageViewModel.loadingProgress){
            baseLiveDataLoading.value = it
        }
        reqEstimateModel.wheelChair = false
        reqEstimateModel.childSeat = false
        mConfirmPageFragmentBinding.viewModel = mConfirmPageViewModel
        mConfirmPageFragmentBinding.executePendingBindings()
        mTaxiViewModel.getRideTypeId().observe(this, Observer {
            if (it != null) {
                reqEstimateModel.rideTypeId = it.toString()
            }
        })
        reqEstimateModel.sourceLattitude = mTaxiViewModel.sourceLat.get()
        reqEstimateModel.sourceLongitude = mTaxiViewModel.sourceLon.get()
        reqEstimateModel.serviceType = mTaxiViewModel.serviceType.get()
        reqEstimateModel.destinationLatitude = mTaxiViewModel.destinationLat.get()
        reqEstimateModel.destinationLongitude = mTaxiViewModel.destinationLon.get()
        reqEstimateModel.paymentMode = mTaxiViewModel.paymentType.get()
        reqEstimateModel.card_id = mTaxiViewModel.card_id.get()
        val isGustUser= preference.getValue(PreferenceKey.ISGUST,false) as Boolean
        if(isGustUser){
           reqEstimateModel.cityID=preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String
        }

        mTaxiViewModel.getEstimate(reqEstimateModel)
        mConfirmPageFragmentBinding.wheelchairNo.isChecked = true
        mConfirmPageFragmentBinding.childseatNo.isChecked = true
        mConfirmPageViewModel.getWalletData()

        mConfirmPageViewModel.loadingProgress.value = true
        mTaxiViewModel.getEstimateResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mConfirmPageFragmentBinding.tvEstimatedFarePrice.text = Constant.currency + " "+it.responseData.fare.estimated_fare
                mConfirmPageFragmentBinding.tvEtaMins.text = it.responseData.fare.time
                mConfirmPageFragmentBinding.tvVehicleType.text = it.responseData.service.vehicle_name
                if (it.responseData.service.capacity > 3) {
                    mConfirmPageFragmentBinding.endorsmentContainerLl.visibility = View.VISIBLE
                } else {
                    mConfirmPageFragmentBinding.endorsmentContainerLl.visibility = View.GONE
                }
                if (it.responseData.fare.peak == 1) {
                    mConfirmPageFragmentBinding.peakHoursLayout.visibility = View.VISIBLE
                    mConfirmPageFragmentBinding.peakHoursPercentage.text = it.responseData.fare.peak_percentage
                } else {
                    mConfirmPageFragmentBinding.peakHoursLayout.visibility = View.GONE
                }
                mConfirmPageViewModel.loadingProgress.value = false

            }
        })
        mConfirmPageViewModel.getWalletResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                mWalletAmount = it.responseData?.wallet_balance.toString()
                mConfirmPageFragmentBinding.tvWalletAmount.text = Constant.currency + " " + mWalletAmount
                mConfirmPageViewModel.loadingProgress.value = false
            }
        })
        mConfirmPageViewModel.errorResponse.observe(this, Observer {
            ViewUtils.showToast(activity!!, it, false)
            mConfirmPageViewModel.loadingProgress.value = false
        })

        mTaxiViewModel.errorResponse.observe(this, Observer {
            ViewUtils.showToast(activity!!, it, false)
            mConfirmPageViewModel.loadingProgress.value = false
        })

        mConfirmPageViewModel.startRideResonse.observe(this, Observer {
            if (it != null) {
                mConfirmPageViewModel.loadingProgress.value = false

                ViewUtils.showToast(activity!!,it.message,true)
                if((it.message?:"").contains("Schedule",true)){
                    activity?.finish()
                }else{
                    fragmentManager!!.popBackStack()
                }
            }
        })
        /* mConfirmPageViewModel.getRideResponse().observe(this, Observer {
             if (it.statusCode == "200") {
                 // ViewUtils.showNormalToast(activity!!, it.rideSuccessModel?.requestId.toString())
                 //  mTaxiViewModel.setRequestId(it.rideSuccessModel?.requestId!!)
                 *//*  val mSearchFragment: SearchPageFragment = SearchPageFragment.newInstance()
                  replaceFragment(R.id.main_container, mSearchFragment, mSearchFragment.tag, true)*//*


            }
        })*/
        mTaxiViewModel.getSomeOneData().observe(this, Observer {
            if (it != null) {
                mSomeOneModel = it
                if (mSomeOneModel?.phoneNumber != "" && mSomeOneModel?.name != null) {
                    mSomeOneName = mSomeOneModel?.name
                    mSomeOneEmail = mSomeOneModel?.email
                    mConfirmPageFragmentBinding.cbBookSomeOne.text = getString(R.string.BookFor) + " "+ mSomeOneName
                    mSomeOnePhone = mSomeOneModel?.phoneNumber
                    mConfirmPageFragmentBinding.btnEdit.visibility = View.VISIBLE
                    reqEstimateModel.someone = 1
                } else {
                    mConfirmPageFragmentBinding.btnEdit.visibility = View.GONE
                    reqEstimateModel.someone = 0
                }
            } else {
                mConfirmPageFragmentBinding.cbBookSomeOne.isChecked = false
            }
        })
        mTaxiViewModel.getScheduleDateTimeData().observe(this, Observer {
            if (it != null) {
                mScheduleDateTime = it
                mConfirmPageFragmentBinding.btnSchedule.visibility = View.GONE
                mConfirmPageFragmentBinding.scheduleDateView.visibility = View.VISIBLE
                reqEstimateModel.scheduleDate = mScheduleDateTime?.scheduleDate!!
                reqEstimateModel.scheduleTime = mScheduleDateTime?.scheduleTime!!
                mConfirmPageFragmentBinding.scheduleDateTime.text = (getString(R.string.YourScheduleOn) + mScheduleDateTime?.scheduleDate!! + " " + mScheduleDateTime?.scheduleTime!!)
                mConfirmPageFragmentBinding.editSchedule.setOnClickListener {
                    openScheduleUI()
                }
                mConfirmPageFragmentBinding.deleteSchedule.setOnClickListener {
                    ViewUtils.showAlert(activity!!, R.string.taxi_cancel_schedule_ride, object : ViewCallBack.Alert {
                        override fun onPositiveButtonClick(dialog: DialogInterface) {
                            val mScheduleRide = ReqScheduleRide()
                            mScheduleRide.scheduleDate = ""
                            mScheduleRide.scheduleTime = ""
                            mTaxiViewModel.setScheduleDateTime(mScheduleRide)
                            mConfirmPageFragmentBinding.scheduleDateView.visibility = View.GONE
                            mConfirmPageFragmentBinding.btnSchedule.visibility = View.VISIBLE
                            dialog.dismiss()
                        }

                        override fun onNegativeButtonClick(dialog: DialogInterface) {
                            dialog.dismiss()
                        }
                    })
                }


            } else {
                mConfirmPageFragmentBinding.scheduleDateView.visibility = View.GONE
            }
        })

        mTaxiViewModel.getPromoCode().observe(this, Observer {
            if (it != null) {
                reqEstimateModel.promoId = it.id.toString()
                mConfirmPageFragmentBinding.tvViewCoupon.text = it.promo_code
            }
        })

        mConfirmPageFragmentBinding.rgWheelChair.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = mRootView!!.findViewById(checkedId)
            reqEstimateModel.wheelChair = radio.text != getString(R.string.no)

        }
        mConfirmPageFragmentBinding.rgChildSheet.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = mRootView!!.findViewById(checkedId)
            reqEstimateModel.childSeat = radio.text != getString(R.string.no)

        }


    }


    override fun onResume() {
        super.onResume()

        if(view == null) return
        view!!.setFocusableInTouchMode(true);
        view!!.requestFocus();
        view!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {

                return if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    goToHome()
                    true
                } else false
            }
        })
    }

    override fun searchProviders() {
        mConfirmPageViewModel.loadingProgress.value = true
        /*clearFragment()
        val mSearchPageFragment: SearchPageFragment = SearchPageFragment.newInstance()
        replaceFragment(R.id.main_container, mSearchPageFragment, mSearchPageFragment.tag, true)*/
        reqEstimateModel.sourceLattitude = mTaxiViewModel.sourceLat.get()
        reqEstimateModel.sourceLongitude = mTaxiViewModel.sourceLon.get()
        reqEstimateModel.serviceType = mTaxiViewModel.serviceType.get()
        reqEstimateModel.sourceAddress = mTaxiViewModel.addressvalue.get()
        reqEstimateModel.destinationAddress = mTaxiViewModel.destinationaddressvalue.get()
        reqEstimateModel.destinationLatitude = mTaxiViewModel.destinationLat.get()
        reqEstimateModel.destinationLongitude = mTaxiViewModel.destinationLon.get()
        reqEstimateModel.paymentMode = mTaxiViewModel.paymentType.get()
        reqEstimateModel.distance = LocationUtils.distance(mTaxiViewModel.sourceLat.get()!!.toDouble(), mTaxiViewModel.sourceLon.get()!!.toDouble(), mTaxiViewModel.destinationLat.get()!!.toDouble(), mTaxiViewModel.destinationLon.get()!!.toDouble()).toString()
        if (mConfirmPageFragmentBinding.cbUseWalletAmount.isChecked) reqEstimateModel.useWallet = "1"
        else reqEstimateModel.useWallet = "0"

        if (reqEstimateModel.someone == 1) {
            reqEstimateModel.someOneName = mSomeOneName
            reqEstimateModel.someOneMobile = mSomeOnePhone
            reqEstimateModel.someOneEmail = mSomeOneEmail
        }
        mConfirmPageViewModel.startRide(reqEstimateModel)
    }

    override fun openBookSomeOneUI() {
        if (mConfirmPageFragmentBinding.cbBookSomeOne.isChecked) {
            if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_SMS, 0) == 1 || BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_EMAIL, 0) == 1) {
                val mBookForSomeOneFragment = BookForSomeOneFragment.newInstance()
                mBookForSomeOneFragment.isCancelable = true
                mBookForSomeOneFragment.show(baseActivity.supportFragmentManager, mBookForSomeOneFragment.tag)
            } else {
                ViewUtils.showToast(baseActivity, getString(R.string.BookForSomeoneDisabled), false)

            }
        } else {
            mConfirmPageFragmentBinding.cbBookSomeOne.text = getString(R.string.BookForSomeone)
            mConfirmPageFragmentBinding.btnEdit.visibility = View.GONE
            ViewUtils.showToast(baseActivity, getString(R.string.BookForSomeoneDisabled), false)
        }

    }

    override fun openScheduleUI() {
        val mScheduleFragment = ScheduleFragment.newInstance()
        mScheduleFragment.show(baseActivity.supportFragmentManager, mScheduleFragment.tag)
    }

    override fun viewCoupons() {
        val mTaxiCouponFragment = TaxiCouponFragment.newInstance()
        mTaxiCouponFragment.show(baseActivity.supportFragmentManager, mTaxiCouponFragment.tag)

    }


    override fun goToHome() {
        //activity!!.onBackPressed()
        fragmentManager!!.popBackStack()
    }


    override fun isNeedToLogout(isTrue: Boolean) {
        ViewUtils.showAlert(activity!!, R.string.gust_message,object : ViewCallBack.Alert{
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                val intent = Intent(activity,Class.forName("com.gox.app.ui.signup.SignupActivity"))
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
