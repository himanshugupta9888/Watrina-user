package com.gox.app.ui.currentorder_fragment

import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gox.app.R
import com.gox.app.adapter.CurrentOrdersAdapter
import com.gox.app.data.repositary.remote.model.TransportHistory
import com.gox.app.data.repositary.remote.model.TransportResponseData
import com.gox.app.databinding.FragmentCurrentOrdersBinding
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.Constant
import com.gox.xubermodule.utils.PaginationScrollListener

class CurrentOrderFragment : BaseFragment<FragmentCurrentOrdersBinding>(), CurrentOrderNavigator {

    lateinit var mViewDataBinding: FragmentCurrentOrdersBinding
    override fun getLayoutId(): Int = R.layout.fragment_current_orders
    var transportResponseData: TransportResponseData = TransportResponseData(0, mutableListOf(), mutableListOf(), mutableListOf(), "")
    private var offset = 0
    private var loadMore = true

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as FragmentCurrentOrdersBinding
        val currentOrderViewModel = ViewModelProviders.of(this).get(CurrentOrderViewModel::class.java)
        val userDashboardViewModel = ViewModelProviders.of(activity!!).get(UserDashboardViewModel::class.java)
        currentOrderViewModel.navigator = this
        this.mViewDataBinding.currentorderviewmodel = currentOrderViewModel

        currentOrderViewModel.getTransportCurrentHistory(userDashboardViewModel
                .selectedFilterService.value!!.toLowerCase(), offset.toString())

        showLoading()

        currentOrderViewModel.transportCurrentHistoryResponse.observe(this@CurrentOrderFragment,
                Observer<TransportHistory> {
                    currentOrderViewModel.loadingProgress.value = false
                    hideLoading()
                    if (it.responseData.type.equals(Constant.ModuleTypes.TRANSPORT, true) && !it.responseData.transport.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.transport.addAll(it.responseData.transport)
                        setCurrentTransportHistoryAdapter(Constant.ModuleTypes.TRANSPORT)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.SERVICE, true) && !it.responseData.service.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.service.addAll(it.responseData.service)
                        setCurrentTransportHistoryAdapter(Constant.ModuleTypes.SERVICE)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.ORDER, true) && !it.responseData.order.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.order.addAll(it.responseData.order)
                        setCurrentTransportHistoryAdapter(Constant.ModuleTypes.ORDER)
                    }
                    when (transportResponseData.order.size + transportResponseData.service.size + transportResponseData.transport.size > 0) {
                        false -> {
                            this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                            this.mViewDataBinding.currentOrdersfrgRv.visibility = View.GONE
                        }
                    }

                })

        currentOrderViewModel.errorResponse.observe(this@CurrentOrderFragment, Observer<String> { error ->
            hideLoading()
//            this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE

            Log.d("_D", error + "")
//            ViewUtils.showToast(activity as Context, error, false)

        })

        mViewDataBinding.currentOrdersfrgRv.addOnScrollListener(object : PaginationScrollListener(mViewDataBinding.currentOrdersfrgRv.layoutManager as LinearLayoutManager) {
            override fun isLastPage() = false
            override fun isLoading() = false
            override fun loadMoreItems() {
                when (loadMore) {
                    true -> {
                        currentOrderViewModel.getTransportCurrentHistory(userDashboardViewModel.selectedFilterService.value!!.toLowerCase(), offset.toString())
                        loadMore = false
                    }
                }
            }
        })
    }

    private fun setCurrentTransportHistoryAdapter(serviceType: String) {
        this.mViewDataBinding.currrentOrdersAdapter = CurrentOrdersAdapter(activity,
                transportResponseData, serviceType)
    }


    override fun goToDetailPage() {
    }

}