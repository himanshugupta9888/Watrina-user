package com.gox.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.gox.app.R
import com.gox.app.databinding.OffersListItemBinding
import com.bumptech.glide.Glide
import com.github.florent37.materialleanback.MaterialLeanBack

class OffersCouponsAdapter(activity: FragmentActivity?) : MaterialLeanBack.Adapter<OffersCouponsAdapter.MyViewHolder>() {

    val activity = activity


    var bannerList = arrayOf<Int>(
            R.drawable.food_banner,
            R.drawable.taxi_banner,
            R.drawable.service_banner)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<OffersListItemBinding>(LayoutInflater.from(parent.context), R.layout.offers_list_item, parent, false)
        return MyViewHolder(inflate)
    }



    override fun getLineCount(): Int = 1
    override fun getCellsCount(row: Int): Int = 3


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()

        Glide.with(activity as Activity).load(bannerList[position]).into(holder.currentOderItemlistBinding.offerlistImg)
        holder.currentOderItemlistBinding.offerlistImg
    }


    inner class MyViewHolder(itemView: OffersListItemBinding) : MaterialLeanBack.ViewHolder(itemView.root) {

        val currentOderItemlistBinding = itemView

        fun bind() {

        }


    }


}