package com.gox.basemodule.common.payment.transaction

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.databinding.FragmentBaseTransactionBinding
import com.gox.basemodule.common.payment.adapter.TransactionListAdapter
import com.gox.basemodule.common.payment.managepayment.ManagePaymentViewModel
import com.gox.basemodule.common.payment.model.WalletTransactionList
import kotlinx.android.synthetic.main.fragment_base_transaction.*

class TransactionFragment : BaseFragment<FragmentBaseTransactionBinding>(), TransactionNavigator {
    private lateinit var fragmentTransactionBinding: FragmentBaseTransactionBinding
    private lateinit var transcationViewModel: TransactionViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var transactionListAdapter: TransactionListAdapter
    private lateinit var managePaymentViewModel: ManagePaymentViewModel


    override fun getLayoutId(): Int {
        return R.layout.fragment_base_transaction
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {

        fragmentTransactionBinding = mViewDataBinding as FragmentBaseTransactionBinding
        transcationViewModel = TransactionViewModel()
        transcationViewModel.navigator = this
        fragmentTransactionBinding.transctionmodel = transcationViewModel
        fragmentTransactionBinding.lifecycleOwner = this
        linearLayoutManager = LinearLayoutManager(activity)
        fragmentTransactionBinding.transactionListRv.layoutManager = linearLayoutManager
        transcationViewModel.showLoading = baseLiveDataLoading as MutableLiveData<Boolean>
        managePaymentViewModel = ViewModelProviders.of(activity!!).get(ManagePaymentViewModel::class.java)
        //callGetTrancation Api
        getApiResponse()
    }

    fun getApiResponse() {
        transcationViewModel.transcationLiveResponse.observe(this, Observer<WalletTransactionList> {
            transcationViewModel.showLoading.value=false
            if (it.responseData!=null && !(it.responseData.data!!.isNullOrEmpty())) {
                cardView.visibility = View.VISIBLE
                llEmptyView.visibility = View.GONE
                transactionListAdapter = TransactionListAdapter(activity!!, it.responseData.data,managePaymentViewModel.strCurrencyType.value.toString())
                fragmentTransactionBinding.transactionListRv.adapter = transactionListAdapter
            } else {
                cardView.visibility = View.GONE
                llEmptyView.visibility = View.VISIBLE
            }

        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser==true) {
            transcationViewModel.showLoading.value = true
            transcationViewModel.callTranscationApi()
        }
    }
}
