package com.gox.xubermodule.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.PromocodeModel

class XuberCouponAdapter(internal var mContext: Context, private var images: ArrayList<PromocodeModel>) : PagerAdapter() {

    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mOnAdapterClickListener: PromoCodeUse? = null

    fun setOnClickListener(onClickListener: PromoCodeUse) {
        this.mOnAdapterClickListener = onClickListener
    }

    interface PromoCodeUse {
        fun useCoupon(promo: PromocodeModel)
    }

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.fragment_xuber_coupon_selection_page, container, false)
        val tvCouponCode = itemView.findViewById<View>(R.id.tvCouponCode) as TextView
        val tvCouponDescription = itemView.findViewById<View>(R.id.tvCouponDescription) as TextView
        val tvCouponValidity = itemView.findViewById<View>(R.id.tvCouponValidity) as TextView
        val tvUseThis = itemView.findViewById<View>(R.id.tvUseThis) as TextView
        val delimiter = " "
        val productImageModel = images[position]
        tvCouponCode.text = productImageModel.promoCode
        tvCouponDescription.text = productImageModel.promoDescription
        val expire = productImageModel.promoExpiration!!.split(delimiter)
        tvCouponValidity.text = mContext.getString(R.string.otp) + expire[0]
        container.addView(itemView)
        tvUseThis.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.useCoupon(images[position])
            }
        }
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
