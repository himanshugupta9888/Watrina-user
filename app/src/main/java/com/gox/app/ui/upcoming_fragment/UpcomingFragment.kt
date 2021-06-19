package com.gox.app.ui.upcoming_fragment

import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gox.app.R
import com.gox.app.adapter.UpcomingOrdersAdapter
import com.gox.app.data.repositary.remote.model.TransportHistory
import com.gox.app.data.repositary.remote.model.TransportResponseData
import com.gox.app.databinding.FragmentUpcomingOrdersBinding
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.xubermodule.utils.PaginationScrollListener

class UpcomingFragment : BaseFragment<FragmentUpcomingOrdersBinding>(), UpComingOrderNavigator {


    lateinit var mViewDataBinding: FragmentUpcomingOrdersBinding
    override fun getLayoutId(): Int = R.layout.fragment_upcoming_orders
    var transportResponseData: TransportResponseData = TransportResponseData(0, mutableListOf(), mutableListOf(), mutableListOf(), "")
    private var offset = 0
    private var loadMore = true
    lateinit var upcomingFragmentViewmodel:UpcomingFragmentViewmodel
    lateinit var userDashboardViewModel:UserDashboardViewModel

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as FragmentUpcomingOrdersBinding
        userDashboardViewModel = ViewModelProviders.of(activity!!).get(UserDashboardViewModel::class.java)
        upcomingFragmentViewmodel = UpcomingFragmentViewmodel()


        mViewDataBinding.upcomingfragmentviewmodel = upcomingFragmentViewmodel

        showLoading()

        upcomingFragmentViewmodel.upComingHistoryResponse.observe(this@UpcomingFragment,
                Observer<TransportHistory> {
                    upcomingFragmentViewmodel.loadingProgress.value = false
                    hideLoading()
                    if (it.responseData.type.equals(Constant.ModuleTypes.TRANSPORT, true) && !it.responseData.transport.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.transport.addAll(it.responseData.transport)
                        setUpcomingHistoryAdapter(Constant.ModuleTypes.TRANSPORT)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.SERVICE, true) && !it.responseData.service.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.service.addAll(it.responseData.service)
                        setUpcomingHistoryAdapter(Constant.ModuleTypes.SERVICE)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.ORDER, true) && !it.responseData.order.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.order.addAll(it.responseData.order)
                        setUpcomingHistoryAdapter(Constant.ModuleTypes.ORDER)
                    }
                    when (transportResponseData.order.size + transportResponseData.service.size + transportResponseData.transport.size > 0) {
                        false -> {
                            this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                            this.mViewDataBinding.upcomingOrdersfrgRv.visibility = View.GONE
                        }
                    }
                })

        upcomingFragmentViewmodel.errorResponse.observe(this@UpcomingFragment,
                Observer<String> { error ->
                    hideLoading()
//                    this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                    Log.d("_D", error + "")
//                    ViewUtils.showToast(activity as Context, error, false)
                })

        mViewDataBinding.upcomingOrdersfrgRv.addOnScrollListener(object : PaginationScrollListener(mViewDataBinding.upcomingOrdersfrgRv.layoutManager as LinearLayoutManager) {
            override fun isLastPage() = false
            override fun isLoading() = false
            override fun loadMoreItems() {
                when (loadMore) {
                    true -> {
                        upcomingFragmentViewmodel.getTransportUpcomingHistory(userDashboardViewModel.selectedFilterService.value!!.toLowerCase(),offset.toString())
                        loadMore = false
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        upcomingFragmentViewmodel.getTransportUpcomingHistory(userDashboardViewModel.selectedFilterService.value!!.toLowerCase(),offset.toString())
    }

    override fun goToDetailPage() {
    }

    private fun setUpcomingHistoryAdapter(servicetype: String) {
        this.mViewDataBinding.upcomingOrderAdapter = UpcomingOrdersAdapter(activity,
                transportResponseData,servicetype)

    }

}
