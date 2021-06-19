package com.gox.app.ui.home_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.gox.app.BR
import com.gox.app.R
import com.gox.app.callbacks.OnClickListener
import com.gox.app.data.repositary.remote.model.HomeMenuResponse
import com.gox.app.databinding.ItemMostRecentServiceBinding


class MostRecentAdapter : Adapter<RecyclerView.ViewHolder>() {
    private var recentRequests: List<HomeMenuResponse.ResponseData.Service> = ArrayList()
    lateinit var mListener: OnClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mBinding: ItemMostRecentServiceBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_most_recent_service, parent, false
        )

        return MostRecentVh(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MostRecentVh).bindData(recentRequests[position])
    }

    override fun getItemCount(): Int {
        return recentRequests.size
    }

    fun setData(recentRequests: List<HomeMenuResponse.ResponseData.Service>) {
        this.recentRequests = recentRequests
        notifyDataSetChanged()
    }

    fun getItem(index: Int): HomeMenuResponse.ResponseData.Service {
        return recentRequests[index]
    }

    fun onItemClick(position: Int) {
        mListener.onClick(position)
    }

    fun setOnClickListener(mListener: OnClickListener) {
        this.mListener = mListener
    }

    inner class MostRecentVh(private val mBinding: ItemMostRecentServiceBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(service: HomeMenuResponse.ResponseData.Service) {
            mBinding.setVariable(BR.item, service)
            mBinding.setVariable(BR.adapter, this@MostRecentAdapter)
            mBinding.setVariable(BR.position, adapterPosition)
            mBinding.executePendingBindings()
        }
    }
}

