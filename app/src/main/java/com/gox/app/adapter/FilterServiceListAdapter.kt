package com.gox.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.Service
import com.gox.app.databinding.FilterserviceRowitemBinding
import com.gox.app.ui.dashboard.UserDashboardViewModel


class FilterServiceListAdapter(val orderFragmentViewModel: UserDashboardViewModel, val filterServiceListName: List<Service>)
    : RecyclerView.Adapter<FilterServiceListAdapter.MyViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        this.context = parent.context
        val inflate = DataBindingUtil
                .inflate<FilterserviceRowitemBinding>(LayoutInflater.from(parent.context),
                        R.layout.filterservice_rowitem, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = filterServiceListName.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.filterserviceRowitemBinding.filterserviceNameTv.text = (filterServiceListName.get(position)
                .admin_service).toLowerCase().capitalize()
        when (orderFragmentViewModel.selectedFilterService.value) {
            filterServiceListName[position].admin_service -> {
                holder.filterserviceRowitemBinding.filterserviceNameTv.background =
                        ContextCompat.getDrawable(context
                                , R.drawable.custom_roundcorner_selected_type)
                holder.filterserviceRowitemBinding.filterserviceNameTv.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }
            else -> {
                holder.filterserviceRowitemBinding.filterserviceNameTv.background =
                        ContextCompat.getDrawable(context
                                , R.drawable.custom_roundcorner_button_grey)
                holder.filterserviceRowitemBinding.filterserviceNameTv.setTextColor(ContextCompat.getColor(context,R.color.order_normal_txt_color))
            }
        }
        holder.filterserviceRowitemBinding.itemClickListener = object : CustomClickListener {
            override fun onListClickListner() {
                orderFragmentViewModel.selectedFilterService.value = filterServiceListName[position].admin_service
                notifyDataSetChanged()
            }
        }
    }

    inner class MyViewHolder(itemView: FilterserviceRowitemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val filterserviceRowitemBinding = itemView
        fun bind() {

        }
    }
}
