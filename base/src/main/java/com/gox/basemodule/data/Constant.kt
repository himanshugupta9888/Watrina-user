@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.gox.basemodule.data

import android.Manifest
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import java.nio.charset.Charset

object Constant {
    const val M_TOKEN = "Bearer "
    const val APP_REQUEST_CODE = 99
    const val FILTERTYPE_CODE = 110
    const val STATELIST_REQUEST_CODE = 101
    const val COUNTRYLIST_REQUEST_CODE = 100
    const val CITYLIST_REQUEST_CODE = 102
    const val COUNTRYCODE_PICKER_REQUEST_CODE = 111
    const val PAYMENT_TYPE_REQUEST_CODE = 201
    const val CHANGE_ADDRESS_TYPE_REQUEST_CODE = 202
    const val ADDCARD = 125
    const val CUSTOM_PREFERENCE: String = "BaseConfigSetting"
    const val storetype = "Restaurant"
    const val CHAT = "/chat"
    var currency = "$"
    var walletAmount =0.0

    object ModuleTypes {
        val TRANSPORT = "TRANSPORT"
        val ORDER = "ORDER"
        val SERVICE = "SERVICE"
    }

    object DISPLAY_NAMES {
        val TRANSPORT = "RIDE"
        val ORDER = "ORDER"
        val SERVICE = "SERVICE"
    }

    object PaymentMode {
        val CASH = "cash"
        val CARD = "card"
    }

    object RequestCode {
        val ADDCARD = 125
        val SELECTED_CARD = 1004
        val PERMISSION_CODE_CAMERA = 1003

    }

    object HistoryDisputeAPIType {
        val TRANSPORT = "ride"
        val SERVICES = "service"
        val ORDER ="order"
    }

    object MapConfig {
        val DEFAULT_ZOOM = 15.0f
        val DEFAULT_LOCATION = LatLng(-33.8523341, 151.2106085)
    }

    var isSocketFailed: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }

    object RoomConstants {
        @JvmField
        var COMPANY_ID: String = String(Base64.decode(BuildConfig.SALT_KEY, Base64.DEFAULT), Charset.defaultCharset())
        var CITY_ID: Any? = PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.CITY_ID, "0")
        var TRANSPORT_REQ_ID: Any? = PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.TRANSPORT_REQ_ID, 0)
        var SERVICE_REQ_ID: Any? = PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.SERVICE_REQ_ID, 0)
        var ORDER_REQ_ID: Any? = PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.ORDER_REQ_ID, 0)
    }

    object ROOM_NAME {
        var COMMON_ROOM_NAME: String = "joinCommonRoom"
        var STATUS: String = "socketStatus"
        var NEW_REQ: String = "newRequest"
        var RIDE_REQ: String = "rideRequest"
        var SERVICE_REQ: String = "serveRequest"
        var ORDER_REQ: String = "orderRequest"
        var TRANSPORT_ROOM_NAME: String = "joinPrivateRoom"
        var SERVICE_ROOM_NAME: String = "joinPrivateRoom"
        var ORDER_ROOM_NAME: String = "joinPrivateRoom"
        var JOIN_ROOM_NAME: String = "joinPrivateChatRoom"
        var CHATROOM: String = "send_message"
        var ON_MESSAGE_RECEIVE: String = "new_message"
        var UPDATELOCATION: String = "updateLocation"
    }

    object ROOM_ID {
        @JvmField
        var COMMON_ROOM: String = "room_${Constant.RoomConstants.COMPANY_ID}_${Constant.RoomConstants.CITY_ID}"
        var TRANSPORT_ROOM: String = "room_${Constant.RoomConstants.COMPANY_ID}_R${Constant.RoomConstants.TRANSPORT_REQ_ID}_TRANSPORT"
        var SERVICE_ROOM: String = "room_${Constant.RoomConstants.COMPANY_ID}_R${Constant.RoomConstants.SERVICE_REQ_ID}_SERVICE"
        var ORDER_ROOM: String = "room_${Constant.RoomConstants.COMPANY_ID}_R${Constant.RoomConstants.ORDER_REQ_ID}_ORDER"

        fun getTransportRoom(reqID:Int):String{
            return "room_${RoomConstants.COMPANY_ID}_R${reqID}_TRANSPORT"
        }

        fun getServiceRoom(reqID:Int):String{
            return "room_${RoomConstants.COMPANY_ID}_R${reqID}_SERVICE"
        }

        fun getOrderRoom(reqID:Int):String{
            return "room_${RoomConstants.COMPANY_ID}_R${reqID}_ORDER"
        }
    }

    object BaseUrl {
        @JvmField
        var APP_BASE_URL: String = BaseApplication.getCustomPreference.getString(PreferenceKey.BASE_URL, BuildConfig.BASE_URL)!!
        var TAXI_BASE_URL: String = BaseApplication.getCustomPreference.getString(PreferenceKey.TRANSPORT_URL, BuildConfig.BASE_URL)!!
        var ORDER_BASE_URL: String = BaseApplication.getCustomPreference.getString(PreferenceKey.ORDER_URL, BuildConfig.BASE_URL)!!
        var SERVICE_BASE_URL: String = BaseApplication.getCustomPreference.getString(PreferenceKey.SERVICE_URL, BuildConfig.BASE_URL)!!
    }

    const val TYPE_USER = "user"
    const val TEMP_FILE_NAME = "gojek_user"

    object Chat {
        const val USER_ID = "USER_ID"
        const val REQUEST_ID = "REQUEST_ID"
        const val PROVIDER_ID = "PROVIDER_ID"
        const val USER_NAME = "USER_NAME"
        const val ADMIN_SERVICE = "ADMIN_SERVICE"
        const val PROVIDER_NAME = "PROVIDER_NAME"
    }

    object RequestPermission {
        val PERMISSIONS_LOCATION = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val PERMISSION_CAMERA = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        )
    }

}