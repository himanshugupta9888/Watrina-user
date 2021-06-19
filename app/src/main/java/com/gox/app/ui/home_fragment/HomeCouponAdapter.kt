package com.gox.app.ui.home_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.gox.basemodule.utils.ViewUtils
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.HomeMenuResponse


class HomeCouponAdapter(internal var mContext: Context, private var images: ArrayList<HomeMenuResponse.ResponseData.Promocode>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var mOnAdapterClickListener: OnClickListener? = null
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as FrameLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.item_offer, container, false)
        val offerImageView = itemView.findViewById<View>(R.id.offerlist_img) as ImageView
        val offerDescription = itemView.findViewById<View>(R.id.offer_decription) as TextView
        val productImageModel = images[position]
        ViewUtils.setImageViewGlide(mContext, offerImageView, productImageModel.picture)
        offerDescription.text = productImageModel.promo_description


        offerImageView.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as FrameLayout)
    }
}