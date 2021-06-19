package com.gox.taximodule.ui.activity.locationpick

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gox.basemodule.data.PlacesModel
import com.gox.taximodule.R
import com.gox.taximodule.callbacks.OnViewClickListener
import com.gox.taximodule.databinding.RowPlacesLayoutBinding

class PlacesAdapter(private val placesList: ArrayList<PlacesModel>) :
        RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private var mOnViewAdapterClickListener: OnViewClickListener? = null
    fun setOnClickListener(onViewClickListener: OnViewClickListener) {
        this.mOnViewAdapterClickListener = onViewClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesAdapter.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_places_layout, parent, false))
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

    class ViewHolder(itemView: RowPlacesLayoutBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindItems(places: PlacesModel) {
            val area = itemView.findViewById(R.id.area) as TextView
            val address = itemView.findViewById(R.id.address) as TextView

            area.text = places.mPrimary
            address.text = places.mSecondary

        }
    }
}