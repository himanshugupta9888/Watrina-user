package com.gox.xubermodule.ui.activity.selectlocation

import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.base.BaseActivity
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.Addresses
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ActivitySelectLocationBinding
import com.gox.xubermodule.ui.activity.locationpick.ServiceLocationPickActivity
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersActivity
import com.gox.xubermodule.ui.activity.xubersubserviceactivity.XuberSubServiceActivity
import com.gox.xubermodule.ui.adapter.OnViewClickListener
import com.gox.xubermodule.ui.adapter.XuberAddressAdapter
import kotlinx.android.synthetic.main.activity_select_location.*
import kotlinx.android.synthetic.main.toolbar_service_category.*

class SelectLocationActivity : BaseActivity<ActivitySelectLocationBinding>(), SelectLocationNavigator {

    private lateinit var activitySelectLocationBinding: ActivitySelectLocationBinding
    private lateinit var selectLocationViewModel: SelectLocationViewModel
    private lateinit var mAddressList: ArrayList<Addresses>
    override fun getLayoutId() = R.layout.activity_select_location

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        selectLocationViewModel = SelectLocationViewModel()
        activitySelectLocationBinding = mViewDataBinding as ActivitySelectLocationBinding
        activitySelectLocationBinding.selectLocationViewModel = selectLocationViewModel
        selectLocationViewModel.navigator = this
        ivBack.setOnClickListener { onBackPressed() }
        service_name.text = getString(R.string.select_location)
        mAddressList = ArrayList()
        selectLocationViewModel.getAddressList()
        getObserverValues()


        /*rlHomeAddressContainer.setOnClickListener {
            val res = rlHomeAddressContainer.tag.toString().split(",")
            XuberServiceClass.sourceLat = res[0]
            XuberServiceClass.sourceLng = res[1]
            XuberServiceClass.address = tvHomeAddress.text.toString()
            gotoProviderListing()
        }
        rlWorkAddressContainer.setOnClickListener {
            val res = rlWorkAddressContainer.tag.toString().split(",")
            XuberServiceClass.sourceLat = res[0]
            XuberServiceClass.sourceLng = res[1]
            XuberServiceClass.address = tvWorkAddress.text.toString()
            gotoProviderListing()
        }*/
        loc_lt.setOnClickListener {
            val intent = Intent(this, ServiceLocationPickActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


     fun getObserverValues(){

         selectLocationViewModel.loaderLiveData.observe(this, Observer {
             loadingObservable.value=it
         })

         selectLocationViewModel.getAddressesResponse().observe(this, Observer {
             if (it != null) {
                 if (it.statusCode == "200") {
                     mAddressList.addAll(it.responseData)

                     val adapter = XuberAddressAdapter(mAddressList)
                     activitySelectLocationBinding.xuberAdapter = adapter
                     adapter.setOnClickListener(mClickListener)
                     adapter.notifyDataSetChanged()

                     when (it.responseData.size) {
                         0 -> {
                             rcvAddress.visibility = View.GONE
                             llEmptyView.visibility = View.VISIBLE
                         }
                         else -> {
                             rcvAddress.visibility = View.VISIBLE
                             llEmptyView.visibility = View.GONE

                             /* mAddressList.forEach {
                                  when (it.address_type) {
                                      "Home" -> {
                                          rlHomeAddressContainer.visibility = View.VISIBLE
                                          tvHomeAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else it.flat_no}" +
                                                  " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                                  " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                                  " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}" +
                                                  " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                                          rlHomeAddressContainer.setTag(it.latitude + "," + it.longitude)
                                      }
                                      "Work" -> {
                                          rlWorkAddressContainer.visibility = View.VISIBLE
                                          tvWorkAddress.text = "${if (TextUtils.isEmpty(it.flat_no)) "" else it.flat_no}" +
                                                  " ${if (TextUtils.isEmpty(it.street)) "" else ", " + it.street}" +
                                                  " ${if (TextUtils.isEmpty(it.city)) "" else ", " + it.city}" +
                                                  " ${if (TextUtils.isEmpty(it.state)) "" else ", " + it.state}" +
                                                  " ${if (TextUtils.isEmpty(it.county)) "" else ", " + it.county}"
                                          rlWorkAddressContainer.setTag(it.latitude + "," + it.longitude)
                                      }
                                  }
                              }*/
                         }
                     }
                 }
             } else
                 loc_txt.text = getString(R.string.no_saved_location)
         })


     }
    private val mClickListener:OnViewClickListener = object:OnViewClickListener{
        override fun onClick(position: Int) {
            val data = mAddressList[position]
            XuberServiceClass.sourceLat = data.latitude
            XuberServiceClass.sourceLng = data.longitude
            var address = getAddress(data)
            XuberServiceClass.address = address
            gotoProviderListing()
        }

        private fun getAddress(data: Addresses): String {
            var address = ""

            data.flat_no.let {
                address = it
            }

            data.street.let {
                address = if (address.isNotEmpty())
                    "${address},$it"
                else
                    it
            }

            data.landmark.let {
                address = if (address.isNotEmpty())
                    "${address},$it"
                else
                    it
            }
            return address
        }
    }

    fun gotoProviderListing() {
        val intent = Intent(this, ProvidersActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        openNewActivity(this, XuberSubServiceActivity::class.java, true)
    }
}
