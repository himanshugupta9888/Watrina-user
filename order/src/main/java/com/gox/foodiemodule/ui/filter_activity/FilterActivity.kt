package com.gox.foodiemodule.ui.filter_activity

import android.content.Intent
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.data.Constant
import com.gox.foodiemodule.R
import com.gox.foodiemodule.adapter.CusineListAdapter
import com.gox.foodiemodule.adapter.MenuItemClickListner
import com.gox.foodiemodule.data.model.CusineListModel
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.databinding.FilterActivityBinding
import android.widget.CheckBox
import android.widget.Toast
import es.dmoral.toasty.Toasty


class FilterActivity : BaseActivity<FilterActivityBinding>() {

    lateinit var mViewDataBinding: FilterActivityBinding
    lateinit var mFilterViewModel: FilterViewModel
    var cusineList: ArrayList<Int> = ArrayList<Int>()
    var cusinesID: String = "0"
    var mfilterType: String = ""

    var storeID: String = ""
    var storeType: String = ""

    override fun getLayoutId(): Int = R.layout.filter_activity


    override fun initView(mViewDataBinding: ViewDataBinding?) {

        mFilterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        this.mViewDataBinding = mViewDataBinding as FilterActivityBinding
        storeID = intent.getStringExtra("store_id")
        storeType = intent.getStringExtra("store_type")

        mFilterViewModel.getCusineList(storeID)

        mFilterViewModel.cusineListData.observe(this, Observer {
            if (it.statusCode == "200" && !it.responseData!!.isEmpty()) {
                mViewDataBinding.cousineRv.visibility = View.VISIBLE
                mViewDataBinding.tvNoCategories.visibility = View.GONE
                setCusineListAdapter(it.responseData)
            } else {
                mViewDataBinding.cousineRv.visibility = View.GONE
                mViewDataBinding.tvNoCategories.visibility = View.VISIBLE
            }
        })


        if (!storeType.equals("FOOD", true)) {
            mViewDataBinding.sortLayout.visibility = View.GONE
        } else {
            mViewDataBinding.sortLayout.visibility = View.VISIBLE
        }

        mViewDataBinding.foodieApplyFilter.setOnClickListener {

            cusinesID = ""
            if (cusineList.size > 0) {
                for (i in 0 until cusineList.size) {
                    cusinesID += "," + cusineList[i]
                }
                cusineList.clear()

            }

//            if( mViewDataBinding.cousineRv.visibility == View.VISIBLE && cusinesID.equals("")) {
//               Toast.makeText(this@FilterActivity, "Please select any category",Toast.LENGTH_SHORT).show()
//            } else {
                val intent = Intent()
                intent.putExtra("selected_cusine_list", cusinesID)
                intent.putExtra("selected_filter", mfilterType)
                setResult(Constant.FILTERTYPE_CODE, intent)
                finish()
//            }
        }

        mViewDataBinding.resetTv.setOnClickListener {

            mViewDataBinding.typeRg.clearCheck()
            cusineList.clear()
            // mViewDataBinding.cusineListAdapter!!.notifyDataSetChanged()
            unCheckAll()

        }


        mViewDataBinding.ivClose.setOnClickListener {
            finish()
        }

        mViewDataBinding.typeRg.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->

            val checkedRadioButton = radioGroup.findViewById(radioGroup.checkedRadioButtonId)
                    as? RadioButton
            if (checkedRadioButton != null && checkedRadioButton.isChecked) {
                mfilterType = checkedRadioButton.text.toString()

            }
        }
    }

    private fun unCheckAll() {
        var cb: CheckBox
        for (i in 0 until mViewDataBinding.cousineRv.childCount) {
            cb = mViewDataBinding.cousineRv.getChildAt(i).findViewById(R.id.cusine_chckbox)
            cb.isChecked = false
        }
        mViewDataBinding.cusineListAdapter?.notifyDataSetChanged()
    }

    private fun setCusineListAdapter(responseData: List<CusineListModel.CusineResponseData?>?) {
        val cusineListAdapter = CusineListAdapter(responseData)
        mViewDataBinding.cusineListAdapter = cusineListAdapter
        cusineListAdapter.setOnClickListener(mOnMenuItemClickListner)
    }

    private val mOnMenuItemClickListner = object : MenuItemClickListner {

        override fun addFilterType(filterType: String) {
            mfilterType = filterType
        }

        override fun showAddonLayout(id: Int?, itemCount: Int, itemsaddon: ResturantDetailsModel.ResponseData.Product?, b: Boolean) {
        }

        override fun addToCart(id: Int?, itemCount: Int, cartId: Int?, repeat: Int, customize: Int) {
        }

        override fun removeCart(position: Int) {

        }

        override fun addedAddons(position: Int) {
            cusineList.add(position)
        }

        override fun removedAddons(position: Int) {
            cusineList.remove(position)
        }
    }


    interface ClearCusineList {

        fun clearCusineList()
    }
}
