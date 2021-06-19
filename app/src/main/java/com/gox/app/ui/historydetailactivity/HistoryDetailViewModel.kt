package com.gox.app.ui.historydetailactivity

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.DisputeListModel
import com.gox.app.data.repositary.remote.model.DisputeStatusModel
import com.gox.app.data.repositary.remote.model.HistoryDetailModel
import com.gox.app.data.repositary.remote.model.ResponseData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.common.payment.model.ResCommonSuccessModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.getValue
import okhttp3.ResponseBody

class HistoryDetailViewModel : BaseViewModel<CurrentOrderDetailsNavigator>() {

    private val appRepository = AppRepository.instance()
    val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    var historyDetailResponse = MutableLiveData<HistoryDetailModel>()
    var historyUpcomingDetailResponse = MutableLiveData<HistoryDetailModel>()
    var disputeListData = MutableLiveData<DisputeListModel>()
    var disputeStatusResponse = MutableLiveData<DisputeStatusModel>()
    var addDisputeResponse = MutableLiveData<ResponseBody>()
    var addLostItemResponse = MutableLiveData<ResponseData>()
    var selectedDisputeName = MutableLiveData<String>()
    var loadingProgress = MutableLiveData<Boolean>()
    var errorResponse = MutableLiveData<String>()
    var current_id = MutableLiveData<String>()
    val cancelSuccessResponse = MutableLiveData<ResCommonSuccessModel>()


    fun moveToMainActivity() {
        navigator.goBack()
    }

    fun getHistoryDeatail(selectedID: String, servicetype: String) {
        getCompositeDisposable().add(appRepository
                .getHistoryDetail(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),
                        selectedID, servicetype))
    }

    fun getUpcomingHistoryDeatail(selectedID: String, servicetype: String) {
        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getUpcomingHistoryDetail(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),
                        selectedID, servicetype))
    }

    fun getDisputeList(servicename: String) {

        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getDisputeList(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), servicename
                ))
    }


    fun getCancelRequest(servicetype: String, id: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = id
        loadingProgress.value = true

        getCompositeDisposable().add(appRepository
                .cancelScheduleRequest(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""),
                        servicetype, hashMap))
    }

    fun addDispute(hashMap: HashMap<String, String>, servicename: String) {


        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .addDispute(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap, servicename))
    }

    fun getDisputeStatus(id: Int
                         , servicename: String) {
        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getDisputeStatus(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), id.toString(), servicename
                ))
    }

    fun addLossItem(id: Int, lostitem: String?) {

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("id", id.toString())
        hashMap.put("lost_item_name", lostitem.toString())

        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .addLostItem(this
                        , Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, ""), hashMap
                ))
    }

    fun setSelectedValue(Selecteddata: String) {
        selectedDisputeName.value = Selecteddata
    }

    fun getSelectedValue(): MutableLiveData<String> {
        return selectedDisputeName
    }

    fun dispute() {
        navigator.onClickDispute()
    }

    fun viewRecepit() {
        navigator.onClickViewRecepit()
    }

    fun lossItem() {

        navigator.onClickLossItem()
    }

    fun cancel() {
        navigator.onClickCancelBtn()

    }


}
