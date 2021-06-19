package com.gox.app.data.repositary.remote

import com.gox.app.data.repositary.remote.model.*
import com.gox.app.data.repositary.remote.model.response.ResProfileUpdate
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.model.ResCommonSuccussModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("user/login")
    fun signIn(@FieldMap params: HashMap<String, Any>): Observable<SignInResponse>

    @Multipart
    @POST("user/signup")
    fun signUp(@PartMap params: HashMap<String, RequestBody>,
               @Part image: MultipartBody.Part): Observable<SignupResponse>

    @Multipart
    @POST("user/signup")
    fun signUpWithOutImage(@PartMap params: HashMap<String, RequestBody>): Observable<SignupResponse>

    @Multipart
    @POST("user/send-otp")
    fun sendOTP(@PartMap params: HashMap<String, RequestBody>): Observable<SendOTPResponse>


    @Multipart
    @POST("user/verify-otp")
    fun verifyOTP(@PartMap params: HashMap<String, RequestBody>): Observable<VerifyOTPResponse>

    @FormUrlEncoded
    @POST("user/countries")
    fun countryList(@FieldMap params: HashMap<String, Any>): Observable<CountryListResponse>

    @GET("user/menus")
    fun getMenu(@Header("Authorization") token: String): Observable<HomeMenuResponse>

    @FormUrlEncoded
    @POST("user/social/login")
    fun socialLogin(@FieldMap params: HashMap<String, Any>): Observable<SignInResponse>

    @FormUrlEncoded
    @POST("user/forgot/otp")
    fun forgotPassword(@FieldMap params: HashMap<String, Any>): Observable<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST("user/reset/otp")
    fun resetPassword(@FieldMap params: HashMap<String, Any>): Observable<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST("user/verify")
    fun verifyMobilePhone(@FieldMap params: HashMap<String, Any>): Observable<ResponseBody>

    @GET("user/profile")
    fun getUserProfile(@Header("Authorization") token: String): Observable<ProfileResponse>

    @GET("user/notification")
    fun getnotification(@Header("Authorization") token: String,
                        @QueryMap params: HashMap<String, String>): Observable<NotificationNewResponse>

    @Multipart
    @POST("user/profile")
    fun profileUpdate(@Header("Authorization") token: String,
                      @PartMap params: HashMap<String, RequestBody>,
                      @Part image: MultipartBody.Part): Observable<ResProfileUpdate>

    @Multipart
    @POST("user/profile")
    fun profilewithOutImageUpdate(@Header("Authorization") token: String,
                                  @PartMap params: HashMap<String, RequestBody>): Observable<ResProfileUpdate>

    @FormUrlEncoded
    @POST("base")
    fun baseApiConfig(@FieldMap params: HashMap<String, Any>): Observable<BaseApiResponse>

    @FormUrlEncoded
    @POST("user/password")
    fun changePassword(@Header("Authorization") token: String,
                       @FieldMap params: HashMap<String, Any>): Observable<ResponseData>


    @GET("user/upcoming/trips/transport")
    fun getTransportUpcomingHistory(@Header("Authorization") token: String,
                                    @QueryMap params: HashMap<String, String>): Observable<TransportHistory>

    @GET("user/trips-history/{selcetedservice}")
    fun getTransportHistory(@Header("Authorization") token: String,
                            @Path("selcetedservice") selectedservice: String,
                            @QueryMap params: HashMap<String, String>): Observable<TransportHistory>


    @GET("user/trips-history/{servicetype}/{id}")
    fun getHistoryDetail(@Header("Authorization") token: String,
                         @Path("id") id: String,
                         @Path("servicetype") servicetype: String): Observable<HistoryDetailModel>

    @GET("user/trips-history/{servicetype}/{id}")
    fun getUpcomingHistoryDetail(@Header("Authorization") token: String,
                                 @Path("id") id: String,
                                 @Path("servicetype") servicetype: String): Observable<HistoryDetailModel>

    @GET("user/trips-history/transport")
    fun getServiceHistory(@Header("Authorization") token: String,
                          @QueryMap params: HashMap<String, String>): Observable<TransportHistory>

    @GET("user/{servicename}/dispute")
    fun getDisputeList(@Header("Authorization") token: String,@Path("servicename") servicename: String): Observable<DisputeListModel>

    @FormUrlEncoded
    @POST("user/{servicename}/dispute")
    abstract fun addDispute(@Header("Authorization") token: String,
                            @FieldMap params: HashMap<String, String>,@Path("servicename") servicename: String): Observable<ResponseBody>

    @GET("user/{servicename}/disputestatus/{id}")
    fun getDisputeStatus(@Header("Authorization") token: String,
                         @Path("id") id: String,
                         @Path("servicename") servicename: String): Observable<DisputeStatusModel>

    @FormUrlEncoded
    @POST("user/ride/lostitem")
    fun addLostItem(@Header("Authorization") token: String,
                    @FieldMap params: HashMap<String, String>): Observable<ResponseData>

    @GET("/user/trips-history/{service_type}?limit={limit}&offset={offset}&type={order_type}")
    fun getHistoryList(@Header("Authorization") token: String, @Path("service_type") serviceType: String,
                       @Path("limit") limit: String, @Path("offset") offset: String,
                       @Path("order_type") order_type: String): Observable<HistoryModel>

    @FormUrlEncoded
    @POST("user/city")
    fun updateCity(@Header("Authorization") token: String,
                   @FieldMap params: HashMap<String, Any>): Observable<ResCommonSuccussModel>

    @POST("user/logout")
    fun logout(@Header("Authorization") token: String): Observable<ResCommonSuccussModel>

    @FormUrlEncoded
    @POST("user/{servicetype}/cancel/request")
    fun cancelSchedule(@Header("Authorization") token: String,
                   @Path("servicetype") servicetype: String,
                       @FieldMap params: HashMap<String, String>): Observable<ResCommonSuccessModel>
}
