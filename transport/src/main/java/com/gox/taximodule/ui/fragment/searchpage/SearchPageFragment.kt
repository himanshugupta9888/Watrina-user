package com.gox.taximodule.ui.fragment.searchpage

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.request.ReqEstimateModel
import com.gox.taximodule.databinding.SearchPageFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainActivity
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import com.gox.taximodule.ui.communication.CommunicationListener

class SearchPageFragment : BaseFragment<SearchPageFragmentBinding>(), SearchPageNavigator {

    private lateinit var mSearchPageViewModel: SearchPageViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private lateinit var mSearchPageFragmentBinding: SearchPageFragmentBinding
    private lateinit var mCommunicationListener: CommunicationListener
    private var mRequestId: Int? = null

    companion object {
        fun newInstance() = SearchPageFragment()
    }

    override fun getLayoutId(): Int = R.layout.search_page_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mSearchPageFragmentBinding = mViewDataBinding as SearchPageFragmentBinding
        mSearchPageViewModel = ViewModelProviders.of(this).get(SearchPageViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)

        mTaxiMainViewModel.getCheckRequest()
        mTaxiMainViewModel.getCheckRequestResponse().observe(this, Observer {
            if (it != null) {
                if (mTaxiMainViewModel.currentStatus.value.equals("STARTED")) {
                    fragmentManager!!.popBackStack()
                }
            }
        })
        mTaxiMainViewModel.getRequestId().observe(this, Observer {
            ViewUtils.showNormalToast(activity!!, it.toString())
            mRequestId = it
        })
        mSearchPageViewModel.navigator = this
        mSearchPageFragmentBinding.viewModel = mSearchPageViewModel
        mTaxiMainViewModel.getCancelResponse().observe(this, Observer {
            if (it.statusCode == "200") {
                ViewUtils.showToast(activity!!, it.message, true)
                activity!!.finish()
            }
        })
    }

    /*override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TaxiMainActivity) {
            mCommunicationListener = context
        }
    }*/

    override fun moveToFlow() {
        /* clearFragment()
         TaxiRideRequest.setStatus("STATUS_FLOW")
         mCommunicationListener.onMessage("Update")*/
    }

    override fun cancelRequest() {
        /* clearFragment()
         TaxiRideRequest.setStatus("EMPTY")
         mCommunicationListener.onMessage("EMPTY")*/
        ViewUtils.showAlert(activity!!, R.string.cancel_request_confirm_msg, object : ViewCallBack.Alert {
            override fun onPositiveButtonClick(dialog: DialogInterface) {

                val reqEstimateModel = ReqEstimateModel()
                reqEstimateModel.id = mRequestId
                mTaxiMainViewModel.cancelRideRequest(reqEstimateModel)

            }

            override fun onNegativeButtonClick(dialog: DialogInterface) {
                dialog.dismiss()
            }
        })

    }
}
