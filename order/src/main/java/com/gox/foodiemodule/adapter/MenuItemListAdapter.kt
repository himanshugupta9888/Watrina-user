package com.gox.foodiemodule.adapter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.graphics.Paint
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
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.Spannable
import android.widget.TextView
import com.gox.foodiemodule.databinding.RestaurantdetailMenuListitemBinding


class MenuItemListAdapter(activity: FragmentActivity?,
                          val resturantDetail: ResturantDetailsModel.ResponseData,
                          val type: String) :
        RecyclerView.Adapter<MenuItemListAdapter.MyViewHolder>() {
    val activity = activity
    val products: List<ResturantDetailsModel.ResponseData.Product?> = resturantDetail.products!!

    private var mOnMenuItemClickListner: MenuItemClickListner? = null

    fun setOnClickListener(onClickListener: MenuItemClickListner) {
        this.mOnMenuItemClickListner = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<RestaurantdetailMenuListitemBinding>(LayoutInflater.from(parent.context)
                , R.layout.restaurantdetail_menu_listitem, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        val productData = products[position]
        holder.mMenuListitemBinding.menuItemName.text = productData!!.item_name.toString()

        if (productData.status == 1) {
            holder.mMenuListitemBinding.llItemAdd.visibility = View.VISIBLE
            holder.mMenuListitemBinding.tvItemstatus.visibility = View.GONE

        } else {
            holder.mMenuListitemBinding.llItemAdd.visibility = View.GONE
            holder.mMenuListitemBinding.tvItemstatus.visibility = View.VISIBLE
        }

        if (productData!!.is_veg.toString().equals("Pure Veg"))
            holder.mMenuListitemBinding.imgVeg.setImageResource(R.drawable.ic_veg_transparent)
        else
            holder.mMenuListitemBinding.imgVeg.setImageResource(R.drawable.ic_non_veg_transparent)

        if (!productData!!.shop_status.isNullOrEmpty() && productData!!.shop_status!!.toUpperCase() == "BESTSELLER")
            holder.mMenuListitemBinding.bestSeller.visibility = View.VISIBLE
        else if (productData!!.shop_status != null && productData!!.shop_status!!.toUpperCase() == "NEW") {
            holder.mMenuListitemBinding.bestSeller.visibility = View.VISIBLE
            holder.mMenuListitemBinding.bestSeller.setBackgroundTintList(ContextCompat.getColorStateList(activity!!, R.color.colorBasePrimary))
        } else
            holder.mMenuListitemBinding.bestSeller.visibility = View.GONE


        if (productData.offer == 1) {
            holder.mMenuListitemBinding.originalPriceTv.visibility = View.VISIBLE
            holder.mMenuListitemBinding.originalPriceTv.text = Constant.currency + "" +
                    productData.product_offer.toString()
            multiLineStrikeThrough(holder.mMenuListitemBinding.itemPriceTv, productData.item_price.toString())
        } else {
            holder.mMenuListitemBinding.originalPriceTv.visibility = View.GONE
            holder.mMenuListitemBinding.itemPriceTv.paintFlags = holder.mMenuListitemBinding.itemPriceTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.mMenuListitemBinding.itemPriceTv.text = Constant.currency + "" + productData.item_price.toString()
        }

        if (productData.quantity != null) {
            holder.mMenuListitemBinding.txtQuantity.visibility = View.VISIBLE
            if (productData.unit != null && productData.unit!!.name != null)
                holder.mMenuListitemBinding.txtQuantity.text = "Qty : " + productData.quantity.toString() + " " + productData.unit!!.name
            else
                holder.mMenuListitemBinding.txtQuantity.text = "Qty : " + productData.quantity.toString()
        } else {
            holder.mMenuListitemBinding.txtQuantity.visibility = View.GONE
        }


        Glide.with(activity!!.baseContext).load(productData.picture)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.mMenuListitemBinding.menuItemImage)

        if (productData.itemsaddon!!.isNotEmpty())
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.VISIBLE
        else
            holder.mMenuListitemBinding.coustomizableTv.visibility = View.GONE

        var totalCount = productData!!.itemcart!!.sumBy { it?.quantity!! }
        holder.mMenuListitemBinding.itemCountTv.text = totalCount.toString()

        if (totalCount == 0) {
            holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat.getDrawable(activity as Context, R.drawable.foodie_addons_minus_grey_bg)
        }

        holder.mMenuListitemBinding.itemcountAddBtn.setOnClickListener {
            if (totalCount > 0) {
                AlertDialog.Builder(activity)
                        .setTitle(R.string.app_name)
                        .setMessage(activity.getString(R.string.repeat_customization))
                        .setNegativeButton(activity.getString(R.string.will_choose)) { dialog, which ->
                            mOnMenuItemClickListner!!.showAddonLayout(productData!!.id, 1
                                    , products[position]!!, true)
                        }
                        .setPositiveButton(activity.getString(R.string.repeat)) { dialog, which ->
                            incrementItems(totalCount, holder, position, 1)
                        }
                        .show()
            } else {
                incrementItems(totalCount, holder, position, 0)
            }


        }
        holder.mMenuListitemBinding.itemcountMinusBtn.setOnClickListener {
            if (totalCount > 0) {
                --totalCount
                holder.mMenuListitemBinding.itemCountTv.text = totalCount.toString()
                if (totalCount > 0 && productData.itemcart?.size!! == 1) {
                    mOnMenuItemClickListner!!.addToCart(products[position]!!.id, totalCount,
                            productData.itemcart?.get(0)?.id, 1, 0)
                } else {
                    holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat
                            .getDrawable(activity as Context
                                    , R.drawable.foodie_addons_minus_grey_bg)
                    mOnMenuItemClickListner!!.removeCart(position)
                    return@setOnClickListener
                }
            }
        }


    }

    private fun multiLineStrikeThrough(description: TextView, textContent: String) {
        description.setText(textContent, TextView.BufferType.SPANNABLE)
        val spannable = description.text as Spannable
        spannable.setSpan(StrikethroughSpan(), 0, textContent.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    private fun incrementItems(itemCount: Int, holder: MyViewHolder, position: Int, repeat: Int) {
        var itemCount1 = itemCount
        ++itemCount1
        holder.mMenuListitemBinding.itemCountTv.text = itemCount1.toString()
        holder.mMenuListitemBinding.itemcountMinusBtn.background = ContextCompat
                .getDrawable(activity as Context
                        , R.drawable.foodie_addons_minus_bg)

        if (itemCount1 == 1 && products[position]!!.itemsaddon!!.isNotEmpty()) {
            mOnMenuItemClickListner!!.showAddonLayout(products[position]!!.id, itemCount1
                    , products[position]!!, true)
        } else {
            mOnMenuItemClickListner!!.addToCart(products[position]!!.id, itemCount1,
                    resturantDetail.products!![position]!!.cartId, repeat, 0)
        }
    }


    inner class MyViewHolder(itemView: RestaurantdetailMenuListitemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val mMenuListitemBinding = itemView

        fun bind() {

        }


    }
}