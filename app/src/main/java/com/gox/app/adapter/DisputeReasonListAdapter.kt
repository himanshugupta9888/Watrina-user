package com.gox.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.R
import com.gox.app.data.repositary.remote.model.DisputeListData
import com.gox.app.databinding.DisputeRowItemBinding
import com.gox.app.ui.historydetailactivity.HistoryDetailViewModel


class DisputeReasonListAdapter(val historyDetailViewModel: HistoryDetailViewModel, val disputereasonList: List<DisputeListData>)
    : RecyclerView.Adapter<DisputeReasonListAdapter.MyViewHolder>() {

    private var mOnAdapterClickListener: ReasonListClicklistner? = null
    private var selectedPosition = -1

    fun setOnClickListener(onClickListener: ReasonListClicklistner) {
        this.mOnAdapterClickListener = onClickListener
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        this.context = parent.context
        val inflate = DataBindingUtil
                .inflate<DisputeRowItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.dispute_row_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = disputereasonList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (selectedPosition == position) {
            holder.disputeRowItemBinding.rbDisbute.isChecked = true
        } else {
            holder.disputeRowItemBinding.rbDisbute.isChecked = false
        }
        holder.disputeRowItemBinding.tvDisbuteReason.text = (disputereasonList.get(position)
                .dispute_name).toLowerCase().capitalize()

        holder.disputeRowItemBinding.llDisputeReaons.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.reasonOnItemClick((disputereasonList.get(position)
                        .dispute_name).toLowerCase().capitalize())
                selectedPosition = position
                notifyDataSetChanged()

            }
        }


//
//        holder.bind()
    }


    inner class MyViewHolder(itemView: DisputeRowItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val disputeRowItemBinding = itemView

        fun bind() {

        }


    }


}


