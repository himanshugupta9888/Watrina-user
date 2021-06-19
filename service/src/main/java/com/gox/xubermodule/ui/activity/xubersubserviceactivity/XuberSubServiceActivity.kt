package com.gox.xubermodule.ui.activity.xubersubserviceactivity

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.SubServiceModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ActivitySubservicelayoutBinding
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainActivity
import com.gox.xubermodule.ui.activity.selectlocation.SelectLocationActivity
import com.gox.xubermodule.ui.adapter.XuberSubServiceAdapter
import com.gox.xubermodule.ui.fragment.scheduleride.ScheduleFragment
import kotlinx.android.synthetic.main.toolbar_location_pick.ivBack
import kotlinx.android.synthetic.main.toolbar_service_category.*

class XuberSubServiceActivity : BaseActivity<ActivitySubservicelayoutBinding>(), XuberSubServiceNavigator {
    private lateinit var mActivitySubservicelayoutBinding: ActivitySubservicelayoutBinding
    lateinit var xuberSubServiceViewModel: XuberSubServiceViewModel
    var subServiceList: ArrayList<SubServiceModel.ResponseData?>? = null
    private val preference = PreferenceHelper(BaseApplication.baseApplication)
    override fun getLayoutId(): Int = R.layout.activity_subservicelayout

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        xuberSubServiceViewModel = XuberSubServiceViewModel()
        mActivitySubservicelayoutBinding = mViewDataBinding as ActivitySubservicelayoutBinding
        mActivitySubservicelayoutBinding.xuberSubServiceViewModel = xuberSubServiceViewModel
        xuberSubServiceViewModel.navigator = this

        xuberSubServiceViewModel.getServiceCategory(XuberServiceClass.mainServiceID,preference.getValue(PreferenceKey.DEFAULTCITYID,"") as String)
        loadingObservable.value = true
        subServiceList = ArrayList()
        xuberSubServiceViewModel.subServiceResponse.observe(this@XuberSubServiceActivity,
                Observer<SubServiceModel> {
                    xuberSubServiceViewModel.loadingProgress.value = false
                    hideLoading()
                    subServiceList?.clear()
                    if (!it.responseData!!.isEmpty()) {
                        for (i in 0 until it.responseData.size) {
                            if (it.responseData[i]?.service_city != null) {
                                subServiceList!!.add(it.responseData[i])
                            }

                        }
                        setSubServiceAdapter(subServiceList!!)
                        if (subServiceList!!.size == 0) {
                            mActivitySubservicelayoutBinding.noServicesTv.visibility = View.VISIBLE
                            mActivitySubservicelayoutBinding.rvXuberSubservice.visibility = View.GONE
                            mActivitySubservicelayoutBinding.noServicesTv.text = getText(R.string.no_services)
                        } else {
                            mActivitySubservicelayoutBinding.noServicesTv.visibility = View.GONE
                            mActivitySubservicelayoutBinding.rvXuberSubservice.visibility = View.VISIBLE
                        }
                    }else{
                        mActivitySubservicelayoutBinding.noServicesTv.visibility = View.VISIBLE
                        mActivitySubservicelayoutBinding.rvXuberSubservice.visibility = View.GONE
                        mActivitySubservicelayoutBinding.noServicesTv.text = getText(R.string.no_services)
                    }

                })


        service_name.text = XuberServiceClass.mainServiceName
        ivBack.setOnClickListener { onBackPressed() }
    }

    private fun setSubServiceAdapter(responseData: List<SubServiceModel.ResponseData?>) {
        mActivitySubservicelayoutBinding.subServiceAdapter = XuberSubServiceAdapter(this@XuberSubServiceActivity
                , responseData)
    }

    override fun bookNowService() {
        when (getSelectedService()) {
            null -> {
                ViewUtils.showToast(this, getString(R.string.select_service), false)
            }
            else -> {
                if(getSelectedService()?.service_city?.allow_quantity == 1 && getSelectedService()?.service_city?.quantity!! == 0){
                    ViewUtils.showToast(this, getString(R.string.select_minimum_quantity), false)
                    return
                }
                val intent = Intent(this, SelectLocationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getSelectedService(): SubServiceModel.ResponseData? {
        var data: SubServiceModel.ResponseData? = null
        mActivitySubservicelayoutBinding.subServiceAdapter!!.subserviceData.forEach {
            when (it!!.selected) {
                "1" -> {
                    data = it
                    XuberServiceClass.subServiceName = data!!.service_name as String
                    XuberServiceClass.subServiceID = data!!.id as Int
                    if (data!!.service_city != null) {
                        XuberServiceClass.quantity = data!!.service_city!!.quantity.toString()
                        XuberServiceClass.baseFare = data!!.service_city!!.base_fare.toString()
                        XuberServiceClass.fareType = data!!.service_city!!.fare_type.toString()
                        XuberServiceClass.allowDesc = data!!.allow_desc.toString()
                        XuberServiceClass.allowQuantity = data!!.service_city!!.allow_quantity.toString()
                    }
                }
            }
        }
        return data
    }

    override fun scheduleService() {
        when (getSelectedService()) {
            null -> {
                ViewUtils.showToast(this, getString(R.string.select_service), false)
            }
            else -> {
                val mScheduleFragment = ScheduleFragment.newInstance()
                mScheduleFragment.show(supportFragmentManager, mScheduleFragment.tag)
            }
        }
    }

    override fun logoit(itTrue: Boolean) {
        ViewUtils.showAlert(this, R.string.gust_message,object : ViewCallBack.Alert{
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                val intent = Intent(this@XuberSubServiceActivity,Class.forName("com.gox.app.ui.dashboard.SignupActivity"))
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("EXIT", true)
                startActivity(intent)
                dialog.dismiss()
            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {

            }

        })
    }


    override fun onBackPressed() {
        openNewActivity(this, XuberMainActivity::class.java, true)
    }
}
