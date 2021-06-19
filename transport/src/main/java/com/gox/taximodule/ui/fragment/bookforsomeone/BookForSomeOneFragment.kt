package com.gox.taximodule.ui.fragment.bookforsomeone


import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.ReqSomeOneModel
import com.gox.taximodule.databinding.FragmentBookForSomeoneBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel

class BookForSomeOneFragment : BaseDialogFragment<FragmentBookForSomeoneBinding>(), BookForSomeOneNavigator {

    private lateinit var mBookForSomeoneBinding: FragmentBookForSomeoneBinding
    private lateinit var mViewModel: BookForSomeOneViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel


    companion object {
        fun newInstance() = BookForSomeOneFragment()
        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_book_for_someone

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mBookForSomeoneBinding = mViewDataBinding as FragmentBookForSomeoneBinding
    }

    /*override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        Log.d("Outside Touch", "Outside Touch")
        mTaxiMainViewModel.someOnemodel.value = null
    }*/


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(BookForSomeOneViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        mViewModel.navigator = this
        mBookForSomeoneBinding.viewModel = mViewModel
        mBookForSomeoneBinding.executePendingBindings()
        mTaxiMainViewModel.getSomeOneData().observe(this, Observer {
            if (it != null) {
                mBookForSomeoneBinding.etName.setText(it.name.toString())
                mBookForSomeoneBinding.etPhoneNumber.setText(it.phoneNumber.toString())
                mBookForSomeoneBinding.etemailaddress.setText(it.email.toString())
            }
        })

        if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_SMS, 0) == 1) {
            mBookForSomeoneBinding.etName.visibility = View.VISIBLE
        } else {
            mBookForSomeoneBinding.etName.visibility = View.GONE
        }

        if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_EMAIL, 0) == 1) {
            mBookForSomeoneBinding.etemailaddress.visibility = View.VISIBLE
        } else {
            mBookForSomeoneBinding.etemailaddress.visibility = View.GONE
        }
    }

    override fun closePopup() {
        val mReqSomeOneModel = ReqSomeOneModel()
        if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_SMS, 0) == 1 && mBookForSomeoneBinding.etPhoneNumber.text.toString() == "" || mBookForSomeoneBinding.etPhoneNumber.text.toString().length < 5) {
            ViewUtils.showNormalToast(activity!!, "Enter Valid Mobile number")
            return
        }
        if (mBookForSomeoneBinding.etName.text.toString() == "") {
            ViewUtils.showNormalToast(activity!!, "Enter Valid Name")
            return

        }
        if (BaseApplication.getCustomPreference!!.getInt(PreferenceKey.SEND_EMAIL, 0) == 1 && !isValidEmail(mBookForSomeoneBinding.etemailaddress.text.toString()) && mBookForSomeoneBinding.etemailaddress.text.toString() != "") {
            ViewUtils.showNormalToast(activity!!, "Enter Valid Email ID")
            return
        }
        mReqSomeOneModel.phoneNumber = mBookForSomeoneBinding.etPhoneNumber.text.toString()
        mReqSomeOneModel.name = mBookForSomeoneBinding.etName.text.toString()
        mReqSomeOneModel.email = mBookForSomeoneBinding.etemailaddress.text.toString()
        mTaxiMainViewModel.setSomeoneData(mReqSomeOneModel)
        dismiss()

    }


}
