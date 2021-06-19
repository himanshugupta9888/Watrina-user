package com.gox.app.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.databinding.RecommendedServiceListBinding
import com.gox.basemodule.data.Constant
import com.squareup.picasso.Picasso



class RecommendedServiceAdapter(activity: FragmentActivity, private val menuList: List<HomeMenuResponse.ResponseData.Service>) : RecyclerView.Adapter<RecommendedServiceAdapter.MyViewHolder>() {

    private val activity = activity

    private var mOnAdapterClickListener: OnClickListener? = null
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<RecommendedServiceListBinding>(LayoutInflater.from(parent.context), R.layout.recommended_service_list, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = menuList.size

    @SuppressLint("Range", "DefaultLocale")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        val mMenuModel = menuList[position]
        Picasso.get().load(mMenuModel.featured_image).into(holder.recommendedServiceListBinding.recommendedServiceBannerImg)

        val color = getColorWithAlpha(Color.parseColor(mMenuModel.bg_color),0.5f)
        holder.recommendedServiceListBinding.cvFeaturedService.setCardBackgroundColor(color)
        holder.recommendedServiceListBinding.cvFeaturedService.setBackgroundColor(color)

        if (position % 2 == 0) {
            holder.recommendedServiceListBinding.btnGo.setBackgroundColor(Color.parseColor("#F7BD66"))
        } else {
            holder.recommendedServiceListBinding.btnGo.setBackgroundColor(Color.parseColor("#8CA1AD"))
        }
        holder.recommendedServiceListBinding.tvTitle.setText(mMenuModel.title)
        when (mMenuModel.service.admin_service.toUpperCase()) {
            Constant.ModuleTypes.TRANSPORT -> {
                holder.recommendedServiceListBinding.btnGo.setText(activity.getString(R.string.go_ride))
                holder.recommendedServiceListBinding.tvDescription.setText(activity.resources.getString(R.string.walk_1_description))
            }

            Constant.ModuleTypes.ORDER -> {
                holder.recommendedServiceListBinding.btnGo.setText(activity.getString(R.string.go_order))
                holder.recommendedServiceListBinding.tvDescription.setText(activity.resources.getString(R.string.walk_2_description))
            }

            Constant.ModuleTypes.SERVICE -> {
                holder.recommendedServiceListBinding.btnGo.setText(activity.getString(R.string.get_appointment))
                holder.recommendedServiceListBinding.tvDescription.setText(activity.resources.getString(R.string.walk_3_description))
            }
        }

        holder.recommendedServiceListBinding.cvFeaturedService.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }
    }

    fun getColorWithAlpha(color: Int, ratio: Float): Int {
        var newColor = 0
        val alpha = Math.round(Color.alpha(color) * ratio)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }


    inner class MyViewHolder(itemView: RecommendedServiceListBinding) : RecyclerView.ViewHolder(itemView.root) {

        val recommendedServiceListBinding = itemView

        fun bind() {

        }


    }

}