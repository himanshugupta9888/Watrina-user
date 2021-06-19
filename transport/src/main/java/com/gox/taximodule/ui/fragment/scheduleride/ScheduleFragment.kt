package com.gox.taximodule.ui.fragment.scheduleride

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.R
import com.gox.taximodule.data.dto.request.ReqScheduleRide
import com.gox.taximodule.databinding.ScheduleFragmentBinding
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel
import java.util.*


class ScheduleFragment : BaseBottomDialogFragment<ScheduleFragmentBinding>(), ScheduleNavigator {


    private lateinit var viewModel: ScheduleViewModel
    private lateinit var mTaxiMainViewModel: TaxiMainViewModel
    private lateinit var mScheduleFragmentBinding: ScheduleFragmentBinding
    private var mSceduleDate: String? = null
    private var mSceduletime: String? = null

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    override fun getLayoutId(): Int = R.layout.schedule_fragment

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mScheduleFragmentBinding = mViewDataBinding as ScheduleFragmentBinding
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        mTaxiMainViewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        viewModel.navigator = this
        mScheduleFragmentBinding.viewModel = viewModel

        mTaxiMainViewModel.getScheduleDateTimeData().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                mSceduleDate = it.scheduleDate
                mSceduletime = it.scheduleTime
                mScheduleFragmentBinding.selectedDate.text = it.scheduleDate
                mScheduleFragmentBinding.selectedTime.text = it.scheduleTime
            }
        })
    }


    @SuppressLint("SetTextI18n")
    override fun pickDate() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val now = System.currentTimeMillis() - 1000
        val maxDate = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3)
        val datePickerDialog = DatePickerDialog(activity!!,R.style.TransportCalenderThemeDialog,
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
        val timePickerDialog = TimePickerDialog(activity!!,R.style.TransportCalenderThemeDialog,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    mSceduletime = "$hourOfDay:$minute"
                    mScheduleFragmentBinding.selectedTime.text = mSceduletime
                }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    override fun scheduleRequest() {
        if (!mSceduleDate.isNullOrEmpty() && !mSceduletime.isNullOrEmpty()) {
            val mScheduleRide = ReqScheduleRide()
            mScheduleRide.scheduleDate = mSceduleDate
            mScheduleRide.scheduleTime = mSceduletime
            mTaxiMainViewModel.setScheduleDateTime(mScheduleRide)
            dismiss()
        }else{
            ViewUtils.showToast(activity!!,getString(R.string.date_and_time_validation),false)
        }
    }
}
