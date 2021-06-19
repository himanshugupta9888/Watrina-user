package com.gox.basemodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gox.basemodule.R
import com.gox.basemodule.data.PlacesModel

class PlaceListAdapter(context: Context, placesList: ArrayList<PlacesModel>) :
        ArrayAdapter<PlacesModel>(context, R.layout.row_place_list) {

    var placeList: ArrayList<PlacesModel>? = ArrayList()
    var ctxt: Context? = null

    init {
        this.placeList = placesList
        this.ctxt = context
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.row_place_list, parent, false)
        if (!placeList.isNullOrEmpty())
            view.findViewById<TextView>(R.id.area).setText(placeList!!.get(position).mFullAddress)
        return view
    }
    override fun getItem(position: Int): PlacesModel? {
        return placeList!!.get(position)
    }
    override fun getCount(): Int {
        return placeList!!.size
    }

}