package com.gox.basemodule.socket

interface SocketListener{

    interface CallBack{
        fun onConnected()

        fun onDisconnected()

        fun onConnectionError()

        fun onConnectionTimeOut()
    }

    interface connectionRefreshCallBack{
        fun onRefresh()
    }

}