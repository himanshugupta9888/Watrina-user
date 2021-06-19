package com.gox.foodiemodule.ui.resturantdetail_activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayoutMediator
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.*
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.AddonsListAdapter
import com.gox.foodiemodule.adapter.MenuItemClickListner
import com.gox.foodiemodule.adapter.MenuItemListAdapter
import com.gox.foodiemodule.data.model.CartMenuItemModel
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.databinding.ActivityRestaurantdetailLayoutBinding
import com.gox.foodiemodule.ui.resturantdetail_activity.custom.CategoryAdapter
import com.gox.foodiemodule.ui.resturantdetail_activity.custom.CategoryFragment
import com.gox.foodiemodule.ui.viewCartActivity.ViewCartActivity
import kotlinx.android.synthetic.main.activity_restaurantdetail_layout.*
import kotlinx.android.synthetic.main.foodie_toolbar_layout.view.*
import java.util.*
import kotlin.collections.ArrayList


class RestaurantDetailActivity : BaseActivity<ActivityRestaurantdetailLayoutBinding>(), ResturantDetailNavigator {

    var showViewCartAlert: Boolean = false
    var cartId: Int = 0
    var addonsId: String = "0"
    lateinit var mViewDataBinding: ActivityRestaurantdetailLayoutBinding
    lateinit var restaurantDetailViewModel: RestaturantDetailViewModel
    private var restaurantsId: String? = null
    var addonsList: ArrayList<Int> = ArrayList<Int>()
    val preference = PreferenceHelper(BaseApplication.baseApplication)
    var products: List<ResturantDetailsModel.ResponseData.Product?>? = listOf()
    var storeType = "Restaurant"
    override fun getLayoutId(): Int = R.layout.activity_restaurantdetail_layout
    var firstTime = false
    var fromcustom = false
    var tabSelectionId=""
    var filter="all"
    var tabselect = false
    var check = 0
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityRestaurantdetailLayoutBinding
        mViewDataBinding.lifecycleOwner = this
        restaurantsId = intent.getStringExtra("restaurantsId")
        preference.setValue("restaurantsId", restaurantsId.toString())
        mViewDataBinding.resturantDetailToolbar.search_resturant_img.visibility = View.GONE;
        mViewDataBinding.resturantDetailToolbar.toolbar_back_img.setOnClickListener { finish() }
        restaurantDetailViewModel = ViewModelProviders
                .of(this).get(RestaturantDetailViewModel::class.java)
        restaurantDetailViewModel.navigator = this
        mViewDataBinding.resturantDetailViewModel = restaurantDetailViewModel
        if(intent!=null && intent.hasExtra("type"))
            restaurantDetailViewModel.storeType.value=intent.getStringExtra("type")
        restaurantDetailViewModel.loadingProgress.observe(this@RestaurantDetailActivity, Observer {
            baseLiveDataLoading.value = it
        })

        restaurantDetailViewModel.resturantDetailResponseCustom.observe(this, Observer {
            hideLoading()
            if (it.responseData != null) {
                fromcustom = true
                setResturantDetails(it.responseData)
            }
        })

        restaurantDetailViewModel.resturantDetailResponse.observe(this@RestaurantDetailActivity,
                Observer<ResturantDetailsModel> {
                    restaurantDetailViewModel.loadingProgress.value = false
                    hideLoading()
                    if (it.responseData != null) {
                        fromcustom = true
                        setResturantDetails(it.responseData)

                        if (!firstTime) {
                            firstTime = true
                            setupViewPager()
                        }

                    }

                })

        restaurantDetailViewModel.cartMenuItemResponse.observe(this@RestaurantDetailActivity,
                Observer<CartMenuItemModel> {
                    Log.d("updatecart", it.responseData!!.store_type)

                    if (!restaurantDetailViewModel.storeType.value.isNullOrEmpty() && restaurantDetailViewModel.storeType.value!!.toUpperCase().equals("OTHER"))
                        restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, filter)
                    else
                        restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, filter)

/*                    if (it.statusCode == "200") {
                        updateViewCart(it.responseData!!)
                    } else {
                        ViewUtils.showToast(this, "Cart not updated", false)
                    }*/
                })

        restaurantDetailViewModel.cartRemoveResponse.observe(this@RestaurantDetailActivity, Observer {
            if (it.statusCode == "200") {

                if (!restaurantDetailViewModel.storeType.value.isNullOrEmpty() && restaurantDetailViewModel.storeType.value!!.toUpperCase().equals("OTHER"))
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, filter)
                else
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, filter)

            } else {
                ViewUtils.showToast(this, "Cart not updated", false)
            }
        })

        if(!restaurantDetailViewModel.storeType.value.isNullOrEmpty()&&restaurantDetailViewModel.storeType.value!!.toUpperCase().equals("OTHER"))
            mViewDataBinding.restaturantTypeSpinner.onItemSelectedListener=null
        else
            mViewDataBinding.restaturantTypeSpinner.onItemSelectedListener=SpinnerClickListener()

        mViewDataBinding.tbCategory.setTabTextColors(ContextCompat.getColor(this!!, R.color.black), ContextCompat.getColor(this!!, R.color.black))
        setTabMargin()

        mViewDataBinding.tbCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (restaurantsId != null){
                    tabSelectionId=""+restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories?.get(tbCategory.selectedTabPosition)?.id
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, "" + restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories?.get(tbCategory.selectedTabPosition)?.id, filter)
                    tabselect = true
                }
                //Toast.makeText(this@RestaurantDetailActivity, "" + tbCategory.selectedTabPosition, Toast.LENGTH_SHORT).show()
            }


        })
    }

    override fun onResume() {
        super.onResume()
        restaurantsId = preference.getValue("restaurantsId", restaurantsId!!.toString()) as String
        firstTime = false
        tabselect = false
        mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.GONE
        restaurantDetailViewModel.getResturantDetails(restaurantsId!!, "", "all")
        /*  if (!restaurantDetailViewModel.storeType.value.isNullOrEmpty() && restaurantDetailViewModel.storeType.value!!.toUpperCase().equals("OTHER"))
              restaurantDetailViewModel.getResturantDetails(restaurantsId!!, "", "")
          else
              restaurantDetailViewModel.getResturantDetails(restaurantsId!!, "", "all")*/
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewCart(cartData: CartMenuItemModel.ResponseData) {
        if (cartData.total_cart!! > 0) {
            mViewDataBinding.viewCartLayout.visibility = View.VISIBLE
            mViewDataBinding.totalItemsCountPriceTv
            mViewDataBinding.totalItemsCountPriceTv.text = " " + cartData.total_cart + getString(R.string.items) +
                    Constant.currency + cartData.total_price

        } else {
            mViewDataBinding.viewCartLayout.visibility = View.GONE
        }
        cartId = cartData.carts!![0]!!.id!!
    }

    @SuppressLint("SetTextI18n")
    private fun setResturantDetails(resturantDetail: ResturantDetailsModel.ResponseData) {

        if (resturantDetail.storetype!!.name!!.contains("Foo", true)) {
            mViewDataBinding.restaturantTypeSpinner.visibility = View.VISIBLE
            mViewDataBinding.etaTimeLay.visibility = View.VISIBLE
            storeType = "Restaurant"
        } else {
            mViewDataBinding.restaturantTypeSpinner.visibility = View.GONE
            mViewDataBinding.etaTimeLay.visibility = View.GONE
            storeType = "Shop"
        }


        if (resturantDetail.usercart!! > 0) {
            mViewDataBinding.viewCartLayout.visibility = View.VISIBLE

            val itemsFound = resources.getQuantityString(R.plurals.numberOfItems, resturantDetail.usercart, resturantDetail.usercart)
            mViewDataBinding.totalItemsCountPriceTv.text = "" + itemsFound + " " + Constant.currency + resturantDetail.totalcartprice


        } else {
            mViewDataBinding.viewCartLayout.visibility = View.GONE
        }

        if (resturantDetail.totalstorecart == 0 && resturantDetail.usercart > 0) {
            showViewCartAlert = true
        }



        mViewDataBinding.restaturantName.text = resturantDetail.store_name
        mViewDataBinding.resturantDetailToolbar.title_toolbar.title = resturantDetail.store_name

        var categoryName = ""
        for (i in resturantDetail.categories!!.indices) {
            if (i == 0)
                categoryName = resturantDetail.categories[i]!!.store_category_name!!
            else
                categoryName = categoryName + ", " + resturantDetail.categories[i]!!.store_category_name
        }
        mViewDataBinding.restaturantCusinetypeTv.text = categoryName

        /*if (resturantDetail.categories?.size!! > 0)
            mViewDataBinding.restaturantCusinetypeTv.text = resturantDetail.categories.get(0)?.store_category_description*/
        mViewDataBinding.etaTimeTv.text = resturantDetail.estimated_delivery_time + " " + getString(R.string.mins)
        mViewDataBinding.ratingTv.text = resturantDetail.rating.toString() + ""
        mViewDataBinding.minamountTv.text = Constant.currency + "" + resturantDetail.offer_min_amount.toString()

        /*  products.clear()
          for (i in 0 until resturantDetail!!.carts!!.size) {


              products.add(resturantDetail.carts!![i]!!.product!!)
              products[i].tot_quantity = resturantDetail.carts[i]!!.quantity
              products[i].cartId = resturantDetail.responseData.carts[i]!!.id

          }*/

        products = resturantDetail.products

        if(fromcustom)
            mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.VISIBLE
        else
            mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.GONE

        if(tabselect)
            mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.VISIBLE

        if(products?.size == 0)
            mViewDataBinding.noDataFound.visibility = View.VISIBLE
        else
            mViewDataBinding.noDataFound.visibility = View.GONE

        val menuItemListAdapter = MenuItemListAdapter(this, resturantDetail, "")
        mViewDataBinding.menuItemListAdapter = menuItemListAdapter
        mViewDataBinding.menuItemListAdapter!!.notifyDataSetChanged()
        menuItemListAdapter.setOnClickListener(mOnMenuItemClickListner)

    }

    override fun goToCartPage() {

        openNewActivity(this, ViewCartActivity::class.java, false)

    }

    private val mOnMenuItemClickListner = object : MenuItemClickListner {
        override fun addFilterType(filterType: String) {

        }


        override fun showAddonLayout(id: Int?, itemCount: Int, itemsaddon: ResturantDetailsModel.ResponseData.Product?
                                     , isContainAddon: Boolean) {
            val isGust=preference.getValue(PreferenceKey.ISGUST,false) as Boolean
            if(isGust) {
                ViewUtils.showAlert(this@RestaurantDetailActivity,R.string.gust_message,object : ViewCallBack.Alert{
                    override fun onPositiveButtonClick(dialog: DialogInterface) {
                        val intent = Intent(this@RestaurantDetailActivity,Class.forName("com.gox.app.ui.signup.SignupActivity"))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("EXIT", true)
                        startActivity(intent)
                        dialog.dismiss()
                        dialog.dismiss()
                    }

                    override fun onNegativeButtonClick(dialog: DialogInterface) {

                    }

                })
            }else{
                if (showViewCartAlert) {
                    mshowViewCartAlert(id!!, cartId, itemCount, addonsId, itemsaddon)
                } else {
                    if (isContainAddon)
                        showAddonBottomSheet(id, itemCount, itemsaddon!!)
                    else
                        restaurantDetailViewModel.addItemToCart(id!!, 0, itemCount, addonsId, 0, 0)
                }

            }


        }

        override fun addToCart(id: Int?, itemCount: Int, cartId: Int?, repeat: Int, customize: Int) {


            val isGust=preference.getValue(PreferenceKey.ISGUST,false) as Boolean
            if(isGust) {
                ViewUtils.showAlert(this@RestaurantDetailActivity, R.string.gust_message,object : ViewCallBack.Alert{
                    override fun onPositiveButtonClick(dialog: DialogInterface) {
                        val intent = Intent(this@RestaurantDetailActivity,Class.forName("com.gox.app.ui.signup.SignupActivity"))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("EXIT", true)
                        startActivity(intent)
                        dialog.dismiss()
                        dialog.dismiss()
                    }

                    override fun onNegativeButtonClick(dialog: DialogInterface) {

                    }

                })
            }else {
                if (showViewCartAlert) {
                    mshowViewCartAlert(id!!, cartId!!, itemCount, addonsId, null)
                } else {
                    restaurantDetailViewModel.addItemToCart(id!!, cartId!!, itemCount, addonsId, repeat, customize)
                }
            }
        }



        override fun removeCart(position: Int) {
            val itemCart = products?.get(position)?.itemcart

            if (itemCart?.size == 1)
                restaurantDetailViewModel.removeCart(products?.get(position)?.itemcart?.get(0)?.id!!)
            else {
                goToCartPage()
            }

        }

        override fun addedAddons(position: Int) {
            Log.d("_D", "itemaddonId" + position)
            addonsList.add(position)
        }

        override fun removedAddons(position: Int) {
            addonsList.remove(position)

        }


    }

    private fun mshowViewCartAlert(id: Int, cartId: Int, itemCount: Int, addonsId: String, itemsaddon: ResturantDetailsModel.ResponseData.Product?) {
        val builder = AlertDialog.Builder(this@RestaurantDetailActivity)

        // Set the alert dialog title
        builder.setTitle("Replace cart item?")

        // Display a message on alert dialog
        builder.setMessage("Your cart contains items from another ${storeType}.Do you want discard the selection?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES") { dialog, which ->

            showViewCartAlert = false
            if (itemsaddon != null) {
                showAddonBottomSheet(id, itemCount, itemsaddon)
            }
            restaurantDetailViewModel.addItemToCart(id, cartId, itemCount, addonsId, 0,0)
        }

        builder.setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()


        }

        val dialog: AlertDialog = builder.create()
        dialog.show()


    }

    @SuppressLint("SetTextI18n")
    private fun showAddonBottomSheet(id: Int?, itemCount: Int, product: ResturantDetailsModel.ResponseData.Product) {
        val inflate = DataBindingUtil.inflate<com.gox.foodiemodule.databinding
        .FoodieAddonsDialogBinding>(LayoutInflater.from(baseContext)
                , com.gox.foodiemodule.R.layout.foodie_addons_dialog, null, false)

        val addonsAdapter = AddonsListAdapter(product.itemsaddon)
        if(product.picture != null)
        ViewUtils.setImageViewGlide(this@RestaurantDetailActivity, inflate.itemImg, product.picture.toString())

        if(product.product_offer != null || product.product_offer != 0.0)
            inflate.itemPriceTv.text = Constant.currency + product.product_offer
        else
            inflate.itemPriceTv.text = Constant.currency + product.item_price
        inflate.itemNameTv.text = product.item_name
        inflate.addonsAdapter = addonsAdapter
        if(product.itemsaddon!!.size > 0)
            inflate.addonText.visibility = View.VISIBLE
        else
            inflate.addonText.visibility = View.GONE
        addonsAdapter.setOnClickListener(mOnMenuItemClickListner)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(inflate.root)
        dialog.setOnCancelListener(
                DialogInterface.OnCancelListener {
                    //When you touch outside of dialog bounds,
                    //the dialog gets canceled and this method executes.
                    restaurantDetailViewModel.addItemToCart(id!!, 0, itemCount, addonsId, 0,0)
                    dialog.dismiss()
                }
        )
        inflate.applyFilter.setOnClickListener {
            addonsId = ""
            if (addonsList.size > 0) {
                for (i in 0 until addonsList.size) {
                    if (i == 0)
                        addonsId = ""+ addonsList[i]
                    else
                        addonsId += "," + addonsList[i]
                }
                addonsList.clear()
            }
            restaurantDetailViewModel.addItemToCart(id!!, 0, itemCount, addonsId, 0,0)
            dialog.dismiss()
        }


        inflate.closeDialogImg.setOnClickListener {
            restaurantDetailViewModel.addItemToCart(id!!, 0, itemCount, addonsId, 0,0)
            dialog.dismiss()

        }

        dialog.show()
    }

    open  inner  class  SpinnerClickListener:AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, "all")
                    filter = "all"
                    tabselect = true
                }

                1 -> {
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, "pure-veg")
                    filter = "pure-veg"
                    tabselect = true
                }

                2 -> {
                    restaurantDetailViewModel.getResturantDetailsNew(restaurantsId!!, tabSelectionId, "non-veg")
                    filter = "non-veg"
                    tabselect = true

                }

            }
        }
    }

    fun setTabMargin() {
        for (i in 0 until mViewDataBinding.tbCategory.tabCount) {
            val tabViewGroup = mViewDataBinding.tbCategory.getChildAt(0) as ViewGroup
            val tabItem = tabViewGroup.getChildAt(i)
            val layoutParam: ViewGroup.MarginLayoutParams = tabItem.layoutParams as ViewGroup.MarginLayoutParams
            if (i == mViewDataBinding.tbCategory.tabCount - 1)
                layoutParam.setMargins(15, 0, 0, 0)
            else
                layoutParam.setMargins(15, 0, 0, 0)
            mViewDataBinding.tbCategory.requestLayout()
        }
    }

    fun setupViewPager() {
        //SetViewPager
        val categoryFragment = Vector<Fragment>()
        if (restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories?.size!! > 0) {
            for (category in restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories!!) {
                var categoryFragmentTemp = CategoryFragment()
                var bundle = Bundle()
                bundle.putString("category_id", "" + category?.id)
                categoryFragmentTemp.arguments = bundle
                categoryFragment.add(categoryFragmentTemp)
            }

        }

        //Set Pager Adapter
        val categoryAdapter = CategoryAdapter(this!! as AppCompatActivity, supportFragmentManager, categoryFragment, mViewDataBinding.lifecycleOwner?.lifecycle!!)
        mViewDataBinding.vpCategory.adapter = categoryAdapter

       /* TabLayoutMediator(mViewDataBinding.tbCategory, mViewDataBinding.vpCategory) { tab, position ->
            when (position) {
                position -> {
                    tab.tag = restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories?.get(position)?.id
                    tab.text = restaurantDetailViewModel.resturantDetailResponse.value?.responseData?.categories?.get(position)?.store_category_name!!.toString()
                }
            }

        }.attach()*/
    }

}
