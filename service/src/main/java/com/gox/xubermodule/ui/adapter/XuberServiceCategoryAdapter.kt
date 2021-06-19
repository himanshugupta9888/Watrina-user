package com.gox.xubermodule.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.SubCategoryModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.XuberServiceCategoryTypeInflateBinding
import com.gox.xubermodule.ui.activity.xubersubserviceactivity.XuberSubServiceActivity

class XuberServiceCategoryAdapter(val activity: FragmentActivity?
                                  , val subCategoryData: List<SubCategoryModel.SubCategoryData?>)
    : RecyclerView.Adapter<XuberServiceCategoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = DataBindingUtil.inflate<XuberServiceCategoryTypeInflateBinding>(LayoutInflater.from(parent.context)
                , R.layout.xuber_service_category_type_inflate, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = subCategoryData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        if (position == subCategoryData.size - 1)
            holder.xuberServiceCategoryTypeInflateBinding.bottomView.visibility = View.INVISIBLE
        holder.xuberServiceCategoryTypeInflateBinding.tvXuberServiceName.text = subCategoryData[position]!!.service_subcategory_name
        holder.xuberServiceCategoryTypeInflateBinding.subcategoryLayout.setOnClickListener {
            val intent = Intent(activity, XuberSubServiceActivity::class.java)
            XuberServiceClass.mainServiceID = subCategoryData[position]!!.id as Int
            XuberServiceClass.mainServiceName = subCategoryData[position]!!.service_subcategory_name as String
            activity!!.startActivity(intent)
            activity!!.finish()
        }
    }

    inner class MyViewHolder(itemView: XuberServiceCategoryTypeInflateBinding)
        : RecyclerView.ViewHolder(itemView.root) {
        val xuberServiceCategoryTypeInflateBinding = itemView
        fun bind() {

        }
    }
}
