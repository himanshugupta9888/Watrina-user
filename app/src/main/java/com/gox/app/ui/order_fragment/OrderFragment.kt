package com.gox.app.ui.order_fragment

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.gox.app.R
import com.gox.app.adapter.FilterServiceListAdapter
import com.gox.app.data.repositary.remote.model.BaseApiResponseData
import com.gox.app.data.repositary.remote.model.Service
import com.gox.app.databinding.FilterDialogBinding
import com.gox.app.databinding.FragmentOrderBinding
import com.gox.app.ui.currentorder_fragment.CurrentOrderFragment
import com.gox.app.ui.dashboard.UserDashboardViewModel
import com.gox.app.ui.pastorder_fragment.PastOrderFragment
import com.gox.app.ui.upcoming_fragment.UpcomingFragment
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey

class OrderFragment : BaseFragment<FragmentOrderBinding>(), OrderFragmentNavigator {

    lateinit var mViewDataBinding: FragmentOrderBinding
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    val preference = PreferenceHelper(BaseApplication.baseApplication)
    lateinit var filterServiceListName: List<Service>
    lateinit var orderFragmentViewModel: OrderFragmentViewModel
    lateinit var dashboardViewModel: UserDashboardViewModel

    override fun getLayoutId(): Int = R.layout.fragment_order

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        orderFragmentViewModel = OrderFragmentViewModel()
        dashboardViewModel = ViewModelProviders.of(activity!!).get(UserDashboardViewModel::class.java)
        this.mViewDataBinding = mViewDataBinding as FragmentOrderBinding
        orderFragmentViewModel.navigator = this
        this.mViewDataBinding.orderfragmentviewmodel = orderFragmentViewModel

        val baseApiResponseString: String = BaseApplication.getCustomPreference!!.getString(PreferenceKey.BASE_CONFIG_RESPONSE, "")!!
        val baseApiResponsedata: BaseApiResponseData = Gson().fromJson<BaseApiResponseData>(baseApiResponseString
                , BaseApiResponseData::class.java)
        filterServiceListName = baseApiResponsedata.services
        dashboardViewModel.selectedFilterService.value = filterServiceListName[0].admin_service
        goToPastOrder()
    }

    override fun goToCurrentOrder() {
        mViewDataBinding.pastorderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.currentOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_selectedorder)
        }
        mViewDataBinding.upcomingOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it, R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.pastorderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))
        mViewDataBinding.currentOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorWhite))
        mViewDataBinding.upcomingOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container_order, CurrentOrderFragment())?.commit()

    }

    override fun goToPastOrder() {
        mViewDataBinding.pastorderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_selectedorder)
        }
        mViewDataBinding.currentOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.upcomingOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.pastorderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorWhite))
        mViewDataBinding.currentOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))
        mViewDataBinding.upcomingOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container_order, PastOrderFragment())?.commit()

    }

    override fun goToUpcomingOrder() {
        mViewDataBinding.pastorderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.currentOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_unselectedorder)
        }
        mViewDataBinding.upcomingOrderBtn.background = context?.let {
            ContextCompat.getDrawable(it
                    , R.drawable.custom_roundcorner_selectedorder)
        }

        mViewDataBinding.pastorderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))
        mViewDataBinding.currentOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorBlack))
        mViewDataBinding.upcomingOrderBtn.setTextColor(ContextCompat.getColor(activity!!, R.color.colorWhite))
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container_order, UpcomingFragment())?.commit()

    }

    override fun openFilterlayout() {
        val inflate = DataBindingUtil.inflate<FilterDialogBinding>(LayoutInflater.from(context)
                , R.layout.filter_dialog, null, false)
        inflate.filterServiceListAdapter = FilterServiceListAdapter(dashboardViewModel, filterServiceListName)
        val dialog = BottomSheetDialog(activity!!)
        dialog.setContentView(inflate.root)
        dialog.show()
        inflate.applyFilter.setOnClickListener {
            mViewDataBinding.serviceNameToolbarTv.text = dashboardViewModel.selectedFilterService.value
            goToPastOrder()
            dialog.dismiss()
        }
    }
}
