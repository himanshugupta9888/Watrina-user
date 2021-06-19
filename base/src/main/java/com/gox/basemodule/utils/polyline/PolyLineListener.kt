package com.gox.basemodule.utils.polyline

import com.google.android.gms.maps.model.PolylineOptions

interface PolyLineListener {

    fun whenDone(output: PolylineOptions)

    fun getDistanceTime(meters: Double, seconds: Double)

    fun whenFail(statusCode: String)
}