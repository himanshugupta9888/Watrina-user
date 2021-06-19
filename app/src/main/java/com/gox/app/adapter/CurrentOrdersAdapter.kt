package com.gox.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.foodiemodule.ui.orderdetailactivity.OrderDetailActivity
import com.gox.taximodule.ui.activity.main.TaxiMainActivity
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.TransportResponseData
import com.gox.app.databinding.CurrentOderItemlistBinding
import com.gox.app.utils.CommanMethods
import com.gox.basemodule.data.Constant
import com.gox.xubermodule.ui.activity.serviceflowactivity.ServiceFlowActivity

class CurrentOrdersAdapter(val activity: FragmentActivity?, val transportHistory: TransportResponseData, val servicetype: String)
    : RecyclerView.Adapter<CurrentOrdersAdapter.MyViewHolder>(), CustomClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<CurrentOderItemlistBinding>(LayoutInflater.from(parent.context),
                R.layout.current_oder_itemlist, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        when (servicetype) {
            Constant.ModuleTypes.TRANSPORT -> return transportHistory.transport.size
            Constant.ModuleTypes.SERVICE -> return transportHistory.service.size
            Constant.ModuleTypes.ORDER -> return transportHistory.order.size
        }

        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        when (servicetype) {

            Constant.ModuleTypes.TRANSPORT -> {
                updateHistoryUi(holder, transportHistory.transport[position].ride?.vehicle_name!!
                        , transportHistory.transport[position].status.equals("Completed", true)
                        , transportHistory.transport[position].booking_id
                        , (CommanMethods.getLocalTimeStamp(transportHistory.transport[position].assigned_at, "Req_time") + "")
                        , (CommanMethods.getLocalTimeStamp(transportHistory.transport[position].assigned_at, "Req_Date_Month") + ""))

                holder.currentOderItemlistBinding.itemClickListener = object : CustomClickListener {
                    override fun onListClickListner() {
                        val intent = Intent(activity, TaxiMainActivity::class.java)
                        intent.putExtra("serviceId", transportHistory.transport[position].id)
                        activity!!.startActivity(intent)
                    }

                }
            }

            Constant.ModuleTypes.SERVICE -> {

                updateHistoryUi(holder, transportHistory.service[position].service?.service_name.toString()!!
                        , transportHistory.service[position].status.equals("Completed", true)
                        , transportHistory.service[position].booking_id!!
                        , (CommanMethods.getLocalTimeStamp(transportHistory.service[position].assigned_at!!, "Req_time") + "")
                        , (CommanMethods.getLocalTimeStamp(transportHistory.service[position].assigned_at!!, "Req_Date_Month") + ""))

                holder.currentOderItemlistBinding.itemClickListener = object : CustomClickListener {
                    override fun onListClickListner() {
                        val intent = Intent(activity, ServiceFlowActivity::class.java)
                        intent.putExtra("serviceId", transportHistory.service[position].id)

                        activity!!.startActivity(intent)
                    }

                }

            }
            Constant.ModuleTypes.ORDER -> {
                updateHistoryUi(holder, transportHistory.order[position].pickup?.store_name.toString()!!
                        , transportHistory.order[position].status.equals("Completed", true)
                        , transportHistory.order[position].store_order_invoice_id!!
                        , (CommanMethods.getLocalTimeStamp(transportHistory.order[position].created_at!!, "Req_time") + "")
                        , (CommanMethods.getLocalTimeStamp(transportHistory.order[position].created_at!!, "Req_Date_Month") + ""))

                holder.currentOderItemlistBinding.itemClickListener = object : CustomClickListener {
                    override fun onListClickListner() {
                        val intent = Intent(activity, OrderDetailActivity::class.java)
                        intent.putExtra("orderId", transportHistory.order[position].id)
                        activity!!.startActivity(intent)
                    }

                }

            }


        }
    }


    inner class MyViewHolder(itemView: CurrentOderItemlistBinding) : RecyclerView.ViewHolder(itemView.root) {

        val currentOderItemlistBinding = itemView


        fun bind() {

        }


    }

    override fun onListClickListner() {

    }

    private fun updateHistoryUi(holder: MyViewHolder, order: String, status: Boolean, booking_id: String
                                , req_time: String, req_month: String) {
        holder.currentOderItemlistBinding.titleCurrentListTv.text = booking_id
        holder.currentOderItemlistBinding.dateCurrentListTv.text = req_month
        holder.currentOderItemlistBinding.timeCurrentListTv.text = req_time
        holder.currentOderItemlistBinding.orderedItemTv.text = order
    }
}
