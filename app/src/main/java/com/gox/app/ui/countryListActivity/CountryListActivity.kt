package com.gox.app.ui.countryListActivity

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.adapter.CountryListAdapter
import com.gox.app.data.repositary.remote.model.CountryListResponse
import com.gox.app.databinding.ActivityCountrylistBinding
import kotlinx.android.synthetic.main.toolbar_layout.view.*


class CountryListActivity : BaseActivity<ActivityCountrylistBinding>() {

    lateinit var mViewDataBinding: ActivityCountrylistBinding
    override fun getLayoutId(): Int = R.layout.activity_countrylist

    override fun initView(mViewDataBinding: ViewDataBinding?) {


        this.mViewDataBinding = mViewDataBinding as ActivityCountrylistBinding

        val countrylist = intent.getSerializableExtra("countrylistresponse") as CountryListResponse
        mViewDataBinding.countryListAdapter = CountryListAdapter(this, countrylist.responseData)
        mViewDataBinding.toolbar.toolbar_back_img.setOnClickListener {
            finish()
        }


        mViewDataBinding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 2)
                    mViewDataBinding.countryListAdapter!!.filter.filter(s)
                else
                    mViewDataBinding.countryListAdapter!!.notifyDataSetChanged()
            }

        })
    }


}