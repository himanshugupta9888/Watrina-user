package com.gox.basemodule.common.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.R
import com.gox.basemodule.databinding.RowPaymentModeBinding
import com.gox.basemodule.model.ConfigDataModel
import com.gox.basemodule.common.cardlist.CardListViewModel

class PaymentModeAdapter(context: Context, payTypes: List<ConfigDataModel.BaseApiResponseData.Appsetting.Payment>, cardListViewModel: CardListViewModel, isFromWallet: Boolean) : RecyclerView.Adapter<PaymentModeAdapter.PaymentViewHolder>() {

    private var context: Context? = null
    private var selectedPosition: Int? = -1
    private var payTypes: List<ConfigDataModel.BaseApiResponseData.Appsetting.Payment>? = null
    private var cardListViewModel: CardListViewModel? = null
    private var isFromWallet: Boolean? = false
    private var mOnAdapterClickListener: PaymentTypeListener? = null
    fun setOnClickListener(onClickListener: PaymentTypeListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    init {
        this.context = context
        this.payTypes = payTypes
        this.cardListViewModel = cardListViewModel
        this.isFromWallet = isFromWallet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val inflate = DataBindingUtil.inflate<RowPaymentModeBinding>(LayoutInflater.from(parent.context), R.layout.row_payment_mode, parent, false)
        return PaymentViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return payTypes!!.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.paymentBinding.tvPaymentMode.text = payTypes!![position].name
        if (payTypes!!.get(position).name.equals("card")) {
            holder.paymentBinding.tvPaymentMode.visibility = View.GONE
            holder.paymentBinding.tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_card, 0, 0, 0)
        } else {
            holder.paymentBinding.tvPaymentMode.visibility = View.VISIBLE
            holder.paymentBinding.tvPaymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_payment_cash, 0, 0, 0)
        }
        if (selectedPosition == position) {
            holder.paymentBinding.tvPaymentMode.background = ContextCompat.getDrawable(context!!, R.drawable.bg_blue_rounded_corner)
            holder.paymentBinding.tvPaymentMode.setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
        } else {
            holder.paymentBinding.tvPaymentMode.background = ContextCompat.getDrawable(context!!, R.drawable.bg_white)
            holder.paymentBinding.tvPaymentMode.setTextColor(ContextCompat.getColor(context!!, R.color.black))
        }
        holder.paymentBinding.tvPaymentMode.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }
        holder.paymentBinding.paymentmodel = object : CustomClickListner {
            override fun onListClickListner() {
                selectedPosition = holder.adapterPosition


            }
        }
    }

    inner class PaymentViewHolder(rowItemBinding: RowPaymentModeBinding) : RecyclerView.ViewHolder(rowItemBinding.root) {
        val paymentBinding = rowItemBinding
    }
}