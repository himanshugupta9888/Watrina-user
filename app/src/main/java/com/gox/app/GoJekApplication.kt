package com.gox.app

import android.content.Intent
import android.util.Log
import com.gox.basemodule.session.SessionListener
import com.gox.basemodule.session.SessionManager
import com.gox.basemodule.socket.SocketListener
import com.gox.basemodule.socket.SocketManager
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.BuildConfig
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.clearAll
import com.gox.basemodule.data.Constant
import com.gox.app.ui.onboard.OnBoardActivity
import io.socket.emitter.Emitter

class GoJekApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        SessionManager.instance(object : SessionListener {
            override fun invalidate() {
                logoutApp()
            }

            override fun refresh() {
            }
        })


        SocketManager.setOnConnectionListener(object : SocketListener.CallBack {
            override fun onConnected() {
                SocketManager.onEvent(Constant.ROOM_NAME.STATUS, Emitter.Listener {
                    Log.e("SOCKET","SOCKET_SK status "+it[0])
                })
            }

            override fun onDisconnected() {
                Log.e("SOCKET","SOCKET_SK disconnected")
            }

            override fun onConnectionError() {
                Log.e("SOCKET","SOCKET_SK connection error")
            }

            override fun onConnectionTimeOut() {
                Log.e("SOCKET","SOCKET_SK connection timeout")
            }
        })
        SocketManager.connect(BuildConfig.BASE_URL)
        Log.e("SOCKET","SOCKET_SK connection attempted")

    }

    private fun logoutApp() {
        PreferenceHelper(this).clearAll()
        val newIntent = Intent(applicationContext, OnBoardActivity::class.java)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(newIntent)
    }

}
