package com.gox.app.ui.cityListActivity

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.adapter.CityListAdapter
import com.gox.app.data.repositary.remote.model.City
import com.gox.app.databinding.ActivityCitylistBinding


class CityListActivity : BaseActivity<ActivityCitylistBinding>() {

    lateinit var mViewDataBinding: ActivityCitylistBinding
    override fun getLayoutId(): Int = R.layout.activity_citylist

    override fun initView(mViewDataBinding: ViewDataBinding?) {


        this.mViewDataBinding = mViewDataBinding as ActivityCitylistBinding
        this.mViewDataBinding.inputSearch.hint = getString(R.string.search_city)

        val cityList = intent.getSerializableExtra("citylistresponse") as ArrayList<City>

        mViewDataBinding.cityListAdapter = CityListAdapter(this, cityList)



        mViewDataBinding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length>2)
                    mViewDataBinding.cityListAdapter!!.filter.filter(s)
            }

        })
    }


}