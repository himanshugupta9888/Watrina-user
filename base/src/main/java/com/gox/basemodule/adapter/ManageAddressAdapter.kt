package com.gox.basemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.R
import com.gox.basemodule.common.manage_address.ManageAddressModel
import com.gox.basemodule.common.manage_address.OnManageAddressClickListener
import com.gox.basemodule.databinding.ManagesAddressListItemBinding
import com.gox.basemodule.model.AddressModel


class ManageAddressAdapter(val activity: FragmentActivity?, private var mAddressList: List<AddressModel>) : RecyclerView.Adapter<ManageAddressAdapter.MyViewHolder>() {

    private var mOnAdapterClickListener: OnManageAddressClickListener? = null
    private lateinit var mContext:Context
    fun setOnClickListener(onClickListener: OnManageAddressClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<ManagesAddressListItemBinding>(LayoutInflater.from(parent.context), R.layout.manages_address_list_item, parent, false)
        mContext = parent.context
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return mAddressList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mAddressModel = mAddressList[position]
        holder.bind(mAddressList[position])

        when(mAddressModel.addressType?.toUpperCase()?:""){
            "HOME" ->{
                holder.currentOderItemlistBinding.titleAddressTv.text = mAddressModel.addressType
                holder.currentOderItemlistBinding.titleAddressTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0)
            }

            "WORK" ->{
                holder.currentOderItemlistBinding.titleAddressTv.text = mAddressModel.addressType
                holder.currentOderItemlistBinding.titleAddressTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_work, 0, 0, 0)
            }

            else ->{
                holder.currentOderItemlistBinding.titleAddressTv.text = mAddressModel.title
                holder.currentOderItemlistBinding.titleAddressTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0)
            }
        }

        holder.currentOderItemlistBinding.addressView.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(mAddressModel)
            }
        }

        var address = ""

        mAddressModel.flatNumber?.let {
            address = it
        }

        mAddressModel.street?.let {
            address = if (address.isNotEmpty())
                "${address},$it"
            else
                it
        }

        mAddressModel.landmark?.let {
            address = if (address.isNotEmpty())
                "${address},$it"
            else
                it
        }

        holder.currentOderItemlistBinding.addressTv.text = address

    }


    inner class MyViewHolder(itemView: ManagesAddressListItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val currentOderItemlistBinding = itemView
        val viewmodel = ManageAddressModel()
        fun bind(addressModel: AddressModel) {
            viewmodel.bind(addressModel)
        }

        init {

            currentOderItemlistBinding.editMangeaddressTv.setOnClickListener {
                if (mOnAdapterClickListener != null) {
                    mOnAdapterClickListener!!.onEdit(adapterPosition)
                }
            }
            currentOderItemlistBinding.deleteManageaddrsTv.setOnClickListener {
                if (mOnAdapterClickListener != null) {
                    mOnAdapterClickListener!!.onDelete(adapterPosition)
                }
            }

        }
    }


}