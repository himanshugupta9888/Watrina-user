package com.gox.xubermodule.utils

import android.app.Activity
import android.util.Log
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ProviderListModel
import com.gox.xubermodule.data.model.SubServiceModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    private val preference = PreferenceHelper(BaseApplication.baseApplication)

    fun getPrice(service: SubServiceModel.ResponseData.ServiceCity, activity: Activity): String {
        var price = (preference.getValue(PreferenceKey.CURRENCY, "$") as String)
        when (service.fare_type) {
            "HOURLY" -> {
                price = price + " " + service.per_mins + " " + activity.getString(R.string.per_hour)
            }
            "FIXED" -> {
                price = price + " " + service.base_fare
            }
            "DISTANCETIME" -> {
                price = price + " " + service.per_miles + " " + activity.getString(R.string.distance_min)
            }
        }
        return price
    }

    fun getPrice(service: ProviderListModel.ResponseData.ProviderService, activity: Activity): String {
        var price = (preference.getValue(PreferenceKey.CURRENCY, "$") as String)
        when (service.fare_type) {
            "HOURLY" -> {
                price = price + " " + service.per_mins + " " + activity.getString(R.string.per_hour)
            }
            "FIXED" -> {
                price = price + " " + service.base_fare
            }
            "DISTANCETIME" -> {
                price = price + " " + service.per_miles + " " + activity.getString(R.string.distance_min)
            }
        }
        return price
    }

    fun getLocalTime(strDate: String, dateFormat: String): Long {
        var timeDiff: Long? = 0
        try {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            var calendar: Calendar? = null
            try {
                date = df.parse(strDate)
                calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.time = date
            } catch (e: ParseException) {
                e.printStackTrace()
                Log.e("error", ":------" + e.message)
            }
            val hoursFormat = SimpleDateFormat("HH:mm:ss")
            val formateDate = Date()
            formateDate.time = calendar!!.timeInMillis
            timeDiff = formateDate.time
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("error", ":------" + e.message)
        }
        return timeDiff!!
    }



}
