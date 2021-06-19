package com.gox.xubermodule.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.SubServiceModel
import com.gox.xubermodule.databinding.SubserviceRowlistItemBinding
import com.gox.xubermodule.ui.activity.xubersubserviceactivity.XuberSubServiceActivity
import com.gox.xubermodule.utils.Utils.getPrice

class XuberSubServiceAdapter(val activity: XuberSubServiceActivity, var subserviceData: List<SubServiceModel.ResponseData?>)
    : RecyclerView.Adapter<XuberSubServiceAdapter.MyViewHolder>() {

    private object ACTION{
        internal const val ADD = 1000
        internal const val REMOVE = 1001
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = DataBindingUtil.inflate<SubserviceRowlistItemBinding>(LayoutInflater.from(parent.context)
                , R.layout.subservice_rowlist_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = subserviceData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.subserviceRowlistItemBinding.subserviceItemName.text = subserviceData[position]!!.service_name
        when (subserviceData[position]!!.selected) {
            "1" -> holder.subserviceRowlistItemBinding.subserviceListRb.setImageResource(R.drawable.radio_btn_checked)
            else -> holder.subserviceRowlistItemBinding.subserviceListRb.setImageResource(R.drawable.radio_btn_normal)
        }
        holder.subserviceRowlistItemBinding.subServiceItemLt.setOnClickListener {
            updateSelection(subserviceData[position]!!)
        }

        holder.subserviceRowlistItemBinding.itemcountAddBtn.setOnClickListener {
//            updateQuantity(subserviceData[position]!!, true)
            updateQuantity(position, ACTION.ADD)
        }
        holder.subserviceRowlistItemBinding.itemcountMinusBtn.setOnClickListener {
//            updateQuantity(subserviceData[position]!!, false)
            updateQuantity(position, ACTION.REMOVE)
        }
        when (subserviceData[position]!!.service_city) {
            null -> {
                holder.subserviceRowlistItemBinding.itemPriceTv.visibility = GONE
                holder.subserviceRowlistItemBinding.quantityLt.visibility = GONE
            }
            else -> {
                when (subserviceData[position]!!.service_city!!.allow_quantity) {
                    1 -> holder.subserviceRowlistItemBinding.quantityLt.visibility = VISIBLE
                    else -> holder.subserviceRowlistItemBinding.quantityLt.visibility = GONE
                }
                holder.subserviceRowlistItemBinding.itemPriceTv.visibility = GONE
                holder.subserviceRowlistItemBinding.itemCountTv.text = subserviceData[position]!!.service_city!!.quantity.toString()
                holder.subserviceRowlistItemBinding.itemPriceTv.text = getPrice(subserviceData[position]!!.service_city!!,activity)
            }
        }
    }

    /*private fun updateQuantity(responseData: SubServiceModel.ResponseData, add: Boolean) {
        subserviceData.forEach {
            when (responseData.id) {
                it!!.id -> {
                    when (add) {
                        true -> {
                            when (it.service_city!!.quantity) {
                                it.service_city.max_quantity!! -> {
                                }
                                else -> {
                                    it.service_city.quantity = +1
                                }
                            }
                        }
                        false -> {
                            when (it.service_city!!.quantity) {
                                0 -> {
                                }
                                else -> {
                                    it.service_city.quantity = -1
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged()
    }*/


    private fun updateQuantity(position:Int,action:Int){
        val serviceCity = subserviceData[position]?.service_city
        when(action){
            ACTION.ADD ->{
                if((serviceCity?.max_quantity)!! > (serviceCity.quantity)){
                    serviceCity.quantity = serviceCity.quantity + 1
                }
            }

            ACTION.REMOVE ->{
                if(0 < (serviceCity?.quantity)!!){
                    serviceCity.quantity = serviceCity.quantity - 1
                }
            }
        }

        notifyItemChanged(position)
    }


    private fun updateSelection(responseData: SubServiceModel.ResponseData) {
        subserviceData.forEach {
            when (responseData.id) {
                it!!.id -> it.selected = "1"
                else -> it.selected = "0"
            }
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: SubserviceRowlistItemBinding)
        : RecyclerView.ViewHolder(itemView.root) {
        val subserviceRowlistItemBinding = itemView
        fun bind() {

        }
    }


}
