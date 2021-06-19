package com.gox.app.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.TransportResponseData
import com.gox.app.databinding.UpcomingOderItemlistBinding
import com.gox.app.ui.historydetailactivity.HistoryDetailActivity
import com.gox.app.utils.CommanMethods
import com.gox.basemodule.data.Constant

class UpcomingOrdersAdapter(val activity: FragmentActivity?, val transportHistory: TransportResponseData,val servicetype: String)
    : RecyclerView.Adapter<UpcomingOrdersAdapter.MyViewHolder>(), CustomClickListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<UpcomingOderItemlistBinding>(LayoutInflater.from(parent.context)
                , R.layout.upcoming_oder_itemlist, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int =  when (servicetype) {
        Constant.ModuleTypes.TRANSPORT -> transportHistory.transport.size
        Constant.ModuleTypes.SERVICE -> transportHistory.service.size
        Constant.ModuleTypes.ORDER -> transportHistory.order.size
        else -> 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        when (servicetype) {
            Constant.ModuleTypes.TRANSPORT -> {
                holder.upcomingOderItemlistBinding.titleUpcomingListTv.text = (transportHistory.transport[position].booking_id)
                holder.upcomingOderItemlistBinding.orderedItemTv.text = transportHistory.transport[position].ride?.vehicle_name
                holder.upcomingOderItemlistBinding.dateUpcomingListTv.text = (CommanMethods.getLocalTimeStamp(transportHistory.transport[position]
                        .assigned_at, "Req_Date_Month") + "")
                holder.upcomingOderItemlistBinding.timeUpcomingListTv.text = (CommanMethods.getLocalTimeStamp(transportHistory.transport[position]
                        .assigned_at, "Req_time") + "")

                holder.upcomingOderItemlistBinding.itemClickListener = object : CustomClickListener {
                    override fun onListClickListner() {

                        val intent = Intent(activity, HistoryDetailActivity::class.java)
                        intent.putExtra("selected_trip_id", transportHistory.transport[position].id.toString())
                        intent.putExtra("history_type", "upcoming")
                        intent.putExtra("servicetype", Constant.ModuleTypes.TRANSPORT)
                        activity!!.startActivity(intent)

                    }

                }
            }


            Constant.ModuleTypes.SERVICE -> {
                holder.upcomingOderItemlistBinding.titleUpcomingListTv.text = (transportHistory.service[position].booking_id)
                holder.upcomingOderItemlistBinding.orderedItemTv.text = transportHistory.service[position].service?.service_name
                holder.upcomingOderItemlistBinding.dateUpcomingListTv.text = (CommanMethods.getLocalTimeStamp(transportHistory.service[position]
                        .assigned_at?:"", "Req_Date_Month") + "")
                holder.upcomingOderItemlistBinding.timeUpcomingListTv.text = (CommanMethods.getLocalTimeStamp(transportHistory.service[position]
                        .assigned_at?:"", "Req_time") + "")

                holder.upcomingOderItemlistBinding.itemClickListener = object : CustomClickListener {
                    override fun onListClickListner() {

                        val intent = Intent(activity, HistoryDetailActivity::class.java)
                        intent.putExtra("selected_trip_id", transportHistory.service[position].id.toString())
                        intent.putExtra("history_type", "upcoming")
                        intent.putExtra("servicetype", Constant.ModuleTypes.SERVICE)
                        activity!!.startActivity(intent)

                    }

                }
            }

        }

    }


    inner class MyViewHolder(itemView: UpcomingOderItemlistBinding) : RecyclerView.ViewHolder(itemView.root) {

        val upcomingOderItemlistBinding = itemView
        fun bind() {

        }


    }

    override fun onListClickListner() {

        Log.d("currentadapter", "onListClickListner")
    }

}