package com.gox.xubermodule.ui.activity.provierlistactivity

import android.content.DialogInterface
import android.content.Intent
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.databinding.ActivityProvidersBinding
import com.gox.xubermodule.ui.activity.selectlocation.SelectLocationActivity
import com.gox.xubermodule.ui.adapter.ProvidersAdapter
import com.gox.xubermodule.ui.fragment.providerslistfragment.ProvidersListFragment
import com.gox.xubermodule.ui.fragment.providersmapfragment.ProvidersMapFragment
import kotlinx.android.synthetic.main.toolbar_service_category.*
import java.util.*

class ProvidersActivity : BaseActivity<ActivityProvidersBinding>(), ProvidersNavigator
        , ViewPager.OnPageChangeListener {

    lateinit var activityProvidersBinding: ActivityProvidersBinding
    private lateinit var providersViewModel: ProvidersViewModel
    private lateinit var providersAdapter: ProvidersAdapter
    private lateinit var tbproviders: TabLayout

    override fun getLayoutId(): Int = R.layout.activity_providers
    override fun initView(mViewDataBinding: ViewDataBinding?) {
        activityProvidersBinding = mViewDataBinding as ActivityProvidersBinding
        providersViewModel = ProvidersViewModel()
        providersViewModel = ViewModelProviders.of(this).get(ProvidersViewModel::class.java)
        providersViewModel.navigator = this
        tbproviders = findViewById(R.id.tb_providerlist)
       // providersViewModel.getProviderList()
        val providerFragmentList = Vector<Fragment>()
        val providerListFragment = ProvidersListFragment()
        val providerMapFragment = ProvidersMapFragment()
        providerFragmentList.add(providerListFragment)
        providerFragmentList.add(providerMapFragment)
        providersAdapter = ProvidersAdapter(supportFragmentManager, this@ProvidersActivity
                , providerFragmentList)
        activityProvidersBinding.vbServiceprovider.adapter = providersAdapter
        tbproviders.setupWithViewPager(activityProvidersBinding.vbServiceprovider)
        ivBack.setOnClickListener { onBackPressed() }
        service_name.text = getString(R.string.service_providers)

        getObserValues()
    }

    fun getObserValues(){
        providersViewModel.loaderLiveData.observe(this, androidx.lifecycle.Observer {
            loadingObservable.value=it
        })
    }

    override fun onPageScrollStateChanged(state: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageSelected(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onBackPressed() {
        openNewActivity(this, SelectLocationActivity::class.java, true)
    }

    override fun logout(isTrue: Boolean) {
        ViewUtils.showAlert(this, R.string.gust_message,object : ViewCallBack.Alert{
            override fun onPositiveButtonClick(dialog: DialogInterface) {
                val intent = Intent(this@ProvidersActivity,Class.forName("com.gox.app.ui.signup.SignupActivity"))
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("EXIT", true)
                startActivity(intent)
                dialog.dismiss()
            }
            override fun onNegativeButtonClick(dialog: DialogInterface) {
            }

        })
    }

}
