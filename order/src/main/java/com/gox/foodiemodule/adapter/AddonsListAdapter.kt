package com.gox.foodiemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.data.Constant
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.databinding.AddonsRowitemBinding


class AddonsListAdapter(val addonItems: List<ResturantDetailsModel.ResponseData.Product.Itemsaddon?>?)
    : RecyclerView.Adapter<AddonsListAdapter.MyViewHolder>() {


    lateinit var context: Context
    private var mOnMenuItemClickListner: MenuItemClickListner? = null

    fun setOnClickListener(onClickListener: MenuItemClickListner) {
        this.mOnMenuItemClickListner = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        this.context = parent.context
        val inflate = DataBindingUtil
                .inflate<AddonsRowitemBinding>(LayoutInflater.from(parent.context),
                        R.layout.addons_rowitem, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = addonItems!!.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind()
        holder.mAddonsRowitemBinding.addonsItemTv.text = addonItems!![position]!!.addon_name
        holder.mAddonsRowitemBinding.addonsItemPrice.text = Constant.currency + addonItems[position]!!.price.toString()

        holder.mAddonsRowitemBinding.addonsItemTv.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                if(mOnMenuItemClickListner!=null)
                mOnMenuItemClickListner!!.addedAddons(addonItems[position]!!.id!!)
            }else{
                if(mOnMenuItemClickListner!=null)
                mOnMenuItemClickListner!!.removedAddons(addonItems[position]!!.id!!)
            }

        }

    }


    inner class MyViewHolder(itemView: AddonsRowitemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val mAddonsRowitemBinding = itemView

        fun bind() {

        }


    }


}


