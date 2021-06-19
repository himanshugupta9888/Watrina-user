package com.gox.basemodule.common.payment

object WebApiConstants {

    object AddWallet {
        const val AMOUNT = "amount"
        const val CARD_ID = "card_id"
        const val USER_TYPE = "user_type"
        const val PAYMENT_MODE = "payment_mode"
    }

    object addCard {
        const val STRIP_TOKEN = "stripe_token"
    }
}