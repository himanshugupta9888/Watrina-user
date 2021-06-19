package com.gox.foodiemodule.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.foodiemodule.R
import com.gox.foodiemodule.data.model.ResturantListModel
import com.gox.foodiemodule.databinding.RestaurantlistItemBinding

class RestaurantListAdapter(activity: FragmentActivity?, val resturantList: List<ResturantListModel.ResponseData?>, val selectedSearch: String)
    : RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder>() {

    val activity = activity
    private var mOnAdapterClickListener: FoodieItemClickListner? = null
    fun setOnClickListener(onClickListener: FoodieItemClickListner) {
        this.mOnAdapterClickListener = onClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflate = DataBindingUtil.inflate<RestaurantlistItemBinding>(LayoutInflater.from(parent.context)
                , R.layout.restaurantlist_item, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = resturantList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()

        if(resturantList[position]!!.storetype!!.category!!.equals("FOOD"))
            holder.resturantlistItemBinding.estTimeTv.visibility = View.VISIBLE
        else
            holder.resturantlistItemBinding.estTimeTv.visibility = View.GONE

        if (resturantList[position]!!.shopstatus.equals("closed", true))
            holder.resturantlistItemBinding.closedLay.visibility = View.VISIBLE
        else
            holder.resturantlistItemBinding.closedLay.visibility = View.GONE

        Glide.with(activity!!.baseContext).load(resturantList[position]!!.picture)
                .placeholder(R.drawable.image_placeholder).into(holder.resturantlistItemBinding.resturantImage)

        if(selectedSearch == "store") {
            holder.resturantlistItemBinding.ratingLay.visibility = View.VISIBLE
            holder.resturantlistItemBinding.offerTv.visibility = View.VISIBLE
            holder.resturantlistItemBinding.offerTv.text = "" + resturantList[position]!!.offer_percent + activity.getString(R.string.all_orders)
        } else {
            if(resturantList[position]!!.item_discount != 0) {
                holder.resturantlistItemBinding.offerTv.visibility = View.VISIBLE
                holder.resturantlistItemBinding.offerTv.text = "" + resturantList[position]!!.item_discount + "% discount"
            } else
                holder.resturantlistItemBinding.offerTv.visibility = View.GONE
            holder.resturantlistItemBinding.restaturantCusinetypeTv.text = "" + resturantList[position]!!.category
            holder.resturantlistItemBinding.ratingLay.visibility = View.GONE
        }

        holder.resturantlistItemBinding.resturantRatingTv.text = "" + resturantList[position]!!.rating.toString()

        if(resturantList[position]!!.estimated_delivery_time.toString() != "")
            holder.resturantlistItemBinding.estTimeTv.text = resturantList[position]!!.estimated_delivery_time.toString() + activity.getString(R.string.mins)
        else
            holder.resturantlistItemBinding.estTimeTv.text = ""

      /*  if(resturantList[position]!!.storetype!!.category.equals("FOOD")){
            holder.resturantlistItemBinding.restaturantCusinetypeTv.text = resturantList[position]!!.cusine_list.toString()
        }else{
            var categoryName = ""
            for (i in resturantList[position]!!.categories!!.indices) {
                if (i == 0)
                    categoryName = resturantList[position]!!.categories!!.get(i)!!.store_category_name!!
                else
                    categoryName = categoryName + ", " + resturantList[position]!!.categories!!.get(i)!!.store_category_name
            }
            holder.resturantlistItemBinding.restaturantCusinetypeTv.text = categoryName
        }*/

        var categoryName = ""
        for (i in resturantList[position]!!.categories!!.indices) {
            if (i == 0)
                categoryName = resturantList[position]!!.categories!!.get(i)!!.store_category_name!!
            else
                categoryName = categoryName + ", " + resturantList[position]!!.categories!!.get(i)!!.store_category_name
        }
        if(categoryName != "")
            holder.resturantlistItemBinding.restaturantCusinetypeTv.text = categoryName



        if (selectedSearch.equals("dish")) {
            holder.resturantlistItemBinding.restaturantName.text = resturantList[position]!!.item_name

        } else {
            holder.resturantlistItemBinding.restaturantName.text = resturantList[position]!!.store_name

        }


       /* holder.resturantlistItemBinding.resturantItemClick = object : FoodieItemClickListner {
            override fun restutantItemClick() {

                if (!resturantList[position]!!.shopstatus.equals("closed", true)) {
                    val intent = Intent(activity, RestaurantDetailActivity::class.java)
                    intent.putExtra("restaurantsId", resturantList[position]!!.id.toString())
                    activity.startActivity(intent)
                }


            }
        }*/

        holder.resturantlistItemBinding.resturantListCardView.setOnClickListener {
            if (mOnAdapterClickListener != null) {
                mOnAdapterClickListener!!.restutantItemClick(position)
            }
        }


    }


    inner class MyViewHolder(itemView: RestaurantlistItemBinding) : RecyclerView.ViewHolder(itemView.root) {

        val resturantlistItemBinding = itemView

        fun bind() {

        }


    }


}