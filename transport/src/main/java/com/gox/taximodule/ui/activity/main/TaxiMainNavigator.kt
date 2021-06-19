package com.gox.taximodule.ui.activity.main

interface TaxiMainNavigator {
    fun goToLocationPick()
    fun goBack()
    fun showCurrentLocation()
    fun moveStatusFlow()
    fun goToSourceLocationPick()
    fun showService()
}