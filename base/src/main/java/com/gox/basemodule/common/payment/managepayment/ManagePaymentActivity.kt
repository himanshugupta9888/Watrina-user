package com.gox.basemodule.common.payment.managepayment

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.databinding.ActivityManagePaymentBinding
import com.gox.basemodule.common.payment.transaction.TransactionFragment
import com.gox.basemodule.common.payment.wallet.WalletFragment
import com.gox.basemodule.common.payment.adapter.PaymentAdapter
import com.gox.basemodule.common.payment.recive_money.ReciveEMoneyActivity
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.scanner.SimpleScannerActivity
import com.gox.basemodule.utils.PrefixCustomEditText
import com.gox.basemodule.utils.ViewUtils
import kotlinx.android.synthetic.main.toolbar_base_layout.view.*
import java.util.*
import com.maple.msdialog.ActionSheetDialog
import kotlinx.android.synthetic.main.dialog_send_amount.*


class ManagePaymentActivity : BaseActivity<ActivityManagePaymentBinding>(),
        ManagePaymentNavigator, ViewPager.OnPageChangeListener {

    private lateinit var activityManagePaymentBinding: ActivityManagePaymentBinding
    private lateinit var managePaymentViewModel: ManagePaymentViewModel
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var tbManagePayment: TabLayout
    private var invoiceAlertDialog: AlertDialog.Builder? = null
    private var mAlertDialog: AlertDialog? = null

    private var qrLink: String? = ""
    private var sendAmount: String? = ""

    val preference = PreferenceHelper(BaseApplication.baseApplication)
    private val userid = preference.getValue(PreferenceKey.USER_ID, "")

    private var edtSendAmount: com.gox.basemodule.utils.PrefixCustomEditText? = null
    private var walletamount: Int = 0
    private var tv_label1: TextView? = null
    private lateinit var appCompatActivity: AppCompatActivity
    private var context: Context? = null

    override fun getLayoutId() = R.layout.activity_manage_payment

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        activityManagePaymentBinding = mViewDataBinding as ActivityManagePaymentBinding
        managePaymentViewModel = ViewModelProviders.of(this).get(ManagePaymentViewModel::class.java)
        managePaymentViewModel.navigator = this
        tbManagePayment = findViewById(R.id.tb_payment)

        context = this
        appCompatActivity = context as AppCompatActivity

        activityManagePaymentBinding.toolbarLayout.title_toolbar.title = resources.getString(R.string.wallet)
        activityManagePaymentBinding.toolbarLayout.toolbar_back_img.setOnClickListener {
            finish()
        }

        activityManagePaymentBinding.toolbarLayout.iv_right.setVisibility(View.VISIBLE)
        activityManagePaymentBinding.toolbarLayout.iv_right.setOnClickListener {
            showScannerPopup()
        }

        val activityResultflag = intent.getStringExtra("activity_result_flag")
        managePaymentViewModel.setFlag(activityResultflag!!)
        val paymentFragmentList = Vector<Fragment>()

        val walletFragment = WalletFragment()
        val transactionFragment = TransactionFragment()

        paymentFragmentList.add(walletFragment)
        paymentFragmentList.add(transactionFragment)
        paymentAdapter = PaymentAdapter(supportFragmentManager, this@ManagePaymentActivity, paymentFragmentList)
        activityManagePaymentBinding.vbPayment.adapter = paymentAdapter
        tbManagePayment.setupWithViewPager(activityManagePaymentBinding.vbPayment)

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }

    override fun addCard() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadingObservable.value = false
    }

    fun showScannerPopup() {
        ActionSheetDialog(this)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setCancelText(resources.getString(R.string.cancel), R.color.red, 20, false)
                .addSheetItem(
                        resources.getString(R.string.send_amount), R.color.black
                ) { which -> showSendMoneyDialog() }
                .addSheetItem(
                        resources.getString(R.string.receive_amount), R.color.black
                ) { which ->
                    intent = Intent(context, ReciveEMoneyActivity::class.java)
                    intent.putExtra("qrUrl", qrLink)
                    startActivityForResult(intent, 144)
                }.show()
    }


    fun showSendMoneyDialog() {
        val invoiceDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_amount, null)
        invoiceAlertDialog = AlertDialog.Builder(this@ManagePaymentActivity).setView(invoiceDialogView)
        mAlertDialog = invoiceAlertDialog!!.create()
        mAlertDialog!!.show()
        edtSendAmount = mAlertDialog!!.findViewById<PrefixCustomEditText>(R.id.edtAmount)

        edtSendAmount!!.addTextChangedListener(EditListener())
        mAlertDialog!!.findViewById<Button>(R.id.bt_send)!!.setOnClickListener {
            managePaymentViewModel.strSendAmount.value = edtSendAmount!!.text.toString()
            if (validate()) {
                sendAmount = edtSendAmount!!.text.toString()

                var avlAmount: Double = Constant.walletAmount;
                var mAmount: Int = Integer.parseInt(sendAmount)
                if (mAmount > avlAmount) {
                    Toast.makeText(this@ManagePaymentActivity, "Enter Amount Below " + avlAmount, Toast.LENGTH_SHORT).show()
                } else {

                    val params = HashMap<String, String>()
                    params.put("amount", sendAmount!!)
                    params.put("id", userid?.toString()!!)

                    managePaymentViewModel.sendWalletAmount(params)

                    if (getPermissionUtil().hasPermission(appCompatActivity, Constant.RequestPermission.PERMISSION_CAMERA)) {
                        val scannerIntent = Intent(this@ManagePaymentActivity, SimpleScannerActivity::class.java)
                        startActivityForResult(scannerIntent, 225)
                    } else getPermissionUtil().requestPermissions(appCompatActivity, Constant.RequestPermission.PERMISSION_CAMERA, Constant.RequestCode.PERMISSION_CODE_CAMERA)

                }
            }

        }
    }

    private fun toast(s: String) {
        ViewUtils.showToast(context!!, s, false)

    }

    inner class EditListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            edtSendAmount?.let { setPrefix(it, s, Constant.currency) }

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    fun setPrefix(editText: PrefixCustomEditText, s: Editable?, strPref: String) =
            if (s.toString().isNotEmpty()) editText.setPrefix(strPref) else editText.setPrefix("")

    override fun showErrorMsg(error: String) {
        ViewUtils.showToast(this, error, false)
    }

    override fun validate(): Boolean {

        if (managePaymentViewModel.strSendAmount.value.isNullOrEmpty()) {
            ViewUtils.showToast(this, resources.getString(R.string.empty_amount), false)
            return false
        } else {
            return true
        }
    }


}