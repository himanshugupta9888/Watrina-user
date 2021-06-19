package com.gox.taximodule.ui.fragment.searchpage

import com.gox.basemodule.base.BaseViewModel

class SearchPageViewModel : BaseViewModel<SearchPageNavigator>() {

    fun moveToFlow() = navigator.moveToFlow()

    fun cancelRequest() = navigator.cancelRequest()
}
