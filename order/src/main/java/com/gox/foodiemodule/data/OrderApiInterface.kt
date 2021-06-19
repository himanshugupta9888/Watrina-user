package com.gox.foodiemodule.data

import com.gox.foodiemodule.data.model.*
import com.gox.foodiemodule.data.model.ResFoodieCommonSuccussModel
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.foodiemodule.data.model.CartMenuItemModel
import com.gox.foodiemodule.data.model.CheckOutModel
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.data.model.ResturantListModel
import io.reactivex.Observable
import retrofit2.http.*

interface OrderApiInterface {


    @GET("user/store/list/{id}")
    fun getResturantList(@Header("Authorization") token: String,
                         @Path("id") id: String,
                         @QueryMap params: HashMap<String, String>): Observable<ResturantListModel>


    @GET("user/store/details/{id}")
    fun getResturantDetails(@Header("Authorization") token: String,
                            @Path("id") id: String,
                            @QueryMap params: HashMap<String, String>): Observable<ResturantDetailsModel>


    @FormUrlEncoded
    @POST("user/store/addcart")
    fun updateCartItem(@Header("Authorization") token: String,
                       @FieldMap params: HashMap<String, Any>): Observable<CartMenuItemModel>


    @FormUrlEncoded
    @POST("user/store/removecart")
    fun removeCart(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, Int>): Observable<CartMenuItemModel>

    @GET("user/store/cartlist")
    fun getCartList(@Query("order_type") order_type:String,@Query("user_address_id") addressID:Int): Observable<CartMenuItemModel>

    @GET("user/store/cartlist")
    fun applyPromoCode(@Header("Authorization") token: String,@Query("promocode_id") promocode_id:Int
    ): Observable<CartMenuItemModel>

    @FormUrlEncoded
    @POST("user/store/checkout")
    fun getCheckOutDetail(@Header("Authorization") token: String,
                          @FieldMap params: HashMap<String, Any>
    ): Observable<CheckOutModel>

    @GET("user/promocode/order")
    fun getPromoCodeList(@Header("Authorization") token: String): Observable<PromoCodeListModel>


    @GET("user/order/search/{id}")
    fun getSearchResturant(@Header("Authorization") token: String,
                           @Path("id") id: String,
                           @QueryMap params: HashMap<String, String>): Observable<ResturantListModel>

    @FormUrlEncoded
    @POST("user/store/checkout")
    fun checkOut(@Header("Authorization") token: String,
                 @FieldMap params: HashMap<String, Any>): Observable<ResCheckCartModel>

    @GET("user/store/order/{id}")
    fun getOrderDetails(@Header("Authorization") token: String,
                        @Path("id") id: Int): Observable<ResOrderDetail>

    @GET("user/store/cusines/{id}")
    fun getCusineList(@Path("id") id: String): Observable<CusineListModel>

    @GET("user/store/check/request")
    fun getCheckRequest(@Header("Authorization") token: String): Observable<OrderCheckRequest>

    @FormUrlEncoded
    @POST("user/store/order/rating")
    fun setRating(@Header("Authorization") token: String,
                  @FieldMap params: HashMap<String, Any>): Observable<ResFoodieCommonSuccussModel>

    @GET("user/reasons/")
    fun getReasons(@Header("Authorization") token: String,
                   @Query("type") type: String): Observable<ResFoodieReasonModel>

    @FormUrlEncoded
    @POST("user/order/cancel/request")
    fun cancelOrder(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>
}
