package com.gox.basemodule.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.data.NetworkError
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.ui.CustomLoaderDialog
import com.gox.basemodule.utils.LocaleUtils
import com.gox.basemodule.utils.NetworkUtils
import com.gox.basemodule.utils.PermissionUtils
import com.gox.basemodule.utils.ViewUtils
import java.util.*

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    protected var baseLiveDataLoading = MutableLiveData<Boolean>()
    private var mViewDataBinding: T? = null
    private var mCustomLoader: CustomLoaderDialog? = null
    private var mPermissionUtils: PermissionUtils? = null

    // private lateinit var mNoInternetDialog: Dialog

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    val loadingObservable: MutableLiveData<*> get() = baseLiveDataLoading

    val isNetworkConnected: Boolean get() = NetworkUtils.isNetworkConnected(applicationContext)

    protected abstract fun initView(mViewDataBinding: ViewDataBinding?)

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initView(mViewDataBinding)

        mCustomLoader = CustomLoaderDialog(this, true)

//        try {
//            mNoInternetDialog = Dialog(this)
//            mNoInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            mNoInternetDialog.setCancelable(false)
//            mNoInternetDialog.setContentView(R.layout.dialog_no_internet)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        observeLiveData(baseLiveDataLoading) {
            if (it!!) showLoading() else hideLoading()
        }

        observeLiveData(BaseApplication.getInternetMonitorLiveData) { isInternetAvailable ->
            // if (isInternetAvailable) mNoInternetDialog.dismiss() else mNoInternetDialog.show()
            if (!isInternetAvailable) ViewUtils.showToast(
                    this,
                    NetworkError.UNKNOWN_HOST_EXCEPTION,
                    false
            )
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.setLocale(newBase))
    }

    fun showLoading() {
        if (mCustomLoader != null) {
            Objects.requireNonNull(mCustomLoader!!.window)?.setBackgroundDrawableResource(android.R.color.transparent)
            mCustomLoader!!.show()
        }
    }

    fun hideLoading() {
        if (mCustomLoader != null) mCustomLoader!!.cancel()
    }

    fun showKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openNewActivity(activity: AppCompatActivity, cls: Class<*>, finishCurrent: Boolean) {
        val intent = Intent(activity, cls)
        startActivity(intent)
        if (finishCurrent) activity.finish()
    }

    protected fun replaceFragment(@IdRes id: Int, fragmentName: Fragment, fragmentTag: String, addToBackStack: Boolean) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(id, fragmentName, fragmentTag)
        if (addToBackStack)
            transaction.addToBackStack(fragmentTag)
        transaction.commit()
    }

    fun getPermissionUtil(): PermissionUtils {
        if (mPermissionUtils == null) mPermissionUtils = PermissionUtils()
        return mPermissionUtils!!
    }

}