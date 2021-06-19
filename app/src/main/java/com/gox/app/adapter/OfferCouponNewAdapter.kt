package com.gox.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.R
import com.bumptech.glide.Glide
import com.gox.app.callbacks.OnClickListener
import com.gox.app.databinding.OffersListItemBinding

class OfferCouponNewAdapter(val activity: FragmentActivity?) : RecyclerView.Adapter<OfferCouponNewAdapter.MyViewHolder>() {

    var bannerList = arrayOf<Int>(
            R.drawable.doctor_banner,
            R.drawable.taxi_banner,
            R.drawable.food_banner)

    private var mOnAdapterClickListener: OnClickListener? = null
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<OffersListItemBinding>(LayoutInflater.from(parent.context), R.layout.offers_list_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        Glide.with(activity as Activity).load(bannerList[position]).into(holder.currentOderItemlistBinding.offerlistImg)
        holder.currentOderItemlistBinding.offerlistImg
    }


    inner class MyViewHolder(itemView: OffersListItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val currentOderItemlistBinding = itemView
        fun bind() {
        }


    }


}