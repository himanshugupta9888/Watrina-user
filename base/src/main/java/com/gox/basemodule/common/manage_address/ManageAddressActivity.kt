package com.gox.basemodule.common.manage_address

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.R
import com.gox.basemodule.adapter.ManageAddressAdapter
import com.gox.basemodule.common.add_address.AddAddressActivity
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.databinding.ActivityManageaddressBinding
import com.gox.basemodule.extensions.observeLiveData
import com.gox.basemodule.model.AddressModel
import com.gox.basemodule.utils.ViewCallBack
import com.gox.basemodule.utils.ViewUtils
import com.gox.basemodule.common.edit_address.EditAddressActivity
import kotlinx.android.synthetic.main.toolbar_base_layout.view.*
import java.io.Serializable

class ManageAddressActivity : BaseActivity<ActivityManageaddressBinding>(), ManageAddressNavigator {

    lateinit var mViewDataBinding: ActivityManageaddressBinding
    private lateinit var mManageAddressViewModel: ManageAddressViewModel
    private var mAddressList: ArrayList<AddressModel>? = null
    private var mAddressListAdapter: ManageAddressAdapter? = null
    var activityResultflag: String? = null
    override fun getLayoutId(): Int = R.layout.activity_manageaddress

    override fun initView(mViewDataBinding: ViewDataBinding?) {

        this.mViewDataBinding = mViewDataBinding as ActivityManageaddressBinding
        mViewDataBinding.toolbarLayout.title_toolbar.setTitle(R.string.manage_address)
        mViewDataBinding.toolbarLayout.toolbar_back_img.setOnClickListener {
            finish()
        }
        mViewDataBinding.btnDone.setOnClickListener {
            openNewActivity(this@ManageAddressActivity, AddAddressActivity::class.java, false)
        }
        mAddressList = ArrayList()
        activityResultflag = intent.getStringExtra("changeAddressFlag")
        mManageAddressViewModel = ViewModelProviders.of(this).get(ManageAddressViewModel::class.java)

        observeLiveData(mManageAddressViewModel.loading) {
            baseLiveDataLoading.value = it
        }

        mManageAddressViewModel.getAddressListResponse().observe(this, Observer {
            if (it != null) {
                mAddressList!!.clear()
                if (it.statusCode == "200" && it.addressList!!.isNotEmpty()) {
                    mViewDataBinding.llAddress.visibility = View.VISIBLE
                    mViewDataBinding.llEmptyView.visibility = View.GONE
                    mAddressList!!.addAll(it.addressList!!)
                    mAddressListAdapter = ManageAddressAdapter(this, mAddressList!!)
                    mViewDataBinding.manageAddress = mAddressListAdapter
                    mAddressListAdapter!!.setOnClickListener(mOnAdapterClickListener)
                    mViewDataBinding.manageAddress!!.notifyDataSetChanged()
                } else {
                    mViewDataBinding.llAddress.visibility = View.GONE
                    mViewDataBinding.llEmptyView.visibility = View.VISIBLE
                }
            }
        })

        mManageAddressViewModel.deleteAddressResponse.observe(this, Observer {
            if (it != null) {
                if (it.statusCode == "200") {
                    ViewUtils.showToast(this, it.message, true)
                    mManageAddressViewModel.getAddressList()
                }
            }
        })

    }

    override fun goToMainActivity() {

    }

    override fun onResume() {
        super.onResume()
        mManageAddressViewModel.getAddressList()
    }

    private val mOnAdapterClickListener = object : OnManageAddressClickListener {
        override fun onClick(addressModel: AddressModel) {
            if (activityResultflag.equals("1")) {
                val intent = Intent()
                intent.putExtra("address", addressModel as Serializable)
                setResult(Constant.CHANGE_ADDRESS_TYPE_REQUEST_CODE, intent)
                finish()
            }

        }

        override fun onDelete(position: Int) {
            val maAddressModel = mAddressList!![position]
            ViewUtils.showAlert(this@ManageAddressActivity, R.string.address_delete, object : ViewCallBack.Alert {
                override fun onPositiveButtonClick(dialog: DialogInterface) {
                    mAddressList!!.removeAt(position)
                    mAddressListAdapter?.notifyDataSetChanged()
                    mManageAddressViewModel.deleteAddress(maAddressModel.id.toString())

                    dialog.dismiss()
                }

                override fun onNegativeButtonClick(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            })
        }

        override fun onEdit(position: Int) {
            val maAddressModel = mAddressList!![position]
            val intent = Intent(this@ManageAddressActivity, EditAddressActivity::class.java)
            intent.putExtra("address", maAddressModel as Serializable)
            startActivity(intent)
        }


    }


}


