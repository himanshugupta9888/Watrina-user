package com.gox.foodiemodule.ui.viewCartActivity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.opengl.Visibility
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.cardlist.ActivityCardList
import com.gox.basemodule.common.manage_address.ManageAddressActivity
import com.gox.basemodule.common.manage_address.ManageAddressViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.model.AddressModel
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.MenuItemClickListner
import com.gox.foodiemodule.adapter.OrderCouponAdapter
import com.gox.foodiemodule.adapter.ViewCartMenuItemListAdapter
import com.gox.foodiemodule.data.model.CartMenuItemModel
import com.gox.foodiemodule.data.model.ReqPlaceOrder
import com.gox.foodiemodule.data.model.ResCheckCartModel
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.databinding.ActivityCartPageBinding
import com.gox.foodiemodule.fragment.coupon.OrderCouponFragment
import com.gox.foodiemodule.fragment.coupon.add_note.AddNoteFragment
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.foodie_toolbar_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class ViewCartActivity : BaseActivity<ActivityCartPageBinding>(), ViewCartNavigator {

    private lateinit var mViewDataBinding: ActivityCartPageBinding
    private var products: ArrayList<ResturantDetailsModel.ResponseData.Product> = ArrayList()
    private lateinit var viewCartViewModel: ViewCartViewModel
    private lateinit var mManageAddressViewModel: ManageAddressViewModel
    private var mAddressModel = AddressModel()
    private var addonsId: Int = 0
    private var promoId = 0
    private var paymentMode: String = "CASH"
    private var cardID: String = ""
    private var mUserAddressId: Int? = null
    private var mAddressList: ArrayList<AddressModel>? = null
    private val reqPlaceOrder = ReqPlaceOrder()
    private var menuItemListAdapter: ViewCartMenuItemListAdapter? = null
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    override fun getLayoutId(): Int = R.layout.activity_cart_page
    private var errorMsg = ""
    private var deliveryCharge = 0.0
    private var currency = "$"
    private var totalAmt = 0.0


    @SuppressLint("SetTextI18n")
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityCartPageBinding
        mViewDataBinding.resturantDetailToolbar.title_toolbar.setTitle(R.string.cart)
        mViewDataBinding.resturantDetailToolbar.toolbar_back_img.setOnClickListener {
            finish()
        }
        mViewDataBinding.resturantDetailToolbar.search_resturant_img.visibility = View.GONE
        mViewDataBinding.paymentTypeCartpageTv.text = getString(R.string.cash)
        viewCartViewModel = ViewModelProviders.of(this).get(ViewCartViewModel::class.java)
        mManageAddressViewModel = ViewModelProviders.of(this).get(ManageAddressViewModel::class.java)
        baseLiveDataLoading = viewCartViewModel.loadingProgress
        mManageAddressViewModel.getAddressList()
        mViewDataBinding.viewCartViewModel = viewCartViewModel
        mViewDataBinding.deliveryRb.isChecked = true
        reqPlaceOrder.orderType = "DELIVERY"
        mViewDataBinding.addressLl.visibility = View.VISIBLE
        viewCartViewModel.navigator = this
        mAddressList = ArrayList()
        viewCartViewModel.getPromocodeDetail()
        mManageAddressViewModel.getAddressListResponse().observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    mAddressList!!.addAll(it.addressList!!)
                    if (mAddressList!!.size > 0) {
                        mUserAddressId = mAddressList!![0].id
                        viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), mUserAddressId!!)
                        mViewDataBinding.addressTypeCartpageTv.visibility = View.VISIBLE
                        mViewDataBinding.addressTypeCartpageTv.text = mAddressList!![0].addressType
                        if (mAddressList!![0].landmark == null)
                            mViewDataBinding.tvAddressLine.text = mAddressList!![0].flatNumber.toString() + "," + mAddressList!![0].street.toString()
                        else
                            mViewDataBinding.tvAddressLine.text = mAddressList!![0].flatNumber.toString() + "," + mAddressList!![0].street.toString() + "," + mAddressList!![0].landmark.toString()
                    } else {
                        viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), 0)
                        mViewDataBinding.addressTypeCartpageTv.visibility = View.GONE
                        mViewDataBinding.tvAddressLine.text = getString(R.string.noaddressfoundpleaseaddaddress)
                    }
                }
            }
        })
        mViewDataBinding.deliveryTypeRg.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = this.findViewById(checkedId)
            if (radio.text == "Delivery") {
                reqPlaceOrder.orderType = "DELIVERY"
                mViewDataBinding.addressLl.visibility = View.VISIBLE
                mViewDataBinding.deliveryChargePrice.text = currency + " " + deliveryCharge.toString()
                mViewDataBinding.totalChargeValueTv.text = currency + " " + totalAmt.toString()
            } else {
                reqPlaceOrder.orderType = "TAKEAWAY"
                mViewDataBinding.deliveryChargePrice.text = currency + " 0.0"
                var total = totalAmt - deliveryCharge
                mViewDataBinding.totalChargeValueTv.text = currency + " " + total.toString()
                mViewDataBinding.addressLl.visibility = View.GONE
            }

        }
        mViewDataBinding.cbUseWalletAmount.isChecked = false

        if (mViewDataBinding.cbUseWalletAmount.isChecked) {
            reqPlaceOrder.wallet = "1"
        } else {
            reqPlaceOrder.wallet = "0"
        }

        mViewDataBinding.cbUseWalletAmount.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                reqPlaceOrder.wallet = "1"
            } else {
                reqPlaceOrder.wallet = "0"
            }
        }

        viewCartViewModel.checkoutResponse.observe(this, Observer {
            if (it.statusCode.equals("200"))
                goToOrderDetailPage(it)
            else Toasty.error(this, "" + it.message, Toast.LENGTH_SHORT).show()
        })

        viewCartViewModel.errorResponse.observe(this, Observer {
            if (it != null) {
                ViewUtils.showToast(this, "" + it, false)
            }
        })

        viewCartViewModel.cartRefreshResponse.observe(this, Observer {
            if (it != null) {
                if (it == "205") {
                    /* temp handle*/
                    ViewUtils.showToast(this, "An Item in the cart is not available. Please remove it to continue", false)
                    if (mUserAddressId != null) {
                        viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), mUserAddressId!!)
                    } else {
                        viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), 0)

                    }
                }
            }
        })

        viewCartViewModel.paymentType.observe(this, Observer {
            if (!it.equals("CASH"))
                mViewDataBinding.cbDoorDelivery.visibility = View.VISIBLE
            else
                mViewDataBinding.cbDoorDelivery.visibility = View.GONE
        })

        viewCartViewModel!!.promoCodeResponse.observe(this, Observer {
            if (it.statusCode == "200") {
                viewCartViewModel.Promo.value = false
                if (it.message != "") {
                    ViewUtils.showToast(this, "" + it.message, true)
                } else {
                    //Toast.makeText(this, "Promocode not available", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewCartViewModel.cartListResponse.observe(this, Observer<CartMenuItemModel> {
            products.clear()
            if (it.responseData!!.carts!!.isNotEmpty()) {
                mViewDataBinding.cartEmptyView.visibility = View.GONE
                mViewDataBinding.cartView.visibility = View.VISIBLE
                mViewDataBinding.bottomLayout.visibility = View.VISIBLE
                updateWalletBalance(it.responseData.wallet_balance)
                for (i in it.responseData.carts!!.indices) {
                    products.add(it.responseData.carts[i]!!.product!!)
                    products[i].tot_quantity = it.responseData.carts[i]!!.quantity
                    products[i].cartId = it.responseData.carts[i]!!.id
                    products[i].total_item_price = it.responseData.carts[i]!!.total_item_price
                }
//                menuItemListAdapter = MenuItemListAdapter(this, products, "viewcart")
                menuItemListAdapter = ViewCartMenuItemListAdapter(this, products, "viewcart")
                mViewDataBinding.viewCartmenuItemListAdapter = menuItemListAdapter
                menuItemListAdapter!!.notifyDataSetChanged()
                menuItemListAdapter!!.setOnClickListener(mOnMenuItemClickListner)
                mViewDataBinding.totalChargeValueTv.text = it.responseData.user_currency + " " + it.responseData.payable.toString()
                mViewDataBinding.discountPrice.text = it.responseData.user_currency + " - " + it.responseData.shop_discount.toString()
                mViewDataBinding.deliveryChargePrice.text = it.responseData.user_currency + " " + it.responseData.delivery_charges.toString()
                mViewDataBinding.shopPackageCharge.text = it.responseData.user_currency + " " + it.responseData.shop_package_charge.toString()
                mViewDataBinding.taxCharge.text = it.responseData.user_currency + " " + it.responseData.shop_gst_amount.toString()
                mViewDataBinding.itemTotal.text = it.responseData.user_currency + " " + it.responseData.total_item_price.toString()
                mViewDataBinding.restaturantName.text = it.responseData.carts[0]!!.store!!.store_name
                mViewDataBinding.hotelRatingTv.text = it.responseData.rating
                if (it.responseData.shop_cusines != null)
                    mViewDataBinding.restaturantCusinetypeTv.text = it.responseData.shop_cusines!!.toString()
                deliveryCharge = it.responseData.delivery_charges
                currency = it.responseData.user_currency!!
                totalAmt = it.responseData.payable

                if (viewCartViewModel.selectedPromoCode.value?.id != null) {
                    mViewDataBinding.applyCouponTv.text = viewCartViewModel.selectedPromoCode.value?.maxAmount.toString() + " (PromoCode Applied)"
                } else {
                    if (it.responseData.promocode_amount.toString() != "0.0")
                        mViewDataBinding.applyCouponTv.text = it.responseData.promocode_amount.toString() + " (PromoCode Applied)"
                    else
                        mViewDataBinding.applyCouponTv.text = "Apply Coupon"
                }


                if (it.responseData.carts[0]!!.store!!.picture != null) {
                    ViewUtils.setImageViewGlide(this, mViewDataBinding.resturantImage, it.responseData.carts[0]!!.store!!.picture!!)
                }
            } else {
                mViewDataBinding.cartEmptyView.visibility = View.VISIBLE
                mViewDataBinding.cartView.visibility = View.GONE
                mViewDataBinding.bottomLayout.visibility = View.GONE
                finish()
            }


        })
        mViewDataBinding.changeAddressCartTv.setOnClickListener {

            val intent = Intent(this, ManageAddressActivity::class.java)
            intent.putExtra("changeAddressFlag", "1")
            startActivityForResult(intent, Constant.CHANGE_ADDRESS_TYPE_REQUEST_CODE)
        }
        mViewDataBinding.applyCouponTv.setOnClickListener {
            if (mViewDataBinding.applyCouponTv.text.equals("Apply Coupon")) {
                val mTaxiCouponFragment = OrderCouponFragment.newInstance()
                mTaxiCouponFragment.show(supportFragmentManager, mTaxiCouponFragment.tag)
            } else {
                if (mUserAddressId != null) {
                    viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), mUserAddressId!!)
                } else {
                    viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), 0)

                }
            }
        }
        mViewDataBinding.changePaymentCartTv.setOnClickListener {
            val intent = Intent(this@ViewCartActivity, ActivityCardList::class.java)
            intent.putExtra("activity_result_flag", "1")
            startActivityForResult(intent, Constant.PAYMENT_TYPE_REQUEST_CODE)
        }

        viewCartViewModel.getSelectedPromo().observe(this, Observer {
            if (it != null) {
                promoId = it.id!!
                viewCartViewModel.applyPromoCode(promoId)
            }
        })
        mViewDataBinding.tvAddNote.setOnClickListener {
            val mAddNoteFragment = AddNoteFragment.newInstance()
            mAddNoteFragment.isCancelable = true
            mAddNoteFragment.show(supportFragmentManager, mAddNoteFragment.tag)
        }
    }

    private fun goToOrderDetailPage(it: ResCheckCartModel) {
        val intent = Intent(this@ViewCartActivity, OrderDetailActivity::class.java)
        intent.putExtra("orderId", it.responseData?.id)
        startActivity(intent)
        finish()
    }

    private fun updateWalletBalance(amount: Double) {
        if (amount > 0) {
            mViewDataBinding.walletLayout.visibility = View.VISIBLE
            mViewDataBinding.tvWalletAmount.visibility = View.VISIBLE
            mViewDataBinding.tvWalletAmount.text = amount.toString()
        } else {
            mViewDataBinding.walletLayout.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                Constant.PAYMENT_TYPE_REQUEST_CODE -> {
                    val paymentType = data?.extras?.get("payment_type").toString()
                    cardID = data?.extras?.get("card_id").toString()
                    if (paymentType.toUpperCase(Locale.getDefault()) == (Constant.PaymentMode.CASH).toUpperCase()) {
                        mViewDataBinding.paymentTypeCartpageTv.text = getString(R.string.cash)
                        mViewDataBinding.tvCardNumber.visibility = View.GONE
                        viewCartViewModel.paymentType.value = "CASH"
                    } else {
                        mViewDataBinding.paymentTypeCartpageTv.text = getString(R.string.card)
                        //  mViewDataBinding.tvCardNumber.visibility = View.VISIBLE
                        // mViewDataBinding.tvCardNumber.text = "CARD"
                        viewCartViewModel.paymentType.value = "CARD"
                    }
                }
                Constant.CHANGE_ADDRESS_TYPE_REQUEST_CODE -> {
                    mAddressModel = data?.extras!!.get("address") as AddressModel
                    mUserAddressId = mAddressModel.id
                    viewCartViewModel.getCartList(reqPlaceOrder.orderType.toString(), mUserAddressId!!)

                    mViewDataBinding.addressTypeCartpageTv.visibility = View.VISIBLE
                    mViewDataBinding.addressTypeCartpageTv.text = mAddressModel.addressType

                    if (mAddressModel.landmark == null)
                        mViewDataBinding.tvAddressLine.text = mAddressModel.flatNumber.toString() + "," + mAddressModel.street.toString()
                    else
                        mViewDataBinding.tvAddressLine.text = mAddressModel.flatNumber.toString() + "," + mAddressModel.street.toString() + "," + mAddressModel.landmark.toString()
                    //ViewUtils.showToast(this, "Address Added" + mAddressModel.landmark, false)

                }
            }
        }
    }

    override fun goToOrderPage() {

        reqPlaceOrder.paymentMode = viewCartViewModel.paymentType.value
        reqPlaceOrder.card_id = cardID
        if (viewCartViewModel.selectedPromoCode.value?.id != null)
            reqPlaceOrder.promocodeId = viewCartViewModel.selectedPromoCode.value?.id.toString()
        else
            reqPlaceOrder.promocodeId = ""

//        reqPlaceOrder.wallet = ""
        if (viewCartViewModel.isdoorStep.value == true)
            reqPlaceOrder.isDoorStep = 1
        else
            reqPlaceOrder.isDoorStep = 0


        if (reqPlaceOrder.orderType.equals("DELIVERY", true)) {
            if (mUserAddressId != null) {
                reqPlaceOrder.userAddressId = mUserAddressId
                viewCartViewModel.checkOut(reqPlaceOrder)
            } else {
                ViewUtils.showNormalToast(this, "Select the delivery address")
            }
        } else {
            viewCartViewModel.checkOut(reqPlaceOrder)
        }

//        if (mUserAddressId != null) {
//            reqPlaceOrder.userAddressId = mUserAddressId
//            viewCartViewModel.checkOut(reqPlaceOrder)
//        } else {
//            ViewUtils.showNormalToast(this, "Select the delivery address")
//        }


    }


    private val mOnMenuItemClickListner = object : MenuItemClickListner {
        override fun addFilterType(filterType: String) {

        }

        override fun showAddonLayout(position: Int?, itemCount: Int, itemsaddon: ResturantDetailsModel.ResponseData.Product?, b: Boolean) {

        }

        override fun addedAddons(position: Int) {

        }

        override fun removedAddons(position: Int) {

        }

        override fun addToCart(id: Int?, itemCount: Int, cartId: Int?, repeat: Int, customize: Int) {
            val isGust = preferenceHelper.getValue(PreferenceKey.ISGUST, false) as Boolean
            if (isGust == false) {
                viewCartViewModel.addItemToCart(id!!, cartId!!, itemCount, addonsId, repeat, customize)
            } else {
                ViewUtils.showAlert(this@ViewCartActivity, R.string.gust_message, object : ViewCallBack.Alert {
                    override fun onPositiveButtonClick(dialog: DialogInterface) {
                        dialog.dismiss()
                    }

                    override fun onNegativeButtonClick(dialog: DialogInterface) {

                    }

                })
            }
        }


        override fun removeCart(position: Int) {
            viewCartViewModel.removeCart(products[position].cartId!!)
            menuItemListAdapter!!.notifyDataSetChanged()
        }


    }


    override fun logout(isTrue: Boolean) {
        ViewUtils.showAlert(this, R.string.gust_message, object : ViewCallBack.Alert {
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                val intent = Intent(this@ViewCartActivity, Class.forName("com.gox.app.ui.signup.SignupActivity"))
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("EXIT", true)
                startActivity(intent)
                dialog.dismiss()
            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {

            }

        })
    }
}