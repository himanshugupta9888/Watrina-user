package com.gox.foodiemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ReasonModel
import com.gox.foodiemodule.databinding.OrderReasonListRowBinding

class OrderCancelReasonAdapter(context: Context, private var mReasonList: List<ReasonModel>) : RecyclerView.Adapter<OrderCancelReasonAdapter.MyViewHolder>() {


    private var mOnAdapterClickListener: OnViewClickListener? = null
    fun setOnClickListener(onClickListener: OnViewClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<OrderReasonListRowBinding>(
                LayoutInflater.from(parent.context),
                R.layout.order_reason_list_row,
                parent,
                false
        )
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return mReasonList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mReasonModel = mReasonList[position]
        //holder.bind(mReasonList[position])
        holder.currentOderItemlistBinding.tvReaon.text = mReasonModel.reason
        holder.currentOderItemlistBinding.tvReaon.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }
    }

    inner class MyViewHolder(itemView: OrderReasonListRowBinding) : RecyclerView.ViewHolder(itemView.root) {
        val currentOderItemlistBinding = itemView
        /*  val viewmodel = ManageAddressModel()
          fun bind(addressModel: AddressModel) {
              viewmodel.bind(addressModel)
          }
          */
    }


}