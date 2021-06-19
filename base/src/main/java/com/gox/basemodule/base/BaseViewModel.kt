package com.gox.basemodule.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonSyntaxException
import com.gox.basemodule.session.SessionManager
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.data.NetworkError
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException

abstract class BaseViewModel<N> : ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private lateinit var mNavigator: WeakReference<N>
    private var liveErrorResponse = MutableLiveData<String>()


    var navigator: N
        get() = mNavigator.get()!!
        set(navigator) {
            this.mNavigator = WeakReference(navigator)
        }

    fun getCompositeDisposable() = compositeDisposable

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun getErrorObservable(): MutableLiveData<String> {
        return liveErrorResponse
    }


    fun getErrorMessage(e: Throwable): String? {
        return when (e) {
            is JsonSyntaxException -> {
                if (BuildConfig.DEBUG)
                    e.message.toString()
                else
                    NetworkError.DATA_EXCEPTION
            }
            is HttpException -> {
                val responseBody = e.response().errorBody()
                if (e.code() == 401 && PreferenceHelper(BaseApplication.baseApplication).getValue(PreferenceKey.ACCESS_TOKEN, "")!! != "") {
                    SessionManager.clearSession()
                }
                getErrorMessage(responseBody!!)
            }
            is SocketTimeoutException -> NetworkError.TIME_OUT
            is IOException -> NetworkError.IO_EXCEPTION
            else -> {
                NetworkError.SERVER_EXCEPTION
            }
        }
    }

    private fun getErrorMessage(responseBody: ResponseBody): String? {
        return try {
            val jsonObject = JSONObject(responseBody.string())
            jsonObject.getString("message")
        } catch (e: Exception) {
            e.message
        }
    }
}
