package com.gox.xubermodule.ui.activity.providerdetail

import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gox.basemodule.base.BaseActivity
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReviewListModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ProviderDetailActivityBinding
import com.gox.xubermodule.ui.activity.confirmbooking.ConfirmBookingActivity
import com.gox.xubermodule.ui.activity.providerreviews.ProviderReviewsActivity
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersActivity
import com.gox.xubermodule.ui.adapter.ProviderReviewsAdapter
import com.gox.xubermodule.utils.Utils.getPrice
import kotlinx.android.synthetic.main.provider_detail_activity.*
import kotlinx.android.synthetic.main.toolbar_service_category.*
import java.text.DecimalFormat

class ProviderDetailActivity : BaseActivity<ProviderDetailActivityBinding>(), ProviderDetailNavigator {

    private lateinit var providerDetailActivityBinding: ProviderDetailActivityBinding
    lateinit var providerDetailViewModel: ProviderDetailViewModel
    override fun getLayoutId(): Int = R.layout.provider_detail_activity

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        providerDetailActivityBinding = mViewDataBinding as ProviderDetailActivityBinding
        providerDetailViewModel = ProviderDetailViewModel()
        providerDetailActivityBinding.providerDetailViewModel = providerDetailViewModel
        providerDetailViewModel.navigator = this
        ivBack.setOnClickListener { onBackPressed() }
        service_name.text = getString(R.string.service_provider)
        setProviderProfileDetails()
        loadingObservable.value = true
        providerDetailViewModel.getProviderReviews("0")
        providerDetailViewModel.getReviewResponse().observe(this@ProviderDetailActivity,
                Observer<ReviewListModel> {
                    loadingObservable.value = false
                    if (!it.responseData.review!!.isEmpty()) {
                        val reviewList: MutableList<ReviewListModel.ResponseData.Review> = mutableListOf()
                        var i = 0
                        no_content_txt.visibility = GONE
                        if (it.responseData.review.size > 3) {
                            see_more_txt.visibility = VISIBLE
                            it.responseData.review.forEach {
                                i++
                                when (i) {
                                    in 4..10 -> return@forEach
                                    else -> {
                                        val newReviewList: MutableList<ReviewListModel.ResponseData.Review> = mutableListOf()
                                        newReviewList.add(it)
                                        reviewList.addAll(newReviewList)
                                    }
                                }
                            }
                        } else
                            reviewList.addAll(it.responseData.review)
                        providerDetailActivityBinding.providerReviewAdapter = ProviderReviewsAdapter(this@ProviderDetailActivity, reviewList)
                    } else {
                        no_content_txt.visibility = VISIBLE
                    }
                })
        see_more_txt.setOnClickListener { openNewActivity(this, ProviderReviewsActivity::class.java, false) }
    }

    private fun setProviderProfileDetails() {
        Glide.with(baseContext).load(XuberServiceClass.providerListModel.picture)
                .placeholder(R.drawable.ic_profile_place_holder).into(provider_img)
        provider_name_tv.text = XuberServiceClass.providerListModel.first_name
        provider_base_fare_tv.text = getPrice(XuberServiceClass.providerListModel, this)
        provider_distance_tv.text = DecimalFormat("##.##")
                .format(XuberServiceClass.providerListModel.distance.toDouble()) + getString(R.string.km_away)
        provider_rating_tv.text = XuberServiceClass.providerListModel.rating
    }

    override fun nextClick() {
        val intent = Intent(this, ConfirmBookingActivity::class.java)
        startActivity(intent)
        finish()
//        openNewActivity(this, ConfirmBookingActivity::class.java, true)
    }

    override fun onBackPressed() {
        openNewActivity(this, ProvidersActivity::class.java, true)
    }

}

