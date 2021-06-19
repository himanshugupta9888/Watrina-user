package com.gox.app.ui.stateListActivity

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ViewDataBinding
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.adapter.StateListAdapter
import com.gox.app.data.repositary.remote.model.StateListResponse
import com.gox.app.databinding.ActivityStatelistBinding


class StateListActivity : BaseActivity<ActivityStatelistBinding>() {

    lateinit var mViewDataBinding: ActivityStatelistBinding
    override fun getLayoutId(): Int = R.layout.activity_statelist

    override fun initView(mViewDataBinding: ViewDataBinding?) {


        this.mViewDataBinding = mViewDataBinding as ActivityStatelistBinding
        this.mViewDataBinding.inputSearch.hint = getString(R.string.search_state)

        val stateList = intent.getSerializableExtra("statelistresponse") as StateListResponse
        mViewDataBinding.stateListAdapter = StateListAdapter(this, stateList.responseData)



        mViewDataBinding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length > 2)
                    mViewDataBinding.stateListAdapter!!.filter.filter(s)

            }

        })
    }


}