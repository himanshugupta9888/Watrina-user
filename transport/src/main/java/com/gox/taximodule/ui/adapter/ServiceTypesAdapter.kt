package com.gox.taximodule.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.data.dto.ServiceModel
import com.gox.taximodule.databinding.ServiceTypeInflateBinding



class ServiceTypesAdapter(private val activity: AppCompatActivity, private val serviceList: List<ServiceModel>) :
        RecyclerView.Adapter<ServiceTypesAdapter.MyViewHolder>() {

    private lateinit var mBinding: ServiceTypeInflateBinding
    private var selectedPosition: Int? = -1
    private var mOnAdapterClickListener: OnViewClickListener? = null

    fun setOnClickListener(onClickListener: OnViewClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.service_type_inflate, parent, false)
        return MyViewHolder(mBinding)
    }

    override fun getItemCount() = serviceList.size

    @SuppressLint("UseValueOf")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val serviceData = serviceList[position]
        holder.mBinding.tvServiceName.text = serviceData.vehicleName
        //  mBottomSheetBehavior = BottomSheetBehavior.from(holder.mBinding.lLHolder);
        Glide.with(activity as Context)
                .load(serviceData.vehicleImage)
                .apply(RequestOptions()
                        .placeholder(R.drawable.placeholder_car)
                        .error(R.drawable.placeholder_car)
                        .dontAnimate())
                .into(holder.mBinding.ivServiceImage)

        holder.mBinding.tvServiceETA.text = serviceData.estimatedTime
        holder.mBinding.lLHolder.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()

            if (mOnAdapterClickListener != null) mOnAdapterClickListener!!.onClick(position)
        }
        holder.mBinding.ivServiceImage.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            // mBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);

            if (mOnAdapterClickListener != null) mOnAdapterClickListener!!.onClick(position)
        }

        holder.mBinding.tvServiceName.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_inactive_medium_corner)
        holder.mBinding.tvServiceName.setTextColor(ContextCompat.getColor(activity, R.color.black))
        holder.mBinding.tvServiceETA.setTextColor(ContextCompat.getColor(activity, R.color.black))
        serviceData.isSelected = false

        if (selectedPosition == position) {
            holder.mBinding.tvServiceName.background = ContextCompat.getDrawable(activity, R.drawable.bg_orange_rounded_corner)
            holder.mBinding.tvServiceName.setTextColor(ContextCompat.getColor(activity, R.color.colorWhite))
            holder.mBinding.tvServiceETA.setTextColor(ContextCompat.getColor(activity, R.color.colorTaxiPrimary))
            serviceData.isSelected = true
        }
    }

    inner class MyViewHolder(itemView: ServiceTypeInflateBinding) : RecyclerView.ViewHolder(itemView.root) {
        val mBinding = itemView




        init {
            mBinding.ivServiceImage.setOnClickListener {
                if (mOnAdapterClickListener != null) mOnAdapterClickListener!!.onClick(adapterPosition)
            }
        }
    }

}