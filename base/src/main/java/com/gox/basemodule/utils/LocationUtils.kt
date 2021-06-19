package com.gox.basemodule.utils

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.text.TextUtils

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

import java.io.IOException
import java.util.ArrayList
import java.util.Locale
import androidx.annotation.RequiresPermission

import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

object LocationUtils {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getLastKnownLocation(context: Context, mCallBack: LocationCallBack.LastKnownLocation) {
        val mFusedLocation = getFusedLocationProviderClient(context)
        val locationResult = mFusedLocation.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null)
                mCallBack.onSuccess(task.result)
        }.addOnFailureListener { e -> mCallBack.onFailure(e.localizedMessage) }
    }

    fun getCurrentAddress(context: Context, currentLocation: LatLng): List<Address> {
        var addresses: List<Address> = ArrayList()
        try {
            if (Geocoder.isPresent())
                addresses = Geocoder(context, Locale.getDefault())
                        .getFromLocation(currentLocation.latitude, currentLocation.longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addresses
    }

    @Throws(IOException::class)
    fun getCountryCode(context: Context, currentLocation: LatLng): String {
        val addresses: List<Address>
        val geocoder: Geocoder
        var countryCode = "PS"
        if (Geocoder.isPresent()) {
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(currentLocation.latitude
                    ,currentLocation.longitude, 1)
            if (!addresses.isNullOrEmpty()) countryCode = addresses[0].countryCode
        }
        countryCode = if (TextUtils.isEmpty(countryCode)) "PS" else countryCode
        return countryCode
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta)))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60.0 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

}
