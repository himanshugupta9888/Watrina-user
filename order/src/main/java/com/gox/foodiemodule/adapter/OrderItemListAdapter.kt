package com.gox.foodiemodule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ResOrderDetail
import com.gox.foodiemodule.databinding.OrderDetailListItemBinding

class OrderItemListAdapter(activity: FragmentActivity?, val orderItems: List<ResOrderDetail.ResponseData.Invoice.Item?>?,
                           val currency_symbol: String?) : RecyclerView.Adapter<OrderItemListAdapter.MyViewHolder>() {

    val activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<OrderDetailListItemBinding>(LayoutInflater
                .from(parent.context), R.layout.order_detail_list_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = orderItems!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()

//        itemname(quantity x price)
        holder.resturantlistItemBinding.orderitemNameQuantatityTv.text = orderItems!![position]!!.product!!.item_name +
                "(" + orderItems[position]!!.quantity + "x" + currency_symbol + orderItems[position]!!.item_price + ")"

        holder.resturantlistItemBinding.orderItemPrice.text = currency_symbol +
                orderItems!![position]!!.total_item_price.toString()
        if (orderItems[position]!!.product!!.is_veg.equals("Pure Veg", true)) {
            holder.resturantlistItemBinding.orderitemNameQuantatityTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_veg, 0, 0, 0)

        } else {
            holder.resturantlistItemBinding.orderitemNameQuantatityTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_non_veg, 0, 0, 0);

        }


    }


    inner class MyViewHolder(itemView: OrderDetailListItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val resturantlistItemBinding = itemView

        fun bind() {

        }


    }


}