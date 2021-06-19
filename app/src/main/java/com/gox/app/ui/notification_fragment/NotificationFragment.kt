package com.gox.app.ui.notification_fragment

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.gox.basemodule.base.BaseFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.app.R
import com.gox.app.adapter.NotificationAdapter
import com.gox.app.data.repositary.remote.model.NotificationNewResponse
import com.gox.app.databinding.FragmentNotificationBinding

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationFragmentNavigator {


    lateinit var mViewDataBinding: FragmentNotificationBinding

    override fun getLayoutId(): Int = R.layout.fragment_notification

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as FragmentNotificationBinding
        val notificationViewModel = NotificationFragmentViewModel()
        notificationViewModel.getNotificationList()
        showLoading()
        notificationViewModel.navigator = this
        this.mViewDataBinding.notificationviewmodel = notificationViewModel


        notificationViewModel.notificationResponse.observe(this@NotificationFragment,
                Observer<NotificationNewResponse> {
                    notificationViewModel.loadingProgress.value = false
                    hideLoading()
                    if (it.responseData.notification!= null && it.responseData.notification.isNotEmpty()) {
                        setNotificationAdapter(it.responseData)
                    } else {
                        this.mViewDataBinding.emptyViewLayout.visibility = View.VISIBLE
                        this.mViewDataBinding.notificationRv.visibility = View.GONE
                    }
                })

        notificationViewModel.getErrorObservable().observe(this@NotificationFragment,
                Observer<String> { message ->
                    notificationViewModel.loadingProgress.value = false
                    hideLoading()
                    ViewUtils.showToast(activity!!, message, false)
                })


    }

    private fun setNotificationAdapter(notificationResponseData: NotificationNewResponse.ResponseData) {
        this.mViewDataBinding.notificationAdapter = NotificationAdapter(activity, notificationResponseData)

    }

    override fun goToDetailPage() {
    }

}
