package com.gox.basemodule.session

object SessionManager {

    private lateinit var listener: SessionListener

    fun instance(listener: SessionListener){
        SessionManager.listener = listener
    }

    fun clearSession(){
        listener.invalidate()
    }


}