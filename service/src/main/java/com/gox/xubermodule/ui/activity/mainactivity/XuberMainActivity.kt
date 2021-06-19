package com.gox.xubermodule.ui.activity.mainactivity

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.base.BaseActivity
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.SubCategoryModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ActivityXuberMainBinding
import com.gox.xubermodule.ui.activity.serviceflowactivity.ServiceFlowActivity
import com.gox.xubermodule.ui.adapter.XuberServiceCategoryAdapter
import kotlinx.android.synthetic.main.toolbar_location_pick.ivBack
import kotlinx.android.synthetic.main.toolbar_service_category.*

class XuberMainActivity : BaseActivity<ActivityXuberMainBinding>(), XuberMainNavigator {

    private lateinit var mActivityXuberMainBinding: ActivityXuberMainBinding
    private lateinit var xuberMainModel: XuberMainModel

    override fun getLayoutId(): Int = R.layout.activity_xuber_main

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        mActivityXuberMainBinding = mViewDataBinding as ActivityXuberMainBinding
        xuberMainModel = XuberMainModel()
        xuberMainModel.navigator = this
        xuberMainModel.getCheckRequest()
        xuberMainModel.getServiceCategory(XuberServiceClass.serviceID)
        loadingObservable.value = true




        xuberMainModel.checkRequestResponse.observe(this, Observer {
            if (it.statusCode == "200") {
                if (it.responseData!!.data!!.size > 0) {

                    openNewActivity(this, ServiceFlowActivity::class.java, true)
                }

            }
        })

        xuberMainModel.subCategoryResponse.observe(this@XuberMainActivity,
                Observer<SubCategoryModel> {
                    xuberMainModel.loadingProgress.value = false
                    hideLoading()
                    if (!it.responseData!!.isEmpty()) {
                        mViewDataBinding.emptyViewLayout.visibility = View.GONE
                        mViewDataBinding.rvXuberServiceCategory.visibility = View.VISIBLE
                        setSubCategoryData(it.responseData)
                    } else {
                        mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                        mViewDataBinding.rvXuberServiceCategory.visibility = View.GONE
                    }

                })

        service_name.text = XuberServiceClass.serviceName
        ivBack.setOnClickListener {
            onBackPressed()
        }


    }


    private fun setSubCategoryData(responseData: List<SubCategoryModel.SubCategoryData?>) {
        mActivityXuberMainBinding.categoryAdapter = XuberServiceCategoryAdapter(this@XuberMainActivity,
                responseData)

    }


    override fun onResume() {
        super.onResume()
        var extras = intent.extras
        if(extras?.containsKey("finish") == true){
            finish()
        }
    }


    override fun onBackPressed() {
        finish()
    }


}
