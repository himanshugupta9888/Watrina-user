package com.gox.basemodule.repositary

import com.google.gson.JsonObject
import com.gox.basemodule.model.ResAddressListModel
import com.gox.basemodule.model.ResCommonSuccussModel
import com.gox.basemodule.model.ProfileResponse
import com.gox.basemodule.common.payment.model.*
import retrofit2.http.*
import com.gox.basemodule.common.payment.model.AddCardModel
import com.gox.basemodule.common.payment.model.CardListModel
import com.gox.basemodule.common.payment.model.WalletResponse
import com.gox.basemodule.common.payment.model.WalletTransactionList
import com.gox.basemodule.model.WalletTransferModel
import io.reactivex.Observable
import java.util.*

interface BaseWebService {


    @GET("user/card")
    abstract fun getCardList(): Observable<CardListModel>

    @FormUrlEncoded
    @POST("user/add/money")
    abstract fun addWalletMoney(@FieldMap params: HashMap<String, String>): Observable<WalletResponse>

    @GET("user/wallet")
    abstract fun getWalletTransction(@Header("Authorization") token: String,
                                     @QueryMap params: HashMap<String, String>): Observable<WalletTransactionList>

    @FormUrlEncoded
    @POST("user/card")
    abstract fun addCard(@Header("Authorization") token: String,
                         @FieldMap params: HashMap<String, String>): Observable<AddCardModel>

    @DELETE("user/card/{card_id}")
    abstract fun deleteCard(@Header("Authorization") token: String,
                            @Path("card_id") cardid: String): Observable<JsonObject>

    @GET("user/profile")
    fun getUserProfile(): Observable<ProfileResponse>

    @GET("user/chat")
    fun getMessages(@Header("Authorization") token: String, @Query("admin_service") adminService: String, @Query("id") id: Int): Observable<ResMessageModel>

    @FormUrlEncoded
    @POST("user/chat")
    fun sendMessage(@Header("Authorization") token: String,
                             @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccessModel>


    @GET("user/address")
    fun getAddressList(@Header("Authorization") token: String): Observable<ResAddressListModel>

    @DELETE("user/address/{id}")
    fun deleteAddress(@Header("Authorization") token: String,
                      @Path("id") id: String): Observable<ResCommonSuccussModel>

    @FormUrlEncoded
    @POST("user/address/add")
    fun addAddress(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccussModel>

    @FormUrlEncoded
    @POST("user/address/update")
    fun editAddress(@Header("Authorization") token: String,
                    @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccussModel>

    @FormUrlEncoded
    @POST("user/wallet/transfer")
    fun sendWalletMoney(@FieldMap params:HashMap<String,String>):Observable<WalletTransferModel>


}
