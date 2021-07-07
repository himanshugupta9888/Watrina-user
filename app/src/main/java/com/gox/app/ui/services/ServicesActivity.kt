package com.gox.app.ui.services

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gox.app.R
import com.gox.app.databinding.ActivityHomeBinding
import com.gox.app.databinding.ActivityServicesBinding
import com.gox.app.databinding.FragmentHomeBinding
import com.gox.basemodule.base.BaseActivity
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.ui.activity.mainactivity.XuberMainActivity
import kotlinx.android.synthetic.main.custom_layout.view.*

class ServicesActivity : BaseActivity<ActivityServicesBinding>() {
    private lateinit var mViewDataBinding: ActivityServicesBinding

    override fun getLayoutId(): Int = R.layout.activity_services

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        this.mViewDataBinding = mViewDataBinding as ActivityServicesBinding
        this.mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.llElectrician.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 1
            XuberServiceClass.serviceName = "Electrician"
            startActivity(intent)
        }

        mViewDataBinding.llPlumber.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 2
            XuberServiceClass.serviceName = "Plumber"
            startActivity(intent)
        }
        mViewDataBinding.llMechanic.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 5
            XuberServiceClass.serviceName = "Mechanic"
            startActivity(intent)
        }
        mViewDataBinding.llMaid.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 17
            XuberServiceClass.serviceName = "Maids"
            startActivity(intent)
        }
        mViewDataBinding.llTravel.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 27
            XuberServiceClass.serviceName = "Travel Agent"
            startActivity(intent)
        }
        mViewDataBinding.llInsurance.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 29
            XuberServiceClass.serviceName = "Insurance Agent"
            startActivity(intent)
        }
        mViewDataBinding.llComputerTech.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 37
            XuberServiceClass.serviceName = "Computer Repairer"
            startActivity(intent)
        }
        mViewDataBinding.llMobileTech.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 42
            XuberServiceClass.serviceName = "Mobile Technician"
            startActivity(intent)
        }
        mViewDataBinding.llRoadAssistance.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 46
            XuberServiceClass.serviceName = "Road Assistance"
            startActivity(intent)
        }

        mViewDataBinding.llTranslator.setOnClickListener {
            val intent = Intent(applicationContext, XuberMainActivity::class.java)
            XuberServiceClass.serviceID = 49
            XuberServiceClass.serviceName = "Translator"
            startActivity(intent)
        }

        mViewDataBinding.llBeauty.setOnClickListener {
            showDialog()
        }
        mViewDataBinding.llLandCare.setOnClickListener {
            showDialog()
        }
        mViewDataBinding.llSecurity.setOnClickListener {
            showDialog()
        }
        mViewDataBinding.llPhotographer.setOnClickListener {
            showDialog()
        }
        mViewDataBinding.llLocksmith.setOnClickListener {
            showDialog()
        }
        mViewDataBinding.llCoach.setOnClickListener {
            showDialog()
        }


    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_layout)

//        val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
//        val clearBtn = dialog.findViewById(R.id.clearBtn) as ImageView
////        yesBtn.setOnClickListener {
////            dialog.dismiss()
////        }
//        clearBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_layout, null)
//        //AlertDialogBuilder
//        val mBuilder = AlertDialog.Builder(this)
//            .setView(mDialogView)
//        //show dialog
//        val  mAlertDialog = mBuilder.show()
//        //login button click of custom layout
//        mDialogView.clearBtn.setOnClickListener {
//            //dismiss dialog
//            mAlertDialog.dismiss()
//
//        }
    }
}