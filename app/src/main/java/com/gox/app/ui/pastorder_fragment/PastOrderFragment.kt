package com.gox.app.ui.pastorder_fragment

import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gox.basemodule.base.BaseFragment
import com.gox.app.R
import com.gox.app.adapter.PastOrdersAdapter
import com.gox.app.data.repositary.remote.model.TransportHistory
import com.gox.app.data.repositary.remote.model.TransportResponseData
import com.gox.app.databinding.FragmentPastOrdersBinding
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.basemodule.data.Constant
import com.gox.xubermodule.utils.PaginationScrollListener

class PastOrderFragment : BaseFragment<FragmentPastOrdersBinding>(), PastOrderNavigator {


    lateinit var mViewDataBinding: FragmentPastOrdersBinding
    override fun getLayoutId(): Int = R.layout.fragment_past_orders;
    var transportResponseData: TransportResponseData = TransportResponseData(0, mutableListOf(), mutableListOf(), mutableListOf(), "")
    private var offset = 0
    private var loadMore = true

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as FragmentPastOrdersBinding
        val pastOrderViewModel = PastOrderViewModel()
        val userDashboardViewModel = ViewModelProviders.of(activity!!).get(UserDashboardViewModel::class.java)
        pastOrderViewModel.navigator = this
        mViewDataBinding.pastfragmentviewmodel = pastOrderViewModel

        pastOrderViewModel.getTransportPastHistory(userDashboardViewModel.selectedFilterService.value!!.toLowerCase(), offset.toString())
        showLoading()

        pastOrderViewModel.transportHistoryResponse.observe(this@PastOrderFragment,
                Observer<TransportHistory> {
                    pastOrderViewModel.loadingProgress.value = false
                    hideLoading()
                    if (it.responseData.type.equals(Constant.ModuleTypes.TRANSPORT, true) && !it.responseData.transport.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.transport.addAll(it.responseData.transport)
                        setTransportHistoryAdapter(Constant.ModuleTypes.TRANSPORT)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.SERVICE, true) && !it.responseData.service.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.service.addAll(it.responseData.service)
                        setTransportHistoryAdapter(Constant.ModuleTypes.SERVICE)
                    } else if (it.responseData.type.equals(Constant.ModuleTypes.ORDER, true) && !it.responseData.order.isEmpty()) {
                        loadMore = true
                        offset += 10
                        transportResponseData.order.addAll(it.responseData.order)
                        setTransportHistoryAdapter(Constant.ModuleTypes.ORDER)
                    }
                    when (transportResponseData.order.size + transportResponseData.service.size + transportResponseData.transport.size > 0) {
                        false -> {
                            this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                            this.mViewDataBinding.pastOrdersfrgRv.visibility = View.GONE
                        }
                    }
                })

        pastOrderViewModel.errorResponse.observe(this@PastOrderFragment, Observer<String> { error ->
            hideLoading()
            when (transportResponseData.order.size + transportResponseData.service.size + transportResponseData.transport.size > 0) {
                false -> {
                    this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                    this.mViewDataBinding.pastOrdersfrgRv.visibility = View.GONE
                }
            }
            Log.d("_D", error + "")
//            ViewUtils.showToast(activity as Context, error, false)
        })

        mViewDataBinding.pastOrdersfrgRv.addOnScrollListener(object : PaginationScrollListener(mViewDataBinding.pastOrdersfrgRv.layoutManager as LinearLayoutManager) {
            override fun isLastPage() = false
            override fun isLoading() = false
            override fun loadMoreItems() {
                when (loadMore) {
                    true -> {
                        pastOrderViewModel.getTransportPastHistory(userDashboardViewModel.selectedFilterService.value!!.toLowerCase(), offset.toString())
                        loadMore = false
                    }
                }
            }
        })
    }

    private fun setTransportHistoryAdapter(servicetype: String) {
        this.mViewDataBinding.pastOrdersAdapter = PastOrdersAdapter(activity,
                transportResponseData, servicetype)
    }

    override fun gotoDetailPage() {

    }
}
