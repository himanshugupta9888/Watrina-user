package com.gox.xubermodule.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gox.xubermodule.R
import com.gox.xubermodule.data.model.ProviderListModel
import com.gox.xubermodule.data.model.XuberServiceClass
import com.gox.xubermodule.databinding.ProvidersRowItemListBinding
import com.gox.xubermodule.ui.activity.providerdetail.ProviderDetailActivity
import com.gox.xubermodule.utils.Utils.getPrice
import java.text.DecimalFormat


class ProviderListAdapter(val activity: FragmentActivity?, private var providerListData: ArrayList<ProviderListModel.ResponseData.ProviderService>)
    : RecyclerView.Adapter<ProviderListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = DataBindingUtil.inflate<ProvidersRowItemListBinding>(LayoutInflater.from(parent.context)
                , R.layout.providers_row_item_list, parent, false)
        return MyViewHolder(inflate)
    }

    override fun getItemCount(): Int = providerListData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        Glide.with(activity!!).load(providerListData[position].picture).placeholder(R.drawable.ic_profile_place_holder).into(holder.providersRowItemListBinding.providerImg)
        holder.providersRowItemListBinding.providerNameTv.text = providerListData[position].first_name.toLowerCase()
        holder.providersRowItemListBinding.providerBaseFareTv.text = getPrice(providerListData[position], activity)
        holder.providersRowItemListBinding.providerRatingTv.text = providerListData[position].rating
        holder.providersRowItemListBinding.rlProviderList.setOnClickListener {
            val intent = Intent(activity, ProviderDetailActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
            XuberServiceClass.providerListModel = providerListData!![position]
        }
        holder.providersRowItemListBinding.providerDistanceTv.text = DecimalFormat("##.##")
                .format(providerListData!![position].distance.toDouble()) + activity.getString(R.string.km_away)
    }

    inner class MyViewHolder(itemView: ProvidersRowItemListBinding)
        : RecyclerView.ViewHolder(itemView.root) {
        val providersRowItemListBinding = itemView
        fun bind() {

        }
    }


    fun filterList(filterdNames: ArrayList<ProviderListModel.ResponseData.ProviderService>) {
        providerListData = filterdNames
        notifyDataSetChanged()
    }
    /*override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    providerList = providerListData
                } else {
                    val filteredList = ArrayList<ProviderListModel.ResponseData.ProviderService>()
                    for (row in providerList!!) {
                        if (row.first_name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    providerList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = providerList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                providerList = results?.values as List<ProviderListModel.ResponseData.ProviderService>
                notifyDataSetChanged()
            }

        }
    }*/
}
