package com.gox.foodiemodule.ui.restaurantlist_activity

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout
import com.gox.foodiemodule.adapter.CustomPagerAdapter.Companion.BIG_SCALE

class CouponsItemLayout : LinearLayout {

    private var mScale = BIG_SCALE

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    fun setScaleBoth(scale: Float) {
        this.mScale = scale
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.scale(mScale, mScale, (this.width / 2).toFloat(), (this.height / 2).toFloat())
        super.onDraw(canvas)
    }
}