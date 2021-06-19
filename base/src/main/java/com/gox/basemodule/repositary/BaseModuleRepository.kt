package com.gox.basemodule.repositary

import android.util.Log
import com.gox.basemodule.common.edit_address.EditViewModel
import com.gox.basemodule.common.add_address.AddAddressViewModel
import com.gox.basemodule.common.chatmessage.ChatMainViewModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.common.manage_address.ManageAddressViewModel
import com.gox.basemodule.common.payment.transaction.TransactionViewModel
import com.gox.basemodule.common.payment.wallet.WalletViewModel
import com.gox.basemodule.common.cardlist.CardListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class BaseModuleRepository : BaseRepository() {

    fun getCardList(listener: ApiListener): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .getCardList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    listener.onSuccess(it)
                }, {
                    listener.onError(it)
                })
    }

    fun getProfile(listener: ApiListener): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .getUserProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    listener.onSuccess(it)
                }, {
                    listener.onError(it)
                })
    }

    fun addWalletAmount(listener: ApiListener, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .addWalletMoney(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    listener.onSuccess(it)
                }, {
                    listener.onError(it)
                })
    }

    fun getTransaction(viewModel: TransactionViewModel, token: String, params: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .getWalletTransction(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.transcationLiveResponse.postValue(it)
                }, {
                    viewModel.showLoading.value = false
                    Log.e("Error", "-----" + it.message)
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun deleteCDard(viewModel: CardListViewModel, token: String, cardId: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .deleteCard(token, cardId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.deleCardLivResponse.postValue(it)
                }, {
                    viewModel.navigator.showErrorMsg(getErrorMessage(it)!!)
                })

    }

    fun addCard(viewModel: CardListViewModel, params: HashMap<String, String>, token: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .addCard(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.addCardLiveResposne.postValue(it)
                }, {
                    viewModel.navigator.showErrorMsg(getErrorMessage(it)!!)
                })
    }

    fun getMessage(viewModel: ChatMainViewModel, token: String, adminService: String, id: Int): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, BaseWebService::class.java)
                .getMessages(token, adminService, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.getMessageResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }

    fun sendMessage(viewModel: ChatMainViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.TAXI_BASE_URL, BaseWebService::class.java)
                .sendMessage(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.successResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                }

                )
    }


    fun getAddressList(viewModel: ManageAddressViewModel, token: String): Disposable {
        viewModel.loading.value = true
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .getAddressList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.addressListResponse.value = it
                    viewModel.loading.value = false
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                    viewModel.loading.value = false
                })
    }

    fun deleteAddress(viewModel: ManageAddressViewModel, token: String, addressId: String): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .deleteAddress(token, addressId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.deleteAddressResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun addAddress(viewModel: AddAddressViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .addAddress(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.addAddressSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun editAddress(viewModel: EditViewModel, token: String, params: HashMap<String, Any>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .editAddress(token, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    viewModel.editAddressSuccessResponse.value = it
                }, {
                    viewModel.errorResponse.value = getErrorMessage(it)
                })
    }

    fun walletTransfer(listener: ApiListener, param: HashMap<String, String>): Disposable {
        return BaseRepository().createApiClient(Constant.BaseUrl.APP_BASE_URL, BaseWebService::class.java)
                .sendWalletMoney(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ listener.onSuccess(it) }, { listener.onError(it) })
    }


    companion object {
        private var appRepository: BaseModuleRepository? = null

        fun instance(): BaseModuleRepository {
            if (appRepository == null) synchronized(BaseModuleRepository) {
                appRepository = BaseModuleRepository()
            }
            return appRepository!!
        }
    }




}
