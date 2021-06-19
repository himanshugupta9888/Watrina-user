package com.gox.basemodule.common.cardlist

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.WebApiConstants
import com.gox.basemodule.common.payment.model.AddCardModel
import com.gox.basemodule.common.payment.model.CardListModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import com.gox.basemodule.repositary.ApiListener
import com.gox.basemodule.repositary.BaseModuleRepository

class CardListViewModel : BaseViewModel<CardListNavigator>() {
    var mRepositary = BaseModuleRepository.instance()
    var loadingProgress = MutableLiveData<Boolean>()
    var selectedCardID = MutableLiveData<String>()
    var selectedStripeID = MutableLiveData<String>()
    var addCardLiveResposne = MutableLiveData<AddCardModel>()
    var deleCardLivResponse = MutableLiveData<JsonObject>()
    var cardListLiveResponse = MutableLiveData<CardListModel>()
    var amount = MutableLiveData<String>()
    var paymentType = MutableLiveData<String>()
    var resources: Resources? = null
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)


    init {
        this.resources = resources
    }

    fun saveCard() {
        navigator.addCard()
    }

    fun deselectCard() {
        navigator.deselectCard()
    }

    fun removeCard() {
        navigator.removeCard()
    }

    fun cardDeselect() {
        navigator.deselectCard()
    }

    fun getCardList() {
        loadingProgress.let { it.value = true }
        getCompositeDisposable().add(mRepositary.getCardList(object : ApiListener {
            override fun onSuccess(successData: Any) {
                cardListLiveResponse.value = successData as CardListModel
            }

            override fun onError(error: Throwable) {
                navigator.showErrorMsg(getErrorMessage(error)!!)
            }

        }))
    }


    fun callAddCardApi(stripeID: String) {
        loadingProgress?.let { it.value = true }
        val params = HashMap<String, String>()
        params[WebApiConstants.addCard.STRIP_TOKEN] = stripeID
        getCompositeDisposable().add(mRepositary.addCard(this, params, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))
    }

    fun callCardDeleteCardAPi() {
        loadingProgress?.let { it.value = true }
        if (!selectedCardID.value.isNullOrEmpty())
            getCompositeDisposable().add(mRepositary.deleteCDard(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString(), selectedCardID.value!!))
    }
}