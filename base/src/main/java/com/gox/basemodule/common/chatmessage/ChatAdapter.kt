package com.gox.basemodule.common.chatmessage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.R
import com.gox.basemodule.common.payment.model.ChatSocketResponseModel

class ChatAdapter(private var mContext: Context, private var mChatSocketResponseList: ArrayList<ChatSocketResponseModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return when (viewType) {
            ChatMessageModel.USER -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_user_msg_layout, viewGroup, false)
                userViewHolder(view)
            }
            ChatMessageModel.PROVIDER -> {
                view = LayoutInflater.from(mContext).inflate(R.layout.row_provider_msg_layout, viewGroup, false)
                providerViewHolder(view)
            }
            else -> {
                Holder(View(mContext))
            }

        }
    }

    private inner class userViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val userMessageTv = itemView.findViewById(R.id.usermsgtv) as TextView
    }

    private inner class providerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val providerMessageTv = itemView.findViewById(R.id.provider_msgtv) as TextView
    }

    private inner class Holder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun getItemCount(): Int {
        return mChatSocketResponseList.size
    }

    override fun getItemViewType(position: Int): Int {
        val model = mChatSocketResponseList[position]
        return if (model.type == "user") {
            ChatMessageModel.USER
        } else if (model.type == "provider") {
            ChatMessageModel.PROVIDER
        } else {
            0
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val model = mChatSocketResponseList[viewHolder.adapterPosition]
        if (viewHolder is userViewHolder) {
            setUserMessage(model.message, viewHolder)
        }
        if (viewHolder is providerViewHolder) {
            setProviderMessage(model.message, viewHolder)
        }
    }

    private fun setUserMessage(message: String?, viewHolder: userViewHolder) {
        viewHolder.userMessageTv.text = message
    }

    private fun setProviderMessage(message: String?, viewHolder: providerViewHolder) {
        viewHolder.providerMessageTv.text = message
    }

}