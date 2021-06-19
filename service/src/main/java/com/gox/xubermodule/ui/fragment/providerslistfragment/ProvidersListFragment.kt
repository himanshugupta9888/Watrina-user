package com.gox.xubermodule.ui.fragment.providerslistfragment

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseFragment
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ProviderListModel
import com.gox.xubermodule.databinding.ProviderListFragmentBinding
import com.gox.xubermodule.ui.activity.provierlistactivity.ProvidersViewModel
import com.gox.xubermodule.ui.adapter.ProviderListAdapter
import kotlinx.android.synthetic.main.provider_list_fragment.*


class ProvidersListFragment : BaseFragment<ProviderListFragmentBinding>() {

    lateinit var providerListFragmentBinding: ProviderListFragmentBinding
    override fun getLayoutId(): Int = R.layout.provider_list_fragment
    lateinit var providersListViewModel: ProvidersListViewModel
    lateinit var providersActivityViewModel: ProvidersViewModel
    private var mProviderListAdapter: ProviderListAdapter? = null
    private var mProviderList: ArrayList<ProviderListModel.ResponseData.ProviderService>? = null
    private var temp: ArrayList<ProviderListModel.ResponseData.ProviderService>? = null


    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        providerListFragmentBinding = mViewDataBinding as ProviderListFragmentBinding
        providersListViewModel = ProvidersListViewModel()
        providersActivityViewModel = ViewModelProviders.of(activity!!).get(ProvidersViewModel::class.java)
        mProviderList = ArrayList()
        temp = ArrayList()
        providersActivityViewModel.getProviderList()
        providerListFragmentBinding.svProviders.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (mProviderList!!.size > 0) {
                    filter(newText)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })

        providersActivityViewModel.getProviderResponse().observe(this@ProvidersListFragment,
                Observer<ProviderListModel> { if (it != null) {
                        if (it.statusCode == "200") {
                            if (!it.responseData!!.provider_service!!.isEmpty()) {
                                no_nearby_txt.visibility = GONE
                                mProviderList!!.clear()
                                // providersActivityViewModel.providerListData.value = it.responseData
                                mProviderList!!.addAll(it.responseData.provider_service!!)
                                mProviderListAdapter = ProviderListAdapter(activity, mProviderList!!)
                                providerListFragmentBinding.providerListAdapter = mProviderListAdapter
                                providerListFragmentBinding.providerListAdapter!!.notifyDataSetChanged()
                            } else {
                                no_nearby_txt.visibility = VISIBLE
                            }
                        }
                    }
                })

        providersListViewModel.loaderLiveData.observe(this, Observer {
            baseLiveDataLoading.value=it
        })
    }

    private fun filter(text: String) {
        temp!!.clear()
        for (d in mProviderList!!) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.first_name.toLowerCase().contains(text)) {
                temp!!.add(d)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        mProviderListAdapter!!.filterList(temp!!)
    }


}
