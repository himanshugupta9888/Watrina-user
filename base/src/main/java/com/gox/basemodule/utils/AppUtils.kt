package com.gox.basemodule.utils

import android.app.ActivityManager
import android.content.Context
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object AppUtils {

    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo[0].topActivity
        if (componentInfo?.packageName == context.packageName) {
            isInBackground = false
        }

        return isInBackground
    }

    fun getNumberFormat(): NumberFormat? {
        val currency: String = PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.CURRENCY,"$").toString()
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
        decimalFormatSymbols.currencySymbol = currency
        numberFormat.decimalFormatSymbols = decimalFormatSymbols
        numberFormat.setMinimumFractionDigits(2)
        return numberFormat
    }

}