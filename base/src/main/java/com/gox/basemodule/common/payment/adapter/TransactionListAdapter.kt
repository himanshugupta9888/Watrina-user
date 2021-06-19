package com.gox.basemodule.common.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.R
import com.gox.basemodule.common.payment.model.WalletTransactionList
import com.gox.basemodule.databinding.TransactionBaseListItemBinding
import com.gox.basemodule.utils.AppUtils

class TransactionListAdapter(context: Context, transactionList: List<WalletTransactionList.ResponseData.Data>, currencyType: String)
    : RecyclerView.Adapter<TransactionListAdapter.MyViewHolder>() {

    var context: Context? = null
    var transactionList: List<WalletTransactionList.ResponseData.Data>? = null
    var currencyType: String = ""

    init {
        this.context = context
        this.transactionList = transactionList
        this.currencyType = currencyType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<TransactionBaseListItemBinding>(LayoutInflater.from(parent.context)
                , R.layout.transaction_base_list_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return transactionList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.currentOderItemlistBinding.tvTransactionID.setText(transactionList!!.get(position).transaction_alias)
        holder.currentOderItemlistBinding.tvTransactionAmount.setText(AppUtils.getNumberFormat()!!.format(transactionList!!.get(position).amount))
        if (transactionList!!.get(position).type.equals("D")) {
            holder.currentOderItemlistBinding.tvTransactionStatus.setText(context!!.resources.getString(R.string.depited))
            holder.currentOderItemlistBinding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context!!, R.color.dispute_status_open))
        } else {
            holder.currentOderItemlistBinding.tvTransactionStatus.setText(context!!.resources.getString(R.string.credited))
            holder.currentOderItemlistBinding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context!!, R.color.credit))
        }
    }

    inner class MyViewHolder(itemView: TransactionBaseListItemBinding)
        : RecyclerView.ViewHolder(itemView.root) {
        val currentOderItemlistBinding = itemView
    }

}