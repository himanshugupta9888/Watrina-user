package com.gox.taximodule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.data.dto.EstimateFareResponse
import com.gox.taximodule.data.dto.PromocodeModel

class CouponAdapter(internal var mContext: Context, private var images: ArrayList<EstimateFareResponse.ResponseData.Promocode>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mOnAdapterClickListener: OnViewClickListener? = null
    fun setOnClickListener(onClickListener: OnViewClickListener) {
        this.mOnAdapterClickListener = onClickListener
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.fragment_coupon_selection_page, container, false)
        val tvCouponCode = itemView.findViewById<View>(R.id.tvCouponCode) as TextView
        val tvCouponDescription = itemView.findViewById<View>(R.id.tvCouponDescription) as TextView
        val tvCouponValidity = itemView.findViewById<View>(R.id.tvCouponValidity) as TextView
        val tvUseThis = itemView.findViewById<View>(R.id.tvUseThis) as TextView
        var delimiter = " "

        // imageView.setImageResource(images[position]);
        val productImageModel = images[position]
        tvCouponCode.text = productImageModel.promo_code
        tvCouponDescription.text = productImageModel.promo_description
        val expire = productImageModel.expiration!!.split(delimiter)

        tvCouponValidity.text = mContext.getString(R.string.validTill) + expire[0]
        container.addView(itemView)
        tvUseThis.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.onClick(position)
            }
        }


        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}