package com.gox.app.data.repositary.remote.model

import java.io.Serializable

//BaseApiResponseData
//Appsetting
data class BaseApiResponse(
        val error: List<Any>,
        val message: String,
        val responseData: BaseApiResponseData,
        val statusCode: String,
        val title: String
)

data class BaseApiResponseData(
        val appsetting: Appsetting,
        val base_url: String,
        val services: List<Service>
)

data class Appsetting(
        val android_key: String,
        val cmspage: Cmspage,
        val ios_key: String,
        val languages: List<Language>,
        val otp_verify: Int,
        val send_sms: Int,
        val send_email: Int,
        val ride_otp: Int,
        val order_otp: Int,
        val service_otp: Int,
        val payments: List<Payment>,
        val referral: Int,
        val social_login: Int,
        val demo_mode: Int,
        val provider_negative_balance: Float,
        val supportdetails: SupportDetails
)

data class Language(
        val key: String,
        val name: String
)

data class SupportDetails(
        val contact_email: String,
        val contact_number: List<BaseContactnumber>
)

data class BaseContactnumber(
        val number: String
)

data class Cmspage(
        val cancel: String,
        val help: String,
        val privacypolicy: String,
        val terms: String
)

data class Payment(
        val credentials: List<Credential>,
        val name: String,
        val status: String
)

data class Credential(
        val name: String,
        val value: String
)

data class Service(
        val admin_service: String,
        val base_url: String,
        val display_name: Any,
        val id: Int,
        val status: Int
) : Serializable
