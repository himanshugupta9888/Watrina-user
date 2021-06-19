package com.gox.foodiemodule.adapter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.basemodule.data.Constant
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ResturantDetailsModel
import com.gox.foodiemodule.databinding.RestaurantdetailViewcartmenuListitemBinding

class ViewCartMenuItemListAdapter(activity: FragmentActivity?,
                                  val products: List<ResturantDetailsModel.ResponseData.Product?>,
                                  val type: String) :
        RecyclerView.Adapter<ViewCartMenuItemListAdapter.MyViewHolder>() {


    val activity = activity

    private var mOnMenuItemClickListner: MenuItemClickListner? = null

    fun setOnClickListener(onClickListener: MenuItemClickListner) {
        this.mOnMenuItemClickListner = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<RestaurantdetailViewcartmenuListitemBinding>(
                LayoutInflater.from(parent.context)
                , R.layout.restaurantdetail_viewcartmenu_listitem,
                parent,
                false
        )
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()

        holder.mMenuListitemBinding.menuItemName.text = products[position]!!.item_name.toString()
        holder.mMenuListitemBinding.itemPriceTv.text = Constant.currency + "" +
                products[position]!!.total_item_price.toString()

        Glide.with(activity!!.baseContext).load(products[position]!!.picture)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.mMenuListitemBinding.menuItemImage)
        if (products[position]!!.itemsaddon!!.isNotEmpty())
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.VISIBLE
        else
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.GONE

//        var itemCount: Int = products!![position]!!.tot_quantity!!
        var itemCount = 0
        if (type.equals("viewcart", true)) {
            itemCount = products[position]!!.tot_quantity!!

        } else if (!products[position]!!.itemcart.isNullOrEmpty()) {
            itemCount = products[position]!!.itemcart!![0]!!.quantity!!

        }


        if(products[position]!!.status == 0){
            holder.mMenuListitemBinding.removecartBtn.visibility = View.VISIBLE
            holder.mMenuListitemBinding.addcartBtn.visibility = View.GONE
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.GONE
        } else {
            holder.mMenuListitemBinding.removecartBtn.visibility = View.GONE
            holder.mMenuListitemBinding.addcartBtn.visibility = View.VISIBLE
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.VISIBLE
        }

        holder.mMenuListitemBinding.removecartBtn.setOnClickListener {
            mOnMenuItemClickListner!!.removeCart(position)
            return@setOnClickListener
        }

        holder.mMenuListitemBinding.itemCountTv.text = itemCount.toString()

        if (itemCount == 0) {
            holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat
                    .getDrawable(activity as Context
                            , R.drawable.foodie_addons_minus_grey_bg)
        }
        holder.mMenuListitemBinding.itemcountAddBtn.setOnClickListener {
            ++itemCount
            holder.mMenuListitemBinding.itemCountTv.text = itemCount.toString()
            holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat
                    .getDrawable(activity as Context
                            , R.drawable.foodie_addons_minus_bg)

            if (itemCount == 1) {
                if (products[position]!!.itemsaddon!!.isNotEmpty()) {

                    mOnMenuItemClickListner!!.showAddonLayout(products[position]!!.id, itemCount
                            , products[position]!!, true)
                }

            } else {
                mOnMenuItemClickListner!!.addToCart(products[position]!!.id, itemCount,
                        products[position]!!.cartId, 1, 0)

            }

        }
        holder.mMenuListitemBinding.itemcountMinusBtn.setOnClickListener {
            if (itemCount > 0) {
                --itemCount
                holder.mMenuListitemBinding.itemCountTv.text = itemCount.toString()
                if (itemCount == 0) {

                    holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat
                            .getDrawable(activity as Context
                                    , R.drawable.foodie_addons_minus_grey_bg)
                    mOnMenuItemClickListner!!.removeCart(position)
                    return@setOnClickListener
                }
                mOnMenuItemClickListner!!.addToCart(products[position]!!.id, itemCount,
                        products[position]!!.cartId, 1, 0)
            }
        }
    }

    inner class MyViewHolder(itemView: RestaurantdetailViewcartmenuListitemBinding) :
            RecyclerView.ViewHolder(itemView.root) {

        val mMenuListitemBinding = itemView

        fun bind() {

        }


    }
}