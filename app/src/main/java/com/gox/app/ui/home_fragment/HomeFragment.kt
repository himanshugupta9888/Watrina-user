package com.gox.app.ui.home_fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gox.app.R
import com.gox.app.adapter.ServiceListAdapter
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.City
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.data.repositary.remote.model.Service
import com.gox.app.databinding.FragmentHomeBinding
import com.gox.app.ui.cityListActivity.CityListActivity
import com.gox.app.ui.services.ServicesActivity
import com.gox.app.ui.viewCouponActivity.ViewCouponActivity
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.setValue
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.ui.restaurantlist_activity.RestaurantListActivity
import com.gox.taximodule.ui.activity.main.TaxiMainActivity
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainActivity
import java.io.Serializable
import kotlin.math.abs


class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeFragmentNavigator {

    lateinit var mViewDataBinding: FragmentHomeBinding
    private val mMenuList: MutableList<HomeMenuResponse.ResponseData.Service> = ArrayList()
    private val mFeaturedServiceList: MutableList<HomeMenuResponse.ResponseData.Service> = ArrayList()
    private val mCoupons: ArrayList<HomeMenuResponse.ResponseData.Promocode> = ArrayList()
    private var cityList: ArrayList<City> = ArrayList()
    private var mServiceAdapter: ServiceListAdapter? = null
    private var mCountryName: String? = null
    private var mCountryId: String? = null
    private var muserId: String? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    var homeFragmentViewModel = HomeFragmentViewModel()
    //   private var offersCouponsAdapter: OffersCouponsAdapter? = null
    private var mhomeOfferAdapter: HomeCouponAdapter? = null
    private lateinit var mostRecentAdapter: MostRecentAdapter

    override fun getLayoutId(): Int = R.layout.fragment_home

    @SuppressLint("SetTextI18n")
    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        homeFragmentViewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        this.mViewDataBinding = mViewDataBinding as FragmentHomeBinding
        this.mViewDataBinding.lifecycleOwner = this
        homeFragmentViewModel.navigator = this
        mViewDataBinding.homefragmentmodel = homeFragmentViewModel

        mhomeOfferAdapter = HomeCouponAdapter(activity!!, mCoupons)
        homeFragmentViewModel.getMenu()
        homeFragmentViewModel.getProfile()
        mostRecentAdapter = MostRecentAdapter()
        mViewDataBinding.mostRecentAdapter = mostRecentAdapter
        mostRecentAdapter.setOnClickListener(onRecentServiceClickListener)
        homeFragmentViewModel.mSuccessResponse.observe(this, Observer {
            if (it != null) {
                if (it.statusCode.equals("200")) {
                    ViewUtils.showToast(activity!!, it.message!!, true)
                    homeFragmentViewModel.getMenu()

                }
            }
        })

        homeFragmentViewModel.getMenuResponse().observe(this, Observer { it ->
            if (it != null) {
                if (it.statusCode == "200") {
                    mMenuList.clear()
                    mCoupons.clear()
                    mMenuList.addAll(it.responseData.services)
                    mCoupons.addAll(it.responseData.promocodes)

                    if (mMenuList.isEmpty()) {
                        mViewDataBinding.servicelistFrghomeRv.visibility = View.GONE
                        //  mViewDataBinding.showmoreFrghomeTv.visibility = View.GONE
                        mViewDataBinding.tvNoServiceFound.visibility = View.VISIBLE
                    } else {
                        mViewDataBinding.servicelistFrghomeRv.visibility = View.VISIBLE
                        // mViewDataBinding.showmoreFrghomeTv.visibility = View.VISIBLE
                        mViewDataBinding.tvNoServiceFound.visibility = View.GONE
                    }

                    mServiceAdapter = ServiceListAdapter(activity, mMenuList)
                    mViewDataBinding.serviceListAdapter = mServiceAdapter

                    if (it.responseData.recentRequests.isNullOrEmpty()) {
                        homeFragmentViewModel.isShownRecent.value = false
                    } else {
                        homeFragmentViewModel.isShownRecent.value = true
                        mostRecentAdapter.setData(it.responseData.recentRequests)
                    }

                    mhomeOfferAdapter = HomeCouponAdapter(activity!!, mCoupons)
                    mViewDataBinding.homeCouponAdapter = mhomeOfferAdapter
                    if (mViewDataBinding.serviceListAdapter!!.itemCount > 0) {
                        mViewDataBinding.serviceListAdapter!!.notifyItemRangeRemoved(0, mMenuList.size)
                        mViewDataBinding.serviceListAdapter!!.notifyItemRangeInserted(0, mMenuList.size)
                    }

                    mhomeOfferAdapter!!.notifyDataSetChanged()
                    mServiceAdapter!!.setOnClickListener(mOnAdapterClickListener)
                    mhomeOfferAdapter!!.setOnClickListener(mOnImageAdapterClickListener)
                }

            }
        })

        homeFragmentViewModel.countryListResponse.observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    try {
                        mViewDataBinding.locationHomefragmentTv.text = homeFragmentViewModel.mProfileResponse.value!!.profileData!!.cityName?.cityName + "," + mCountryName


                        for (i in 0 until it.responseData.size) {
                            if (it.responseData[i].id.toString() == mCountryId) {
                                cityList = it.responseData[i].city as ArrayList<City>
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            }
        })
        mViewDataBinding.rlServiceRoot.tag = SHOW_MORE
        mViewDataBinding.viewPagerCoupons.clipToPadding = false
        mViewDataBinding.viewPagerCoupons.setPadding(40, 0, 40, 0)
        mViewDataBinding.viewPagerCoupons.pageMargin = 20
        mViewDataBinding.viewPagerCoupons.setPageTransformer(false) { page, position ->
            val normalizedPosition = abs(abs(position) - 1)
            page.scaleX = normalizedPosition / 2 + 0.5f
            page.scaleY = normalizedPosition / 2 + 0.5f
        }
        homeFragmentViewModel.mProfileResponse.observe(this, Observer {
            if (it.statusCode == "200") {
                mCountryName = it.profileData?.countryName?.CountryName
                mCountryId = it.profileData?.countryName?.id
                muserId = it.profileData?.id
                Constant.currency = it.profileData!!.currencySymbol.toString()
                preference.setValue(PreferenceKey.CURRENCY, it.profileData!!.currencySymbol)
                preference.setValue(PreferenceKey.WALLET_BALANCE, it.profileData!!.walletBalance.toInt())
                preference.setValue(PreferenceKey.CITY_ID, it.profileData!!.cityName?.id)
                preference.setValue(PreferenceKey.USER_ID, it.profileData!!.id)
                homeFragmentViewModel.getProfileCountryList()
            }
        })
        mViewDataBinding.llServices.setOnClickListener{
            val intent = Intent(activity!!, ServicesActivity::class.java)
            startActivity(intent)
        }
        mViewDataBinding.llTaxi.setOnClickListener{
            val intent = Intent(activity, TaxiMainActivity::class.java)
            intent.putExtra("serviceId", "1")
            startActivity(intent)
        }
        mViewDataBinding.imgRide.setOnClickListener{
            val intent = Intent(activity, TaxiMainActivity::class.java)
            intent.putExtra("serviceId", 1)
            startActivity(intent)
        }
        mViewDataBinding.llAirport.setOnClickListener{
            val intent = Intent(activity, TaxiMainActivity::class.java)
            intent.putExtra("serviceId", 1)
            startActivity(intent)
        }
        mViewDataBinding.llFood.setOnClickListener{
            val intent = Intent(activity, RestaurantListActivity::class.java)
            intent.putExtra("serviceId", 1)
            startActivity(intent)
        }
        mViewDataBinding.imgFood.setOnClickListener{
            val intent = Intent(activity, RestaurantListActivity::class.java)
            intent.putExtra("serviceId", 1)
            startActivity(intent)
        }
        mViewDataBinding.llGrocery.setOnClickListener{
            val intent = Intent(activity, RestaurantListActivity::class.java)
            intent.putExtra("serviceId", 2)
            startActivity(intent)
        }
        mViewDataBinding.llSend.setOnClickListener{
            showDialog()
        }
        mViewDataBinding.llBus.setOnClickListener{
            showDialog()
        }
        mViewDataBinding.llRental.setOnClickListener{
            showDialog()
        }

        mViewDataBinding.locationHomefragmentTv.setOnClickListener {
            val intent = Intent(activity!!, CityListActivity::class.java)
            intent.putExtra("selectedfrom", "city")
            intent.putExtra("citylistresponse", cityList as Serializable)
            startActivityForResult(intent, Constant.CITYLIST_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                Constant.CITYLIST_REQUEST_CODE -> {
                    setCity(data)
                }
            }
        }
    }

    private fun setCity(data: Intent?) {
        val selectedCity = data?.extras?.get("selected_list") as? City
        val cityId = selectedCity!!.id
        mViewDataBinding.locationHomefragmentTv.text = selectedCity?.city_name + ", " + mCountryName
        homeFragmentViewModel.updateCity(cityId)
    }

    private val mOnAdapterClickListener = object : OnClickListener {
        override fun onClick(position: Int) {
            val mMenuModel = mMenuList[position]
            onServiceSelected(mMenuModel)
        }
    }


    private val onRecentServiceClickListener = object : OnClickListener {
        override fun onClick(position: Int) {
            val mMenuModel = homeFragmentViewModel.menuResponse.value?.responseData!!.recentRequests[position]
            onServiceSelected(mMenuModel)
        }
    }

    private fun onServiceSelected(mMenuModel: HomeMenuResponse.ResponseData.Service) {
        if (mMenuModel.service.admin_service == "TRANSPORT") {
            val intent = Intent(activity, TaxiMainActivity::class.java)
            intent.putExtra("serviceId", mMenuModel.menu_type_id)
            startActivity(intent)
        } else if (mMenuModel.service.admin_service.equals("Service", true)) {
            val intent = Intent(activity, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = mMenuModel.menu_type_id
            XuberServiceClass.serviceName = mMenuModel.title
            startActivity(intent)
        } else if (mMenuModel.service.admin_service.equals("ORDER", true)) {
            val intent = Intent(activity, RestaurantListActivity::class.java)
            intent.putExtra("serviceId", mMenuModel.menu_type_id)
            startActivity(intent)

        }
    }
    private fun showDialog() {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_layout)

//        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//        val clearBtn = dialog.findViewById(R.id.clearBtn) as ImageView
////        yesBtn.setOnClickListener {
////            dialog.dismiss()
////        }
//        clearBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }



    private val mOnImageAdapterClickListener = object : OnClickListener {
        override fun onClick(position: Int) {
            val promocodeModel = mCoupons[position]
            val intent = Intent(activity, ViewCouponActivity::class.java)
            intent.putExtra("promocode", promocodeModel as Serializable)
            startActivity(intent)

        }
    }

    override fun showMoreLess() {
        val params: ViewGroup.LayoutParams = mViewDataBinding.rlServiceRoot.layoutParams
        if (mViewDataBinding.rlServiceRoot.tag == SHOW_MORE) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            mViewDataBinding.rlServiceRoot.layoutParams = params
            mViewDataBinding.rlServiceRoot.tag = SHOW_LESS
            mViewDataBinding.btnShowMoreLess.text = getString(R.string.user_show_less)
        } else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = resources.getDimensionPixelSize(R.dimen._200sdp)
            mViewDataBinding.rlServiceRoot.layoutParams = params
            mViewDataBinding.rlServiceRoot.tag = SHOW_MORE
            mViewDataBinding.servicelistFrghomeRv.smoothScrollToPosition(0)
            mViewDataBinding.btnShowMoreLess.text = getString(R.string.user_show_more)
        }
    }

    companion object {
        const val SHOW_LESS = "showLess"
        const val SHOW_MORE = "showMore"
    }
}
