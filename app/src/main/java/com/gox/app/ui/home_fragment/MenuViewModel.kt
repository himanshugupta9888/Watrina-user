package com.gox.app.ui.home_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gox.app.data.repositary.remote.model.HomeMenuResponse

class MenuViewModel : ViewModel() {
    private val menuIcon = MutableLiveData<String>()
    private val menuTitle = MutableLiveData<String>()

    fun bind(menu: HomeMenuResponse.ResponseData.Service) {
        menuIcon.value = menu.icon
        menuTitle.value = menu.title
    }

    fun getMenuTitle(): MutableLiveData<String> {
        return menuTitle
    }

    fun getMenuIcon(): MutableLiveData<String> {
        return menuIcon
    }
}