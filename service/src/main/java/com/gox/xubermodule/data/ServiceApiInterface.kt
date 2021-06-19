package com.gox.xubermodule.data

import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.xubermodule.data.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ServiceApiInterface {

    @GET("user/address")
    fun getAddressList(@Header("Authorization") token: String): Observable<FavoriteAddressResponse>

    @GET("user/service_sub_category/{id}")
    fun getSubCategory(@Header("Authorization") token: String,
                       @Path("id") id: String): Observable<SubCategoryModel>

    @GET("user/services/{category_id}/{seb_category_id}")
    fun getSubService(@Header("Authorization") token: String,
                      @Path("category_id") categoryId: String,
                      @Path("seb_category_id") sebCategoryId: String,
                      @Query("city_id") cityID:String
    ): Observable<SubServiceModel>

    @GET("user/list")
    fun getProviderList(@Header("Authorization") token: String,
                        @QueryMap params: HashMap<String, String>): Observable<ProviderListModel>

    @GET("user/promocode/Service")
    fun getPromoCodeList(@Header("Authorization") token: String): Observable<PromoCodeListModel>

    @GET("user/review/{id}")
    fun getReviewList(@Path("id") id: String, @QueryMap params: HashMap<String, String>,
                      @Header("Authorization") token: String): Observable<ReviewListModel>

    @GET("user/service/check/request")
    fun getCheckRequest(@Header("Authorization") token: String): Observable<ServiceCheckReqModel>

    @Multipart
    @POST("/user/service/send/request")
    fun sendXuberRequest(@Header("Authorization") token: String,
                         @PartMap params: HashMap<String, String>,
                         @Part image: MultipartBody.Part): Observable<SendRequestModel>
    @Multipart
    @POST
    fun sendServiceRequest(@Url url: String, @Header("Authorization") token: String,
                           @PartMap params: HashMap<String, RequestBody>,
                           @Part image: MultipartBody.Part?): Observable<SendRequestModel>

    @FormUrlEncoded
    @POST
    fun sendServiceRequest(@Url url: String, @Header("Authorization") token: String,
                           @FieldMap params: HashMap<String, RequestBody>): Observable<SendRequestModel>

    @FormUrlEncoded
    @POST("user/service/rate")
    fun submitRating(@Header("Authorization") token: String,
                     @FieldMap params: HashMap<String, Any>): Observable<ResponseBody>


    @FormUrlEncoded
    @POST("user/service/cancel/request")
    fun cancelService(@Header("Authorization") token: String,
                      @FieldMap params: java.util.HashMap<String, String>): Observable<ResCommonSuccessModel>


    @GET("user/reasons/")
    fun getReasons(@Header("Authorization") token: String,
                   @Query("type") type: String): Observable<ResReasonModel>

    @FormUrlEncoded
    @POST("user/service/update/payment")
    fun changePayment(@Header("Authorization") token: String,
                      @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>

    @FormUrlEncoded
    @POST("user/service/payment")
    fun setPayment(@Header("Authorization") token: String,
                      @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>
}
