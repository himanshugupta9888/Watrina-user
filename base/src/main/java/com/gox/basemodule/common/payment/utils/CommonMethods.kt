package com.gox.basemodule.common.payment.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommonMethods {
    companion object {
        fun getLocalTimeStamp(dateStr: String): String {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            var calendar: Calendar? = null
            var strDate = ""
            try {
                date = df.parse(dateStr)
                calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar!!.time = date!!
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val day = calendar!!.get(Calendar.DAY_OF_MONTH)
            val strMonth = SimpleDateFormat("MMM").format(calendar.time)
            val year = calendar.get(Calendar.YEAR)

            if (strMonth != null) {
                strDate = Integer.toString(day) + "-" + strMonth + "-" + Integer.toString(year)
            }

            return strDate
        }
    }
}
