package com.gox.xubermodule.ui.fragment.scheduleride

import com.gox.basemodule.base.BaseViewModel

class ScheduleViewModel : BaseViewModel<ScheduleNavigator>() {

    fun pickDate() = navigator.pickDate()

    fun pickTime() = navigator.pickTime()

    fun scheduleRequest() = navigator.scheduleRequest()
}
