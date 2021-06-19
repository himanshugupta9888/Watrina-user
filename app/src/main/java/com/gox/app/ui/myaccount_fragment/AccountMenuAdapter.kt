package com.gox.app.ui.myaccount_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.BR
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.AccountMenuModel
import com.gox.app.databinding.ItemAccountBinding


class AccountMenuAdapter(private val mMenuList: List<AccountMenuModel>,
                         private val mListener: OnClickListener)
    : RecyclerView.Adapter<AccountMenuAdapter.AccountVh>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            AccountVh(DataBindingUtil.inflate<ItemAccountBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_account,
                    parent,
                    false)
            )

    override fun getItemCount() = mMenuList.size

    override fun onBindViewHolder(holder: AccountVh, position: Int) =
            holder.bind(mMenuList[position], position)

    inner class AccountVh(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AccountMenuModel, position: Int) {
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.adapter, this@AccountMenuAdapter)
            binding.setVariable(BR.item, data)
            binding.executePendingBindings()
        }
    }

    fun onItemClick(position: Int) {
        mListener.onClick(position)
    }

}