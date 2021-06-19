package com.gox.app.ui.dashboard

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.gox.basemodule.base.BaseActivity
import com.gox.app.R
import com.gox.app.databinding.ActivityHomeBinding
import com.gox.app.ui.home_fragment.HomeFragment
import com.gox.app.ui.myaccount_fragment.MyAccountFragment
import com.gox.app.ui.notification_fragment.NotificationFragment
import com.gox.app.ui.order_fragment.OrderFragment


class UserDashboardActivity : BaseActivity<ActivityHomeBinding>(), UserDashboardViewNavigator {

    lateinit var mViewDataBinding: ActivityHomeBinding
    override fun getLayoutId(): Int = R.layout.activity_home

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        var userDashboardViewModel = UserDashboardViewModel()
        this.mViewDataBinding = mViewDataBinding as ActivityHomeBinding
        userDashboardViewModel.navigator = this
        supportFragmentManager.beginTransaction().add(R.id.main_container, HomeFragment()).commit()
        userDashboardViewModel = ViewModelProviders.of(this).get(UserDashboardViewModel::class.java)
        mViewDataBinding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.notification_fragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, NotificationFragment()).commit()
                    true
                }
                R.id.home_fragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
                    true
                }
                R.id.myaccount_fragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, MyAccountFragment()).commit()
                    true
                }
                R.id.order_fragment -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, OrderFragment()).commit()
                    true
                }
                else -> false
            }
        }

        removeShifting()

    }

    @SuppressLint("RestrictedApi")
    private fun removeShifting() {
        val menuView = mViewDataBinding.bottomNavigation.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until menuView.childCount) {
            val item: BottomNavigationItemView = menuView.getChildAt(i) as BottomNavigationItemView

            item.setShifting(false)
            item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED)

            // set once again checked value, so view will be updated

            item.setChecked(item.itemData.isChecked)
        }
    }


    override fun gotoHomeFragment() {
//  supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
    }

    override fun gotOrderFragment() {
//        supportFragmentManager.beginTransaction().replace(R.id.main_container, OrderFragment()).commit()

    }

    override fun goToAccountFragment() {
//        supportFragmentManager.beginTransaction().replace(R.id.main_container, MyAccountFragment()).commit()

    }

    override fun goToNotificationFragment() {
//        supportFragmentManager.beginTransaction().replace(R.id.main_container, NotificationFragment()).commit()

    }


}