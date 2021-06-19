package com.gox.foodiemodule.data

import com.gox.basemodule.data.Constant
import com.gox.basemodule.repositary.BaseRepository
import com.gox.basemodule.utils.ViewUtils
import com.gox.foodiemodule.fragment.ordercancelreason.OrderReasonViewModel
import com.gox.foodiemodule.ui.filter_activity.FilterViewModel
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailViewModel
import com.gox.foodiemodule.ui.restaurantlist_activity.RestaurantListViewModel
import com.gox.foodiemodule.ui.resturantdetail_activity.RestaturantDetailViewModel
import com.gox.foodiemodule.ui.searchResturantActivity.SearchRestaturantsViewModel
import com.gox.foodiemodule.ui.viewCartActivity.ViewCartViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Matcher
import java.util.regex.Pattern

class OrderRepository : BaseRepository() {
    val Http205: Pattern = Pattern.compile("205")

    companion object {
        private var mServiceRepository: OrderRepository? = null

        fun instance(): OrderRepository {
            if (mServiceRepository == null) synchronized(OrderRepository) {
                mServiceRepository = OrderRepository()
            }
            return mServiceRepository!!
        }
    }

    fun getResturantList(viewModel: RestaurantListViewModel, token: String, mServiceID: Any, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getResturantList(token, mServiceID.toString(), params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.resturantListResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getResturantDetail(viewModel: RestaturantDetailViewModel, token: String, mResturantID: String, params: HashMap<String, String>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getResturantDetails(token, mResturantID, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.resturantDetailResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getCartMenuItemQty(viewModel: RestaturantDetailViewModel, token: String
                           , params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .updateCartItem(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartMenuItemResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun removeCart(viewModel: RestaturantDetailViewModel, token: String, params: HashMap<String, Int>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .removeCart(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartRemoveResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getCartList(viewModel: ViewCartViewModel, order_type: String,addressID:Int): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getCartList(order_type,addressID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartListResponse.value = it
                    viewModel.loadingProgress.value = false

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun applyPromoCOde(viewModel: ViewCartViewModel, token: String,promoCode:Int): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .applyPromoCode(token,promoCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartListResponse.value = it
                    viewModel.loadingProgress.value = false

                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }


    fun getCartViewMenuItemQty(viewModel: ViewCartViewModel, token: String
                               , params: HashMap<String, Any>): Disposable {


        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .updateCartItem(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartListResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun removeCartViewList(viewModel: ViewCartViewModel, token: String, params: HashMap<String, Int>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .removeCart(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cartListResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getCheckOutOrderDetail(viewModel: OrderDetailViewModel, token: String, hashMap: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getCheckOutDetail(token, hashMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.checkoutResponseData.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }

    fun getPromoCodeList(viewModel: ViewCartViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getPromoCodeList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.Promo.value = false
                    viewModel.promoCodeResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.promoerror.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getHomePromoCodeList(viewModel: RestaurantListViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL!!, OrderApiInterface::class.java)
                .getPromoCodeList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.promoCodeResponse.value = it
                    viewModel.loadingProgress.value = false
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                            viewModel.loadingProgress.value = false
                        })
    }

    fun getSearchResturantList(viewModel: SearchRestaturantsViewModel, token: String, mResturantID: String, params: HashMap<String, String>): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getSearchResturant(token, mResturantID, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.resturantListResponse.value = it
                },
                        {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        })
    }


    fun getCusineList(viewModel: FilterViewModel,id:String): Disposable {

        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getCusineList(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.cusineListData.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun checkout(viewModel: ViewCartViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .checkOut(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.checkoutResponse.value = it
                }, {
                    if(it!=null){
                        if(validate(it.toString())) {
                            viewModel.cartRefreshResponse.value = "205"
                        } else {
                            viewModel.errorResponse.value = getErrorMessage(it)
                        }
                    }

                })
    }

    fun orderDetail(viewModel: OrderDetailViewModel, token: String, id: Int): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getOrderDetails(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.resOrderDetail.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun setRating(viewModel: OrderDetailViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .setRating(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.getRatingResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun getCheckRequest(viewModel: RestaurantListViewModel, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getCheckRequest(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.checkRequestResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun cancelOrder(viewModel: OrderReasonViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .cancelOrder(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.successResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }
    fun getReasons(viewModel: OrderReasonViewModel, token: String, type: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.ORDER_BASE_URL, OrderApiInterface::class.java)
                .getReasons(token, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.mReasonResponseData.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun validate(errorStr: String?): Boolean {
        val matcher: Matcher = Http205.matcher(errorStr)
        return matcher.find()
    }
}
