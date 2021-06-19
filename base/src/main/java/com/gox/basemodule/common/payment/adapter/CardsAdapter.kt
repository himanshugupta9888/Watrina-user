package com.gox.basemodule.common.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.R
import com.gox.basemodule.databinding.RowBaseSavedDetailBinding
import com.gox.basemodule.common.cardlist.CardListViewModel
import com.gox.basemodule.common.payment.model.CardResponseModel

class CardsAdapter(val context: Context?, cardList: MutableList<CardResponseModel>, cardListViewModel: CardListViewModel)
    : RecyclerView.Adapter<CardsAdapter.CardViewHolder>(), View.OnClickListener {
    private var mOnAdapterClickListener: CardOnClickListener? = null
    fun setOnClickListener(onClickListener: CardOnClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    private var cardList: MutableList<CardResponseModel>? = null
    private var cardListViewModel: CardListViewModel? = null
    private var selectedPosition: Int? = -1

    init {
        this.cardList = cardList
        this.cardListViewModel = cardListViewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflate = DataBindingUtil.inflate<RowBaseSavedDetailBinding>(LayoutInflater.from(parent.context),
                R.layout.row_base_saved_detail, parent, false)
        return CardViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return cardList!!.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.cardViewBinding.tvCardType.text = cardList!!.get(position).getBrand()
        holder.cardViewBinding.tvCardNumber.text = String.format(context!!.resources.getString(R.string.row_card_number), cardList!!.get(position).getLastFour())
        holder.cardViewBinding.root
        if (selectedPosition == position && cardList!!.get(position).isCardSelected == false) {
            selectedPosition = -1
        }
        if (cardList!!.get(position).isCardSelected == false) {
            holder.cardViewBinding.root.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.card_unselected))
            holder.cardViewBinding.tvCardNumber.setTextColor(ContextCompat.getColor(context,
                    R.color.black))
            holder.cardViewBinding.tvCardType.setTextColor(ContextCompat.getColor(context,
                    R.color.black))
        } else {
            holder.cardViewBinding.root.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.colorAccent))
            holder.cardViewBinding.tvCardNumber.setTextColor(ContextCompat.getColor(context,
                    R.color.colorWhite))
            holder.cardViewBinding.tvCardType.setTextColor(ContextCompat.getColor(context,
                    R.color.colorWhite))
        }

        holder.cardViewBinding.root.setOnClickListener(this)
        holder.cardViewBinding.root.tag = holder
    }

    fun addItem(item : CardResponseModel){
        cardList!!.add(item)
        notifyItemChanged(cardList!!.size)
    }


    inner class CardViewHolder(cardViewBinding: RowBaseSavedDetailBinding)
        : RecyclerView.ViewHolder(cardViewBinding.root) {
        val cardViewBinding = cardViewBinding
    }


    override fun onClick(v: View?) {
        var cardViewHolder = v!!.tag as CardViewHolder
        var position = cardViewHolder.adapterPosition
        if (selectedPosition != position) {
            selectedPosition = position
            val cardResponseModel = cardList!!.get(selectedPosition!!)
            cardListViewModel!!.navigator.cardPicked(cardResponseModel.getCardId().toString(), cardResponseModel.getId().toString(), selectedPosition!!)
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }
    }
}