package com.gox.xubermodule.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.basemodule.common.payment.utils.CommonMethods
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReviewListModel
import com.gox.xubermodule.databinding.ReviewsRowItemListBinding

class ProviderReviewsAdapter(val activity: Activity?, var reviewListModel: List<ReviewListModel.ResponseData.Review>) : RecyclerView.Adapter<ProviderReviewsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = DataBindingUtil.inflate<ReviewsRowItemListBinding>(LayoutInflater.from(parent.context)
                , R.layout.reviews_row_item_list, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount() = reviewListModel.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        val review: ReviewListModel.ResponseData.Review = reviewListModel[position]
        if(!review.user?.picture.isNullOrEmpty())
        Glide.with(activity!!).load(review.user.picture).placeholder(R.drawable.ic_profile_place_holder).into(holder.reviewsRowItemListBinding.providerImg)
        else
        Glide.with(activity!!).load(R.drawable.ic_profile_place_holder).placeholder(R.drawable.ic_profile_place_holder).into(holder.reviewsRowItemListBinding.providerImg)

        if (!review.provider_comment.isEmpty() && review.provider_comment != "null")
            holder.reviewsRowItemListBinding.reviewCmtTv.text = review.provider_comment
        else
            holder.reviewsRowItemListBinding.reviewCmtTv.text = "-"
        holder.reviewsRowItemListBinding.reviewDateTv.text = CommonMethods.getLocalTimeStamp(review.created_at)
    }

    inner class MyViewHolder(itemView: ReviewsRowItemListBinding)
        : RecyclerView.ViewHolder(itemView.root) {
        val reviewsRowItemListBinding = itemView
        fun bind() {

        }
    }
}
