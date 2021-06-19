package com.gox.xubermodule.ui.activity.locationpick

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.data.PlacesModel
import com.gox.xubermodule.R
import com.gox.xubermodule.databinding.XuberRowPlacesLayoutBinding
import com.gox.xubermodule.ui.adapter.OnViewClickListener

class PlacesAdapter(private val placesList: ArrayList<PlacesModel>) :
        RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private var mOnViewAdapterClickListener: OnViewClickListener? = null
    fun setOnClickListener(onViewClickListener: OnViewClickListener) {
        this.mOnViewAdapterClickListener = onViewClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesAdapter.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.xuber_row_places_layout, parent, false))
    }

    override fun onBindViewHolder(holder: PlacesAdapter.ViewHolder, position: Int) {
        holder.bindItems(placesList[position])
        holder.itemView.setOnClickListener {
            if (mOnViewAdapterClickListener != null) mOnViewAdapterClickListener!!.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    class ViewHolder(itemView: XuberRowPlacesLayoutBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindItems(places: PlacesModel) {
            val area = itemView.findViewById(R.id.area) as TextView
            val address = itemView.findViewById(R.id.address) as TextView

            area.text = places.mPrimary
            address.text = places.mSecondary

        }
    }
}