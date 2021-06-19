package com.gox.xubermodule.ui.fragment.scheduleride

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.XuberScheduleFragmentBinding
import com.gox.xubermodule.ui.activity.selectlocation.SelectLocationActivity
import java.util.*

class ScheduleFragment : BaseBottomDialogFragment<XuberScheduleFragmentBinding>(), ScheduleNavigator {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var mScheduleFragmentBinding: XuberScheduleFragmentBinding
    private var mSceduleDate: String? = null
    private var mSceduletime: String? = null

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    override fun getLayoutId(): Int = R.layout.xuber_schedule_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mScheduleFragmentBinding = mViewDataBinding as XuberScheduleFragmentBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ScheduleViewModel()
        viewModel.navigator = this
        mScheduleFragmentBinding.viewModel = viewModel
    }

    @SuppressLint("SetTextI18n")
    override fun pickDate() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val now = System.currentTimeMillis() - 1000
        val maxDate = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3)
        val datePickerDialog = DatePickerDialog(activity!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    view.minDate = System.currentTimeMillis() - 1000
                    view.maxDate = maxDate - 1000
                    mSceduleDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    mScheduleFragmentBinding.selectedDate.text = mSceduleDate


                }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = now
        datePickerDialog.datePicker.maxDate = maxDate
        datePickerDialog.show()
    }

    override fun pickTime() {
        val c = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(activity!!,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    mSceduletime = "$hourOfDay:$minute"
                    mScheduleFragmentBinding.selectedTime.text = mSceduletime
                }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    override fun scheduleRequest() {
        when (mSceduleDate) {
            null -> {
                ViewUtils.showToast(activity!!, getString(R.string.please_select_date), false)
            }
            else -> {
                when (mSceduletime) {
                    null -> {
                        ViewUtils.showToast(activity!!, getString(R.string.please_select_time), false)
                    }
                    else -> {
                        XuberServiceClass.date = mSceduleDate as String
                        XuberServiceClass.time = mSceduletime as String
                        val intent = Intent(activity!!, SelectLocationActivity::class.java)
                        startActivity(intent)
                        dismiss()
                    }
                }
            }
        }
    }
}
