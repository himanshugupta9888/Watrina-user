package com.gox.app.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.gox.app.R

@BindingAdapter("picture")
fun loadImage(imageView: ImageView, imageURL: String) {
    Glide.with(imageView.context)
            .load(imageURL)
            .placeholder(R.drawable.image_placeholder)
            .into(imageView)
}

@BindingAdapter("android:src")
fun setImage(imageView: ImageView, resId: Int) {
    imageView.setImageResource(resId)
}

@BindingAdapter("app:setVisibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
