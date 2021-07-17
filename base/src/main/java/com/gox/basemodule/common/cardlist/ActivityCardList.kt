package com.gox.basemodule.common.cardlist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cooltechworks.creditcarddesign.CardEditActivity
import com.cooltechworks.creditcarddesign.CreditCardUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.gox.basemodule.BaseApplication
import com.gox.basemodule.R
import com.gox.basemodule.base.BaseActivity
import com.gox.basemodule.common.payment.adapter.CardOnClickListener
import com.gox.basemodule.common.payment.adapter.CardsAdapter
import com.gox.basemodule.common.payment.adapter.PaymentModeAdapter
import com.gox.basemodule.common.payment.adapter.PaymentTypeListener
import com.gox.basemodule.common.payment.model.AddCardModel
import com.gox.basemodule.common.payment.model.CardListModel
import com.gox.basemodule.common.payment.model.CardResponseModel
import com.gox.basemodule.data.Constant
import com.gox.basemodule.data.PreferenceKey
import com.gox.basemodule.databinding.ActivitySavedCardListBinding
import com.gox.basemodule.model.ConfigDataModel
import com.gox.basemodule.utils.ViewUtils
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token

class ActivityCardList : BaseActivity<ActivitySavedCardListBinding>(), CardListNavigator {

    private lateinit var cardListViewModel: CardListViewModel
    private lateinit var activitySavedCardListBinding: ActivitySavedCardListBinding
    private lateinit var context: Context
    private var strAmount: String? = null
    private var mCardNumber: String? = ""
    private var mCardCVV: String? = ""

    private var mCardExpiryDate: String? = ""
    private var mCardHolderName: String? = ""
    private var selectedCardID: String? = ""
    private lateinit var cardsAdapter: CardsAdapter
    private var cardList: MutableList<CardResponseModel>? = null
    private var selectedPosition: Int? = -1
    private var isFromWallet: Boolean = false
    private var paymentModeAdapter: PaymentModeAdapter? = null
    private var paymentList: List<ConfigDataModel.BaseApiResponseData.Appsetting.Payment>? = null
    private var activityResultFlag: String? = null
    private var mPaymentType: String? = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_saved_card_list
    }

    override fun initView(mViewDataBinding: ViewDataBinding?) {
        context = this
        activitySavedCardListBinding = mViewDataBinding as ActivitySavedCardListBinding
        cardListViewModel = CardListViewModel()
        cardListViewModel.navigator = this
        activitySavedCardListBinding.cardListViewModel = cardListViewModel
        activitySavedCardListBinding.setLifecycleOwner(this)
        activityResultFlag = intent.getStringExtra("activity_result_flag")
        if (activityResultFlag.equals("1")) {
            if (intent.hasExtra("payment_type")) {
                mPaymentType = intent.getStringExtra("payment_type")
            }

        }
        if (mPaymentType.equals("CARD")) {
            activitySavedCardListBinding.rvPaymentModes.visibility = View.GONE
            activitySavedCardListBinding.tvPaymentTypes.visibility = View.GONE
        } else {
            activitySavedCardListBinding.rvPaymentModes.visibility = View.VISIBLE
            activitySavedCardListBinding.tvPaymentTypes.visibility = View.VISIBLE
        }
        getApiResponse()
        getIntentValues()
        val paytypes = object : TypeToken<List<ConfigDataModel.BaseApiResponseData.Appsetting.Payment>>() {}.type
        paymentList = Gson().fromJson(BaseApplication.getCustomPreference!!.getString(PreferenceKey.PAYMENTLIST, "") as String, paytypes)
        paymentList = paymentList!!.filter { it.status == "1" }

        val cardPaymentAvailable = paymentList!!.any { it.name.equals(Constant.PaymentMode.CARD, true) }
        activitySavedCardListBinding.rlPaymentCard.visibility = if (cardPaymentAvailable) View.VISIBLE else View.GONE

        val linearLayoutManager = LinearLayoutManager(this)
        activitySavedCardListBinding.rvPaymentModes.layoutManager = linearLayoutManager
        paymentModeAdapter = PaymentModeAdapter(context, paymentList!!, cardListViewModel, isFromWallet)
        activitySavedCardListBinding.rvPaymentModes.adapter = paymentModeAdapter
        paymentModeAdapter!!.setOnClickListener(mOnPaymentModeAdapterClickListener)
        cardListViewModel.loadingProgress = loadingObservable as MutableLiveData<Boolean>
        activitySavedCardListBinding.toolbarLayout.tvToolbarTitle.text = resources.getString(R.string.payments)
        activitySavedCardListBinding.toolbarLayout.ivToolbarBack.setOnClickListener { view -> finish() }
        if (isFromWallet) {
            if (!paymentList.isNullOrEmpty() && paymentList!!.size == 2) {
                activitySavedCardListBinding.rvPaymentModes.visibility = View.GONE
                activitySavedCardListBinding.tvPaymentTypes.visibility = View.GONE
            } else {
                activitySavedCardListBinding.rvPaymentModes.visibility = View.VISIBLE
                activitySavedCardListBinding.tvPaymentTypes.visibility = View.VISIBLE
                activitySavedCardListBinding.tvPaymentTypes.text = resources.getString(R.string.choose_payment)
            }
        }
        cardListViewModel.getCardList()
    }

    private fun getIntentValues() {
        isFromWallet = if (intent != null && intent.hasExtra("isFromWallet"))
            intent.getBooleanExtra("isFromWallet", false) else false
    }

    private fun getApiResponse() {
        cardListViewModel.addCardLiveResposne.observe(this, Observer<AddCardModel> { addCardModel ->
            if (addCardModel.getStatusCode().equals("200") && addCardModel.getResponseData() != null) {
                cardListViewModel.getCardList()
            }
        })

        cardListViewModel.cardListLiveResponse.observe(this, Observer<CardListModel> {
            cardListViewModel.loadingProgress.value = false
            if (cardListViewModel.cardListLiveResponse.value!!.getResponseData() != null && cardListViewModel.cardListLiveResponse.value!!.getResponseData()!!.size > 0) {
                activitySavedCardListBinding.ivEmptyCard.visibility = View.GONE
                activitySavedCardListBinding.rvCards.visibility = View.VISIBLE
                val linearLayoutManager = LinearLayoutManager(this)
                linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                cardList = cardListViewModel.cardListLiveResponse.value!!.getResponseData()
                if (cardList != null && cardList!!.size > 0) {
                    cardsAdapter = CardsAdapter(context, cardList!!, cardListViewModel)
                    activitySavedCardListBinding.rvCards.layoutManager = linearLayoutManager
                    activitySavedCardListBinding.rvCards.adapter = cardsAdapter
                    cardsAdapter.setOnClickListener(mOnAdapterClickListener)
                }
            }
        })

        cardListViewModel.deleCardLivResponse.observe(this, Observer<JsonObject> {
            cardListViewModel.loadingProgress.value = false
            if (cardListViewModel.deleCardLivResponse.value!!.get("statusCode").asString == "200") {
                cardList?.let { selectedPosition?.let { it1 -> it.removeAt(it1) } }
                selectedCardID = ""
                selectedPosition = -1
                activitySavedCardListBinding.ivDelete.visibility = View.GONE
                activitySavedCardListBinding.ivRemove.visibility = View.GONE
                if (cardList!!.size == 0) {
                    activitySavedCardListBinding.rvCards.visibility = View.GONE
                    activitySavedCardListBinding.ivEmptyCard.visibility = View.VISIBLE
                }
                cardsAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun addAmount(view: View) {
        when (view.id) {
            R.id.bt_fifty -> {
                strAmount = "50"
            }

            R.id.bt_hundred -> {
                strAmount = "100"
            }

            R.id.bt_thousand -> {
                strAmount = "1000"
            }
        }
        cardListViewModel.amount.value = strAmount

    }

    private val mOnAdapterClickListener = object : CardOnClickListener {
        override fun onClick(position: Int) {
            if (activityResultFlag.equals("1")) {
                val intent = Intent()
                intent.putExtra("payment_type", Constant.PaymentMode.CARD.toUpperCase())
                intent.putExtra("card_id", cardList?.get(position)?.getCardId())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }


    }
    private val mOnPaymentModeAdapterClickListener = object : PaymentTypeListener {
        override fun onClick(position: Int) {
            if (activityResultFlag.equals("1")) {
                val paymentType = paymentList!![position].name
                val intent = Intent()
                intent.putExtra("payment_type", paymentType)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun addCard() {
        val intent = Intent(this, CardEditActivity::class.java)
        intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, getString(R.string.card_holder_name))
        startActivityForResult(intent, Constant.RequestCode.ADDCARD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == Constant.RequestCode.ADDCARD) {
            if (data != null) {

                mCardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER)
                mCardExpiryDate = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY)
                mCardCVV = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV)
                mCardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME)// Show localized error message

                if (mCardNumber == null || mCardExpiryDate == null ||
                        mCardExpiryDate!!.length < 5 || mCardCVV == null || mCardHolderName == null) {
                    ViewUtils.showToast(this, getString(R.string.card_invalid), false)
                    return
                }
                processForStripeToken()
            } else {
                ViewUtils.showToast(this, "Add Card Cancelled", true)
            }

        }
    }

    private fun processForStripeToken() {
        val temp = mCardExpiryDate!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val month = Integer.parseInt(temp[0])
        val year = Integer.parseInt(temp[1])

        val card = Card(mCardNumber, month, year, mCardCVV)
        card.name = mCardHolderName

        if (card.validateCard()) {
            cardListViewModel.loadingProgress.value = true
            val stripe = Stripe(context, BaseApplication.getCustomPreference!!.getString(PreferenceKey.STRIPE_KEY, "") as String)
            stripe.createToken(card, object : TokenCallback {
                override fun onSuccess(token: Token) {
                    if (!TextUtils.isEmpty(token.id))
                        cardListViewModel.callAddCardApi(token.id)
                }

                override fun onError(error: Exception) {
                    // Show localized error message
                    cardListViewModel.loadingProgress.value = false

                }
            })
        } else {
            cardListViewModel.loadingProgress.value = false
            ViewUtils.showToast(this, getString(R.string.card_invalid), false)
        }
    }

    override fun cardPicked(stripeID: String, cardID: String, position: Int) {
        activitySavedCardListBinding.ivDelete.visibility = View.VISIBLE
        activitySavedCardListBinding.ivRemove.visibility = View.VISIBLE
        cardListViewModel.selectedStripeID.value = stripeID
        cardListViewModel.selectedCardID.value = cardID
        if (selectedPosition != -1) {
            selectedPosition?.let { cardList!!.get(it).isCardSelected = false }
            cardsAdapter.notifyItemChanged(selectedPosition!!)
        }
        this.selectedPosition = position
        selectedPosition?.let { cardList!!.get(it).isCardSelected = true }
        cardsAdapter.notifyItemChanged(selectedPosition!!)
        if (isFromWallet) {
            val intent = Intent()
            intent.putExtra("cardStripeID", cardListViewModel.selectedStripeID.value)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    override fun removeCard() {
        cardListViewModel.callCardDeleteCardAPi()
    }

    override fun deselectCard() {
        selectedPosition?.let { cardList!!.get(it).isCardSelected = false }
        cardsAdapter.notifyItemChanged(selectedPosition!!)
        activitySavedCardListBinding.ivRemove.visibility = View.GONE
        activitySavedCardListBinding.ivDelete.visibility = View.GONE
        cardListViewModel.selectedStripeID.value = ""
    }

    override fun paymentType(type: Int) {
    }

    override fun showErrorMsg(error: String) {
        cardListViewModel.loadingProgress.value = false
        ViewUtils.showToast(context, error, false)
    }

    override fun changePaymentMode(paymentId: Int) {

    }
}