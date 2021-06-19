package com.gox.foodiemodule.fragment.coupon.add_note


import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseDialogFragment
import com.gox.basemodule.utils.ViewUtils

import com.gox.foodiemodule.R
import com.gox.foodiemodule.databinding.FragmentAddNoteBinding
import com.gox.foodiemodule.ui.viewCartActivity.ViewCartViewModel

class AddNoteFragment : BaseDialogFragment<FragmentAddNoteBinding>() {
    private lateinit var mAddNoteFragmentBinding: FragmentAddNoteBinding
    lateinit var viewCartViewModel: ViewCartViewModel

    companion object {
        fun newInstance() = AddNoteFragment()
    }

    override fun getLayoutId(): Int = R.layout.fragment_add_note
    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mAddNoteFragmentBinding = mViewDataBinding as FragmentAddNoteBinding
        viewCartViewModel = ViewModelProviders.of(activity!!).get(ViewCartViewModel::class.java)
        mAddNoteFragmentBinding.foodieSubmit.setOnClickListener {

            if (mAddNoteFragmentBinding.etAddNote.equals("")) {
                ViewUtils.showNormalToast(context!!, "Enter Add Description")
            } else {
                viewCartViewModel.setAddNote(mAddNoteFragmentBinding.etAddNote.text.toString())
            }
        }

    }


}
