package com.gox.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.basemodule.utils.ViewUtils.getTimeDifference
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.NotificationNewResponse
import com.gox.app.databinding.NotificationListitemBinding

class NotificationAdapter(val activity: FragmentActivity?, val notificationResponseData: NotificationNewResponse.ResponseData)
    : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>(), CustomClickListener {

    private lateinit var context:Context;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val inflate = DataBindingUtil.inflate<NotificationListitemBinding>(LayoutInflater.from(parent.context)
                , R.layout.notification_listitem, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
            return notificationResponseData.notification.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.notificationListitemBinding.notificationTimeTv.text = getTimeDifference(notificationResponseData
                .notification.get(position).created_at)
        holder.notificationListitemBinding.titlenotificationListTv.text = notificationResponseData.notification.get(position).title
        holder.notificationListitemBinding.descriptionNotificationTv.text = notificationResponseData.notification.get(position).descriptions
        Glide.with(activity!!).load(notificationResponseData.notification.get(position).image)
                .into(holder.notificationListitemBinding.notificationImg)
    }


    inner class MyViewHolder(itemView: NotificationListitemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val notificationListitemBinding = itemView

        fun bind() {
            notificationListitemBinding.run {
                descriptionNotificationTv.setShowingLine(3)
                descriptionNotificationTv.addShowMoreText(context.getString(R.string.show_more))
                descriptionNotificationTv.addShowLessText(context.getString(R.string.show_less))
                descriptionNotificationTv.setShowMoreColor(ContextCompat.getColor(context,R.color.colorBasePrimary))
                descriptionNotificationTv.setShowLessTextColor(ContextCompat.getColor(context,R.color.colorBasePrimary))
            }
        }


    }

    override fun onListClickListner() {

        Log.d("currentadapter", "onListClickListner")
    }

}