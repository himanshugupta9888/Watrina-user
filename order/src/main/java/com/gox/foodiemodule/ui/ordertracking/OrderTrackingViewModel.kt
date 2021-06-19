package com.gox.foodiemodule.ui.ordertracking

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.gox.basemodule.base.BaseViewModel

class OrderTrackingViewModel : BaseViewModel<OrderTrackingNavigator>() {

    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var polyLineSrc = MutableLiveData<LatLng>()
    var polyLineDest = MutableLiveData<LatLng>()

}