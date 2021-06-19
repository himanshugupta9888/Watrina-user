package com.gox.xubermodule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReasonModel
import com.gox.xubermodule.databinding.XuberReasonListRowBinding

class XuberReasonAdapter(context: Context, private var mReasonList: List<ReasonModel>) : RecyclerView.Adapter<XuberReasonAdapter.MyViewHolder>() {


    private var mOnAdapterClickListener: OnViewClickListener? = null
    fun setOnClickListener(onClickListener: OnViewClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<XuberReasonListRowBinding>(LayoutInflater.from(parent.context), R.layout.xuber_reason_list_row, parent, false)
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


    inner class MyViewHolder(itemView: XuberReasonListRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        val currentOderItemlistBinding = itemView
        /*  val viewmodel = ManageAddressModel()
          fun bind(addressModel: AddressModel) {
              viewmodel.bind(addressModel)
          }
          */
    }


}