package com.gox.foodiemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.PromocodeModel
import com.gox.foodiemodule.databinding.ViewpagerItemlistBinding
import com.gox.foodiemodule.ui.restaurantlist_activity.CouponsItemLayout


class CustomPagerAdapter(val context: Context, val promoCoderesponseData: List<PromocodeModel>) : PagerAdapter(), ViewPager.PageTransformer {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view == `object`
    }

    override fun getCount(): Int = promoCoderesponseData.size


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ViewpagerItemlistBinding.inflate(LayoutInflater.from(context), container, false)
        Glide.with(context).load(promoCoderesponseData[position].picture).placeholder(R.drawable.dummy_foodi_banner)
                .into(binding.imageView)
        binding.offertv.text = promoCoderesponseData[position].promoCode
        binding.offerDescriptiontv.text = promoCoderesponseData[position].promoDescription
        binding.itemRoot.setScaleBoth(if (position == FIRST_PAGE) BIG_SCALE else SMALL_SCALE)
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun transformPage(page: View, position: Float) {
        val myLinearLayout = page.findViewById<CouponsItemLayout>(R.id.item_root)
        var scale = BIG_SCALE
        if (position > 0) scale -= position * DIFF_SCALE else scale += position * DIFF_SCALE
        if (scale < 0) scale = 0f
        myLinearLayout.setScaleBoth(scale)
    }

    companion object {
        const val FIRST_PAGE = 0
        const val BIG_SCALE = 1.0f
        const val SMALL_SCALE = 0.7f
        const val DIFF_SCALE = BIG_SCALE - SMALL_SCALE
    }

}

