package com.gox.foodiemodule.ui.resturantdetail_activity.custom

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class CategoryAdapter internal constructor(activity: AppCompatActivity, fm: FragmentManager, fragList: Vector<Fragment>, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    private var fragmentList: Vector<Fragment>? = null
    private var tabTitle: String? = ""
    private var context: Context? = null
    private var categoryId =""


    init {
        this.fragmentList = fragList
        this.context = context
    }

    override fun getItemCount(): Int {
        return fragmentList!!.size
    }

    override fun createFragment(position: Int): Fragment {
        var bundle=Bundle()

        return fragmentList!!.get(position)
    }

    fun setTabInfo(myActivityString: String) {
        this.categoryId = ""
    }
}