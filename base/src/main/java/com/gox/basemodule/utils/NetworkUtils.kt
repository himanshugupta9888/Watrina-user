package com.gox.basemodule.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return !(activeNetwork == null || !activeNetwork.isConnectedOrConnecting)
    }


}// This class is not publicly instantiable
