package com.gox.basemodule.common.chatmessage

import android.util.Base64
import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.payment.model.ChatSocketResponseModel
import com.gox.basemodule.common.payment.model.ReqChatModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.basemodule.databinding.ActivityChatMainBinding
import com.gox.basemodule.socket.SocketManager
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class ChatActivity : BaseActivity<ActivityChatMainBinding>() {

    private lateinit var mBinding: ActivityChatMainBinding

    private var mViewModel: ChatMainViewModel? = null
    private var mChatSocketResponseList: ArrayList<ChatSocketResponseModel>? = null
    private val reqChatMessageModel = ReqChatModel()
    private var message: String? = null
    private var roomName: String? = null
    private var decodeString: String? = null
    private val SALTKEY = "MQ=="
    val preference = PreferenceHelper(BaseApplication.baseApplication)
    private val userId = preference.getValue(Constant.Chat.USER_ID, 0)
    private val requestId = preference.getValue(Constant.Chat.REQUEST_ID, 0)
    private val providerId = preference.getValue(Constant.Chat.PROVIDER_ID, 0)
    private val userName = preference.getValue(Constant.Chat.USER_NAME, "")
    private val serviceType = preference.getValue(Constant.Chat.ADMIN_SERVICE, "")
    private val providerName = preference.getValue(Constant.Chat.PROVIDER_NAME, "")
    private val adminService = preference.getValue(Constant.Chat.ADMIN_SERVICE, "")

    override fun getLayoutId(): Int = R.layout.activity_chat_main

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mBinding = mViewDataBinding as ActivityChatMainBinding
        mBinding.tvtoolBarText.text = getText(R.string.chat)
        mBinding.ivBack.setOnClickListener { finish() }

        decodeString = String(Base64.decode(SALTKEY, Base64.DEFAULT), charset("UTF-8"))
        mChatSocketResponseList = ArrayList()
        mChatSocketResponseList!!.clear()
        mViewModel = ViewModelProviders.of(this).get(ChatMainViewModel::class.java)
        mViewModel?.getMessages(serviceType!! as String, requestId!! as Int)
        mViewModel?.getMessageResponse?.observe(this, Observer {
            if (it != null) if (it.statusCode == "200") {
                mChatSocketResponseList!!.clear()
                mChatSocketResponseList!!.addAll(it.responseData?.get(0)?.ChatSocketResponseModel!!)
                mBinding.chatAdapter!!.notifyDataSetChanged()
            }
        })

        createRoomName()

        mViewModel?.successResponse?.observe(this, Observer {
            if (it != null) if (it.statusCode == "200") {
//                val chatMessageModel = ChatSocketResponseModel()
//                chatMessageModel.type = reqChatMessageModel.type
//                chatMessageModel.user = reqChatMessageModel.userFirstName
//                chatMessageModel.provider = reqChatMessageModel.providerFirstName
//                chatMessageModel.message = message.toString()
//                mChatSocketResponseList!!.add(chatMessageModel)
//                mBinding.chatAdapter!!.notifyDataSetChanged()
                mBinding.messageInput.text.clear()
            }
        })

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        mBinding.messages.layoutManager = layoutManager
        mBinding.chatAdapter = ChatAdapter(this, mChatSocketResponseList!!)
        mBinding.chatAdapter!!.notifyDataSetChanged()
        mBinding.messages.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                mBinding.messages.postDelayed(Runnable {
                    if (mBinding.messages.adapter!!.itemCount > 0)
                        mBinding.messages.smoothScrollToPosition(
                                mBinding.messages.adapter!!.itemCount - 1)
                }, 100)
            }
        }

        SocketManager.emit(Constant.ROOM_NAME.JOIN_ROOM_NAME, roomName.toString())

        mBinding.sendButton.setOnClickListener {
            if (mBinding.messageInput.text.isNotEmpty()) {
                message = mBinding.messageInput.text.toString()
                reqChatMessageModel.roomName = roomName!!
                reqChatMessageModel.url = Constant.BaseUrl.TAXI_BASE_URL + Constant.CHAT
                reqChatMessageModel.saltKey = SALTKEY
                reqChatMessageModel.requestId = requestId!! as Int
                reqChatMessageModel.adminService = adminService!! as String
                reqChatMessageModel.type = "user"
                reqChatMessageModel.userName = userName!! as String
                reqChatMessageModel.providerName = providerName as String
                reqChatMessageModel.message = message.toString()

                val chatObject = JSONObject(Gson().toJson(reqChatMessageModel))
                SocketManager.emit(Constant.ROOM_NAME.CHATROOM, chatObject)
                mBinding.messageInput.text.clear()
//                mViewModel?.sendMessage(reqChatMessageModel)
            }
        }
        layoutManager.smoothScrollToPosition(mBinding.messages, null,
                mBinding.chatAdapter!!.itemCount);

        SocketManager.onEvent(Constant.ROOM_NAME.ON_MESSAGE_RECEIVE, Emitter.Listener {
            runOnUiThread {
                val chatMessageModel = ChatSocketResponseModel()
                val data1 = it[0] as JSONObject
                try {
                    chatMessageModel.type = data1.getString("type")
                    chatMessageModel.message = data1.getString("message")
                    chatMessageModel.user = data1.getString("user")
                    chatMessageModel.provider = data1.getString("provider")
                    mChatSocketResponseList!!.add(chatMessageModel)
                    mBinding.chatAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e("ChatActivity", e.message.toString())
                }
            }
        })
    }

    private fun createRoomName() {
//        room_1_R<RideId>_U<UserId>_P<ProviderId>_TRANSPORT
        val roomPrefix = "room"
        roomName = roomPrefix + "_" + decodeString + "_R" + requestId +
                "_U" + userId + "_P" + providerId + "_" + adminService
        println("RRR :: roomName = $roomName")
    }
}