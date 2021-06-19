package com.gox.taximodule.data.dto.request

import com.google.gson.annotations.SerializedName

class ReqScheduleRide {
    @SerializedName("ScheduleDate")
    var scheduleDate: String? = null
    @SerializedName("scheduleTime")
    var scheduleTime: String? = null
}