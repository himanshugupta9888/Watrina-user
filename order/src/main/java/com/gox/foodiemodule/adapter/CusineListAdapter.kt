package com.gox.foodiemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.CusineListModel
import com.gox.foodiemodule.databinding.FilterCousineRowitemBinding
import com.gox.foodiemodule.ui.filter_activity.FilterActivity


class CusineListAdapter(val cusineList: List<CusineListModel.CusineResponseData?>?)
    : RecyclerView.Adapter<CusineListAdapter.MyViewHolder>(),FilterActivity.ClearCusineList {

    override fun clearCusineList() {

    }


    lateinit var context: Context
    private var mOnMenuItemClickListner: MenuItemClickListner? = null


    fun setOnClickListener(onClickListener: MenuItemClickListner) {
        this.mOnMenuItemClickListner = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        this.context = parent.context
        val inflate = DataBindingUtil
                .inflate<FilterCousineRowitemBinding>(LayoutInflater.from(parent.context),
                        R.layout.filter_cousine_rowitem, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = cusineList!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind()
        holder.mAddonsRowitemBinding.cusineChckbox.text = cusineList?.get(position)?.name.toString()

        holder.mAddonsRowitemBinding.cusineChckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (mOnMenuItemClickListner != null)
                    mOnMenuItemClickListner!!.addedAddons(cusineList!!.get(position)!!.id!!)
            } else {
                if (mOnMenuItemClickListner != null)
                    mOnMenuItemClickListner!!.removedAddons(cusineList!!.get(position)!!.id!!)
            }

        }
    }


    inner class MyViewHolder(itemView: FilterCousineRowitemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val mAddonsRowitemBinding = itemView

        fun bind() {

        }


    }


}


