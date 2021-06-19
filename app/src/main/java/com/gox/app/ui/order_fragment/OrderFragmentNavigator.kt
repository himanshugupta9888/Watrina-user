package com.gox.app.ui.order_fragment

interface OrderFragmentNavigator {
    fun goToCurrentOrder()
    fun goToPastOrder()
    fun goToUpcomingOrder()
    fun openFilterlayout()

}