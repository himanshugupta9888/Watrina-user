package com.gox.taximodule.data

import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.taximodule.data.dto.CityListModel
import com.gox.taximodule.data.dto.EstimateFareResponse
import com.gox.taximodule.data.dto.response.*
import io.reactivex.Observable
import retrofit2.http.*
import java.util.*

interface TaxiApiInterface {

    @GET("user/transport/services/")
    fun getServices(@Header("Authorization") token: String,
                    @Query("type") id: String,
                    @Query("latitude") latitude: String,
                    @Query("longitude") longitude: String,
                    @Query("city_id") cityID:String):Observable<ServiceResponse>

    @FormUrlEncoded
    @POST("user/transport/estimate")
    fun getEstimate(@Header("Authorization") token: String,
                    @FieldMap params: HashMap<String, String>): Observable<EstimateFareResponse>

    @FormUrlEncoded
    @POST("user/transport/send/request")
    fun startRide(@Header("Authorization") token: String,
                  @FieldMap params: HashMap<String, Any>): Observable<ResRideRequestModel>

    @FormUrlEncoded
    @POST("user/transport/cancel/request")
    fun cancelRide(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, String>): Observable<ResCommonSuccessModel>

    @GET("user/transport/check/request")
    fun checkRequest(@Header("Authorization") token: String): Observable<ResCheckRequest>

    @GET("user/address")
    fun getAddressList(@Header("Authorization") token: String): Observable<FavoriteAddressResponse>

    @FormUrlEncoded
    @POST("user/transport/rate")
    fun setRating(@Header("Authorization") token: String,
                  @FieldMap params: HashMap<String, Any?>): Observable<ResCommonSuccessModel>

    @GET("user/reasons/")
    fun getReasons(@Header("Authorization") token: String,
                   @Query("type") type: String): Observable<ResReasonModel>

    @FormUrlEncoded
    @POST("user/transport/payment")
    fun setPayment(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>


    @GET("user/walletlist/")
    fun getWalletData(@Header("Authorization") token: String): Observable<ResWalletModel>

    @FormUrlEncoded
    @POST("user/transport/extend/trip")
    fun extendTrip(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, String>): Observable<ResCommonSuccessModel>

    @FormUrlEncoded
    @POST("user/transport/update/payment")
    fun updatePayment(@Header("Authorization") token: String,
                      @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>




}