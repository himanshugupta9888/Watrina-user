package com.gox.foodiemodule.ui.searchResturantActivity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.FoodieItemClickListner
import com.gox.foodiemodule.adapter.RestaurantListAdapter
import com.gox.foodiemodule.data.model.ResturantListModel
import com.gox.foodiemodule.databinding.ActivitySearchLayoutBinding
import com.gox.foodiemodule.ui.resturantdetail_activity.RestaurantDetailActivity

class SearchRestaturantsActivity : BaseActivity<ActivitySearchLayoutBinding>(), SearchRestaturantsNavigator {


    lateinit var mViewDataBinding: ActivitySearchLayoutBinding
    lateinit var mSearchRestaturantsViewModel: SearchRestaturantsViewModel
    override fun getLayoutId(): Int = R.layout.activity_search_layout
    private var mResturantList: ArrayList<ResturantListModel.ResponseData?>? = null
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)
    var selectedSearch = "store"


    override fun initView(mViewDataBinding: ViewDataBinding?) {

        mSearchRestaturantsViewModel = ViewModelProviders.of(this).get(SearchRestaturantsViewModel::class.java)
        this.mViewDataBinding = mViewDataBinding as ActivitySearchLayoutBinding

        mViewDataBinding.restaturantsListAdapter = RestaurantListAdapter(this, emptyList(), selectedSearch)
        mResturantList = ArrayList()
        if(preferenceHelper.getValue("storetype", "") == "FOOD")
            mViewDataBinding.searchDishes.text = getString(R.string.dishes)
        else
            mViewDataBinding.searchDishes.text = getString(R.string.items)

        mViewDataBinding.searchResturant.setOnClickListener {

            selectedSearch = "store"
            mViewDataBinding.searchResturant.setTextColor(ContextCompat.getColor(this, R.color.foodie_red))
            mViewDataBinding.searchDishes.setTextColor(ContextCompat.getColor(this, R.color.greay))
            if (mViewDataBinding.searchEt.text.toString().length > 2) {
                mSearchRestaturantsViewModel.getSearchResturantList(mViewDataBinding.searchEt.text.toString()
                        , selectedSearch)
            } else {

            }
        }
        mViewDataBinding.searchDishes.setOnClickListener {
            selectedSearch = "dish"
            mViewDataBinding.searchDishes.setTextColor(ContextCompat.getColor(this, R.color.foodie_red))
            mViewDataBinding.searchResturant.setTextColor(ContextCompat.getColor(this, R.color.greay))
            if (mViewDataBinding.searchEt.text.toString().length > 2) {
                mSearchRestaturantsViewModel.getSearchResturantList(mViewDataBinding.searchEt.text.toString()
                        , selectedSearch)
            } else {

            }

        }

        mViewDataBinding.restImg.setOnClickListener {
            mViewDataBinding.searchEt.setText("")

        }

        mViewDataBinding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 2) {
                    mSearchRestaturantsViewModel.getSearchResturantList(s.toString(), selectedSearch)
                    mViewDataBinding.restImg.visibility = View.VISIBLE
                } else {
                    mViewDataBinding.emptyViewLl.visibility = View.VISIBLE
                    mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.GONE
                    mViewDataBinding.restImg.visibility = View.GONE
                }
            }
        })


        mSearchRestaturantsViewModel.resturantListResponse.observe(this@SearchRestaturantsActivity,
                Observer<ResturantListModel> {
                    mSearchRestaturantsViewModel.loadingProgress.value = false
                    hideLoading()
                    if (!it.responseData!!.isEmpty()) {
                        mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.VISIBLE
                        mViewDataBinding.emptyViewLl.visibility = View.GONE
                        setResturantList(it.responseData)


                    } else {
                        mViewDataBinding.emptyViewLl.visibility = View.VISIBLE
                        mViewDataBinding.resturantsListAdapterFrghomeRv.visibility = View.GONE

                    }

                })
    }

    override fun goToResturantDetail() {
    }


    private fun setResturantList(resturantList: List<ResturantListModel.ResponseData?>) {
        mResturantList!!.addAll(resturantList)

        mViewDataBinding.restaturantsListAdapter = RestaurantListAdapter(this, resturantList, selectedSearch)
        mViewDataBinding.restaturantsListAdapter!!.setOnClickListener(mOnAdapterClickListener)

        mViewDataBinding.restaturantsListAdapter!!.notifyDataSetChanged()
    }


    private val mOnAdapterClickListener = object : FoodieItemClickListner {
        override fun restutantItemClick(position: Int) {

            val resturantList = mResturantList!![position]
            if (!resturantList!!.shopstatus.equals("closed", true)) {
                val intent = Intent(this@SearchRestaturantsActivity, RestaurantDetailActivity::class.java)
                intent.putExtra("restaurantsId", resturantList.store_id.toString())
                startActivity(intent)
            }
        }


    }


}

