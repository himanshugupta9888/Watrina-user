package com.gox.app.ui.invitereferals

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.base.BaseViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.data.PreferenceHelper
import com.gox.basemodule.data.getValue
import com.gox.app.data.repositary.AppRepository
import com.gox.app.data.repositary.remote.model.ProfileResponse

class InviteReferalsViewModel : BaseViewModel<InviteReferalsNavigator>() {
    var mProfileResponse = MutableLiveData<ProfileResponse>()
    var mUserReferralCount: ObservableField<String> = ObservableField("")
    var mUserReferralAmount: ObservableField<String> = ObservableField("")
    var mReferralCode: ObservableField<String> = ObservableField("")
    var mReferralInviteText: ObservableField<String> = ObservableField("")
    var errorResponse = MutableLiveData<String>()
    var loadingProgress = MutableLiveData<Boolean>()
    private val preferenceHelper = PreferenceHelper(BaseApplication.baseApplication)

    private val appRepository = AppRepository.instance()
    fun shareMyReferalCode() {
        navigator.goToInviteOption()
    }

    fun getProfile() {
        loadingProgress.value = true
        getCompositeDisposable().add(appRepository
                .getUserProfile(this, Constant.M_TOKEN + preferenceHelper.getValue(PreferenceKey.ACCESS_TOKEN, "").toString()))

    }

    private fun setUserReferralAmount(userReferralAmount: String) {
        mUserReferralAmount.set(userReferralAmount)
    }

    private fun setUserReferralCount(userReferralCount: String) {
        mUserReferralCount.set(userReferralCount)
    }

    private fun setInviteText(referralAmount: String, referralCount: String) {
        mReferralInviteText.set("Invite your friends and earn $$referralAmount for every $referralCount new users")
    }

    private fun setReferralCode(referralCode: String) {
        mReferralCode.set(referralCode)
    }


    fun getProfileResponse(): MutableLiveData<ProfileResponse> {
        return mProfileResponse
    }
}