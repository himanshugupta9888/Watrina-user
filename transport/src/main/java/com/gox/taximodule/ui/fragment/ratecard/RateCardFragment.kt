package com.gox.taximodule.ui.fragment.ratecard

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gox.basemodule.base.BaseBottomDialogFragment
import com.gox.taximodule.R
import com.gox.taximodule.databinding.RateCardFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gox.basemodule.data.Constant
import com.gox.basemodule.utils.ViewUtils
import com.gox.taximodule.ui.activity.main.TaxiMainViewModel

class RateCardFragment : BaseBottomDialogFragment<RateCardFragmentBinding>(), RateCardNavigator {

    private lateinit var viewModel: RateCardViewModel
    private lateinit var mTaxiviewModel: TaxiMainViewModel
    private lateinit var mRateCardFragmentBinding: RateCardFragmentBinding
    private var mServiceID: Int? = null

    companion object {
        fun newInstance() = RateCardFragment()
    }

    override fun getLayoutId(): Int = R.layout.rate_card_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun initView(mRootView: View?, mViewDataBinding: ViewDataBinding?) {
        mRateCardFragmentBinding = mViewDataBinding as RateCardFragmentBinding

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        mTaxiviewModel = ViewModelProviders.of(activity!!).get(TaxiMainViewModel::class.java)
        mTaxiviewModel.getRequestId().observe(this, Observer {
            if (it != null) {
                mServiceID = it
            }
        })
        //  Set callback
        if (behavior != null && behavior is BottomSheetBehavior) {
            Handler().postDelayed({
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }, 200)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RateCardViewModel::class.java)

        viewModel.navigator = this
        mRateCardFragmentBinding.viewModel = viewModel
        mRateCardFragmentBinding.executePendingBindings()
        mTaxiviewModel.getRateCard().observe(this, Observer {
            mRateCardFragmentBinding.tvFareType.text = it.priceDetailModel?.calculator.toString()
            if (it.vehicleImage != null) {
                ViewUtils.setImageViewGlide(context!!, mRateCardFragmentBinding.fabTop.findViewById(R.id.fabTaxiImage), it.vehicleImage)
            }
            if (it.capcity != null) {
                mRateCardFragmentBinding.tvCapacity.text = it.capcity.toString()
            } else {
                mRateCardFragmentBinding.tvCapacity.text = "1"
            }

            mRateCardFragmentBinding.tvFareType.text = it.priceDetailModel?.calculator.toString()

            if (it.priceDetailModel?.calculator.toString().equals("DISTANCEMIN")) {
                mRateCardFragmentBinding.tvBaseFare.text = Constant.currency + " " + it.priceDetailModel?.fixed
                mRateCardFragmentBinding.tvFareKm.text = Constant.currency + " " + it.priceDetailModel?.price+ "/km"
                mRateCardFragmentBinding.tvLabelTimeFareKm.visibility=View.VISIBLE
                mRateCardFragmentBinding.tvFareKm.visibility=View.VISIBLE
                mRateCardFragmentBinding.tvLabelTimeFareKm.text = "Time Fare"
                mRateCardFragmentBinding.tvTimeFare.text = Constant.currency + it.priceDetailModel?.minute + "/min"

            } else if (it.priceDetailModel?.calculator.toString().equals("DISTANCE")) {
                mRateCardFragmentBinding.tvBaseFare.text = Constant.currency + " " + it.priceDetailModel?.fixed
                mRateCardFragmentBinding.tvFareKm.text = it.priceDetailModel?.distance + "/km"
                mRateCardFragmentBinding.tvLabelTimeFareKm.visibility=View.GONE
                mRateCardFragmentBinding.tvTimeFare.visibility=View.GONE

            } else if (it.priceDetailModel?.calculator.toString().equals("DISTANCEHOUR")) {
                mRateCardFragmentBinding.tvBaseFare.text = Constant.currency + " " + it.priceDetailModel?.fixed
                mRateCardFragmentBinding.tvFareKm.text = Constant.currency +it.priceDetailModel?.price+"/km"

                mRateCardFragmentBinding.tvLabelTimeFareKm.text = "Time Fare"
                mRateCardFragmentBinding.tvTimeFare.text = Constant.currency + it.priceDetailModel?.hour + "/hr"

            } else if (it.priceDetailModel?.calculator.toString().equals("HOUR")) {
                mRateCardFragmentBinding.tvBaseFare.text = Constant.currency + " " + it.priceDetailModel?.fixed
                mRateCardFragmentBinding.tvFareKm.text = Constant.currency +it.priceDetailModel?.hour+"/Hr"
                mRateCardFragmentBinding.tvLabelFareKm.text = "Time Fare"
                mRateCardFragmentBinding.tvLabelTimeFareKm.visibility=View.GONE
                mRateCardFragmentBinding.tvTimeFare.visibility=View.GONE
            } else if (it.priceDetailModel?.calculator.toString().equals("MIN")) {
                mRateCardFragmentBinding.tvBaseFare.text = Constant.currency + " " + it.priceDetailModel?.fixed
                mRateCardFragmentBinding.tvFareKm.text = Constant.currency + it.priceDetailModel?.minute + "/min"
                mRateCardFragmentBinding.tvLabelFareKm.text = "Time Fare"
                mRateCardFragmentBinding.tvLabelTimeFareKm.visibility=View.GONE
                mRateCardFragmentBinding.tvTimeFare.visibility=View.GONE
            }


        })

        mRateCardFragmentBinding.btnSchedule.setOnClickListener {
            fragmentManager!!.popBackStack()
            mTaxiviewModel.showServiceList()
        }
    }

}
