package com.gox.xubermodule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.Addresses
import com.gox.xubermodule.databinding.ItemXuberAddressBinding

class XuberAddressAdapter(private var addressesList: List<Addresses>): RecyclerView.Adapter<XuberAddressAdapter.MyViewHolder>(){

    private var mOnViewClickListener: OnViewClickListener? = null
    private lateinit var context:Context

    fun setOnClickListener(onClickListener: OnViewClickListener) {
        this.mOnViewClickListener = onClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
       val inflate = DataBindingUtil.inflate<ItemXuberAddressBinding>(LayoutInflater.from(parent.context), R.layout.item_xuber_address,parent,false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = addressesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mAddressModel = addressesList[position]
                when(mAddressModel.address_type?.toUpperCase()?:""){
                    "HOME" ->{
                        holder.currentOderItemlistBinding.tvAddressType.text = mAddressModel.address_type
                        holder.currentOderItemlistBinding.ivAddressIcon.setImageDrawable(context.getDrawable(R.drawable.ic_home))
                    }

                    "WORK" ->{
                        holder.currentOderItemlistBinding.tvAddressType.text = mAddressModel.address_type
                        holder.currentOderItemlistBinding.ivAddressIcon.setImageDrawable(context.getDrawable(R.drawable.ic_work))
                    }

                    else ->{
                        holder.currentOderItemlistBinding.tvAddressType.text = mAddressModel.title
                        holder.currentOderItemlistBinding.ivAddressIcon.setImageDrawable(context.getDrawable(R.drawable.ic_location))

                    }
                }



        var address = ""

        mAddressModel.flat_no.let {
            address = it
        }

        mAddressModel.street.let {
            address = if (address.isNotEmpty())
                "${address},$it"
            else
                it
        }

        mAddressModel.landmark.let {
            address = if (address.isNullOrEmpty())
                "${address},$it"
            else
                address
        }

        holder.currentOderItemlistBinding.tvAddress.text = address


    }

    inner class MyViewHolder(itemView:ItemXuberAddressBinding):RecyclerView.ViewHolder(itemView.root){
        val currentOderItemlistBinding = itemView
        init {
            itemView.rlAddressRoot.setOnClickListener {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener!!.onClick(adapterPosition)
                }
            }
        }
    }

}