package com.gox.basemodule.repositary

interface ApiListener {
    fun onSuccess(successData: Any)
    fun onError(error: Throwable)
}