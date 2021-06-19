package com.gox.app.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.databinding.ServiceslistItemHomeBinding
import com.gox.app.ui.home_fragment.MenuViewModel

class ServiceListAdapter(val activity: FragmentActivity?, private val menuList: List<HomeMenuResponse.ResponseData.Service>) : RecyclerView.Adapter<ServiceListAdapter.MyViewHolder>() {

    private var mOnAdapterClickListener: OnClickListener? = null
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<ServiceslistItemHomeBinding>(
                LayoutInflater.from(parent.context),
                R.layout.serviceslist_item_home, parent,
                false
        )
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuList[position])
        val menuResponseData = menuList[position]
        Glide.with(activity as Activity).load(menuResponseData.icon).into(holder.serviceslistItemHomeBinding.serviceIconImgv)
        holder.serviceslistItemHomeBinding.serviceIconImgv.background.setTint(Color.parseColor(menuResponseData.bg_color))
        holder.serviceslistItemHomeBinding.serviceNameTv.text = menuResponseData.title
        holder.serviceslistItemHomeBinding.serviceNameTv.setTextColor(Color.parseColor(menuResponseData.bg_color))

        holder.serviceslistItemHomeBinding.serviceIconImgv.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }

    }


    inner class MyViewHolder(itemView: ServiceslistItemHomeBinding) : RecyclerView.ViewHolder(itemView.root) {

        val serviceslistItemHomeBinding = itemView
        private val viewModel = MenuViewModel()
        fun bind(menu: HomeMenuResponse.ResponseData.Service) {
            viewModel.bind(menu)
        }


    }


}