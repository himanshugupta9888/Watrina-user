package com.gox.xubermodule.ui.activity.providerreviews

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gox.basemodule.base.BaseActivity
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ReviewListModel
import com.gox.xubermodule.databinding.ActivityProviderReviewsBinding
import com.gox.xubermodule.ui.activity.providerdetail.ProviderDetailActivity
import com.gox.xubermodule.ui.adapter.ProviderReviewsAdapter
import com.gox.xubermodule.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_provider_reviews.*
import kotlinx.android.synthetic.main.toolbar_service_category.*

class ProviderReviewsActivity : BaseActivity<ActivityProviderReviewsBinding>(), ProviderReviewsNavigator {

    private lateinit var providerReviewsBinding: ActivityProviderReviewsBinding
    private var providerReviewViewModel: ProviderReviewViewModel = ProviderReviewViewModel()
    private var offset = 0
    private var loadMore = true
    private val reviewList: MutableList<ReviewListModel.ResponseData.Review> = mutableListOf()

    override fun getLayoutId() = R.layout.activity_provider_reviews

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        providerReviewsBinding = mViewDataBinding as ActivityProviderReviewsBinding
        providerReviewsBinding.providerReviewViewModel = providerReviewViewModel
        providerReviewViewModel.navigator = this
        loadingObservable.value = true
        ivBack.setOnClickListener { onBackPressed() }
        service_name.text = getString(R.string.reviews)
        review_list_rv.adapter = ProviderReviewsAdapter(this@ProviderReviewsActivity, reviewList)
        providerReviewViewModel.getProviderReviews(offset.toString())
        providerReviewViewModel.getReviewResponse().observe(this@ProviderReviewsActivity,
                Observer<ReviewListModel> {
                    loadingObservable.value = false
                    if (it.responseData.review!!.size > 0) {
                        reviewList.addAll(it.responseData.review)
                        loadMore = true
                        offset += 10
                    }
                    (review_list_rv.adapter as ProviderReviewsAdapter).reviewListModel = reviewList
                    (review_list_rv.adapter as ProviderReviewsAdapter).notifyDataSetChanged()
                })
        review_list_rv.addOnScrollListener(object : PaginationScrollListener(review_list_rv.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return false
            }

            override fun isLoading(): Boolean {
                return false
            }

            override fun loadMoreItems() {
                when (loadMore) {
                    true -> {
                        providerReviewViewModel.getProviderReviews(offset.toString())
                        loadMore = false
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        openNewActivity(this,ProviderDetailActivity::class.java,true)
    }
}
