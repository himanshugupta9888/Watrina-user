package com.gox.foodiemodule.ui.restaurantlist_activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.*
import com.gox.basemodule.data.Constant.storetype
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.CustomPagerAdapter
import com.gox.foodiemodule.adapter.FoodieItemClickListner
import com.gox.foodiemodule.adapter.RestaurantListAdapter
import com.gox.foodiemodule.data.model.PromoCodeListModel
import com.gox.foodiemodule.data.model.PromocodeModel
import com.gox.foodiemodule.data.model.ResturantListModel
import com.gox.foodiemodule.databinding.ActivityRestaurantlistBinding
import com.gox.foodiemodule.ui.filter_activity.FilterActivity
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailActivity
import com.gox.foodiemodule.ui.resturantdetail_activity.RestaurantDetailActivity
import com.gox.foodiemodule.ui.searchResturantActivity.SearchRestaturantsActivity
import kotlinx.android.synthetic.main.activity_orderpage_layout.view.toolbar_back_img
import kotlinx.android.synthetic.main.foodie_toolbar_layout.view.*

class RestaurantListActivity : BaseActivity<ActivityRestaurantlistBinding>() {
    var mServiceID: Int = 1
    lateinit var mViewDataBinding: ActivityRestaurantlistBinding
    lateinit var resturantListViewModel: RestaurantListViewModel
    override fun getLayoutId(): Int = R.layout.activity_restaurantlist
    private var mPromoCodeList: ArrayList<PromocodeModel>? = null
    private var mResturantList: ArrayList<ResturantListModel.ResponseData?>? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    private var storeType:String = "OTHER"
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityRestaurantlistBinding
        mServiceID = intent.getIntExtra("serviceId", 0)
        preferenceHelper.setValue(PreferenceKey.SERVICE_ID,mServiceID.toString())
        mViewDataBinding.homeToolbar.toolbar_back_img.setOnClickListener { finish() }
        mViewDataBinding.homeToolbar.search_resturant_img.setOnClickListener {
            openNewActivity(this, SearchRestaturantsActivity::class.java, false)
        }

        resturantListViewModel = ViewModelProviders.of(this).get(RestaurantListViewModel::class.java)
        mViewDataBinding.resturantlistViewModel = resturantListViewModel
        baseLiveDataLoading = resturantListViewModel.loadingProgress
        mPromoCodeList = ArrayList()
        mResturantList = ArrayList()
        val accessKey=preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN,"") as String

//        if(!accessKey.isNullOrEmpty()){
//            resturantListViewModel.getCheckRequest()
//        }

        resturantListViewModel.getResturantList("", "", mServiceID.toString(),preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)
        resturantListViewModel.getPromocodeDetail()

        resturantListViewModel.checkRequestResponse.observe(this, Observer {
            if (it != null && it.statusCode == "200" && it.responseData!!.data!!.size > 0) {
                val intent = Intent(this@RestaurantListActivity, OrderDetailActivity::class.java)
                intent.putExtra("orderId", it.responseData.data?.get(0)!!.id)
                startActivity(intent)
            }
        })

        resturantListViewModel.errorResponse.observe(this, Observer {
            if (it != null) {
                ViewUtils.showToast(this, it, false)
            }
        })

        resturantListViewModel.resturantListResponse.observe(this@RestaurantListActivity,
                Observer<ResturantListModel> {
                    resturantListViewModel.loadingProgress.value = false
                    hideLoading()
                    if (!it.responseData!!.isEmpty()) {
                        mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.VISIBLE
                        mViewDataBinding.idNoResLayout.visibility = View.GONE
                        storeType = it.responseData[0]!!.storetype!!.category!!
                        preferenceHelper.setValue("storetype",storeType)
                        setResturantList(it.responseData)
                    } else {
                        mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.GONE
                        mViewDataBinding.idNoResLayout.visibility = View.VISIBLE
                        if (storeType.equals("FOOD", true)) {
                            mViewDataBinding.noResturantTxt.text = getString(R.string.no_resturant_found)
                        } else {
                            mViewDataBinding.noResturantTxt.text = getString(R.string.no_shop_found)
                        }
                    }

                    setFilterVisibility(storeType)

                })

        resturantListViewModel.promoCodeResponse.observe(this, Observer<PromoCodeListModel> {
            mPromoCodeList!!.addAll(it.responseData)
            mViewDataBinding.customViewpagerAdapter = CustomPagerAdapter(this, mPromoCodeList!!)
            mViewDataBinding.customViewpagerAdapter!!.notifyDataSetChanged()

            mViewDataBinding.viewPager.setPageTransformer(false, mViewDataBinding.customViewpagerAdapter)
            mViewDataBinding.viewPager.currentItem = CustomPagerAdapter.FIRST_PAGE
            mViewDataBinding.viewPager.offscreenPageLimit = mPromoCodeList!!.size
            mViewDataBinding.viewPager.pageMargin = -220
            mViewDataBinding.pageIndicatorView.count = mPromoCodeList!!.size
        })

        mViewDataBinding.lnrCategoryFilter.setOnClickListener {
            mViewDataBinding.restaturantTypeSpinner.performClick()
        }

        mViewDataBinding.restaturantTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val restaturant_type = getResources().getStringArray(R.array.restaturant_type)
                Log.d("_D", "" + restaturant_type[position])
                if (restaturant_type[position].equals("veg", true)) {
                    resturantListViewModel.getResturantList("", "pure-veg", mServiceID.toString(),preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)
                } else if (restaturant_type[position].equals("non veg", true)) {
                    resturantListViewModel.getResturantList("", "non-veg", mServiceID.toString(),preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)
                } else {
                    resturantListViewModel.getResturantList("", "", mServiceID.toString(),preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)
                }
            }

        }

        mViewDataBinding.pageIndicatorView.setViewPager(mViewDataBinding.viewPager)

        mViewDataBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(p0: Int) {
                        Log.d("VIEWPAGER", "VIEWPAGER" + p0)
                    }

                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                        Log.d("VIEWPAGER", "VIEWPAGER" + p0)
                    }

                    override fun onPageScrollStateChanged(p0: Int) {
                        Log.d("VIEWPAGER", "VIEWPAGER" + p0)
                        when (p0) {
                            ViewPager.SCROLL_STATE_SETTLING -> {
                            }
                            else -> {
                            }
                        }
                    }
        })

        mViewDataBinding.filter.setOnClickListener {
            val intent = Intent(this@RestaurantListActivity, FilterActivity::class.java)
            intent.putExtra("store_id",mServiceID.toString())
            intent.putExtra("store_type",storeType)
            startActivityForResult(intent, Constant.FILTERTYPE_CODE)
        }
    }

    private fun setResturantList(resturantList: List<ResturantListModel.ResponseData?>) {
        mResturantList!!.clear()
        mResturantList!!.addAll(resturantList)
        mViewDataBinding.homeToolbar.title_toolbar.title = "Watirna-Food"
        val resturatAdapter = RestaurantListAdapter(this, mResturantList!!, "store")
        mViewDataBinding.resturantsListAdapter = resturatAdapter
        resturatAdapter.setOnClickListener(mOnAdapterClickListener)
        resturatAdapter.notifyDataSetChanged()
    }

    private fun setFilterVisibility(storeType:String) {
        if (storeType.equals("FOOD", true)) {
            mViewDataBinding.lnrCategoryFilter.visibility = View.VISIBLE
        } else {
            mViewDataBinding.lnrCategoryFilter.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.FILTERTYPE_CODE) {

            if (data != null) {
                Log.d("_D_FILTER", data.extras?.get("selected_cusine_list").toString())
                Log.d("_D_FILTER", data.extras?.get("selected_filter").toString())
                resturantListViewModel.getResturantList(data.extras?.get("selected_cusine_list").toString(),
                        data.extras?.get("selected_filter").toString(), mServiceID.toString(),preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)

            }

        } else {
            loadingObservable.value = false
        }

    }

    private val mOnAdapterClickListener = object : FoodieItemClickListner {
        override fun restutantItemClick(position: Int) {

            val resturantList = mResturantList!![position]
            if (!resturantList!!.shopstatus.equals("closed", true)) {
                val intent = Intent(this@RestaurantListActivity, RestaurantDetailActivity::class.java)
                intent.putExtra("restaurantsId", resturantList.id.toString())
                if(resturantList.storetype!=null)
                    intent.putExtra("type",resturantList.storetype!!.category)
                startActivity(intent)
            }
        }
    }

}