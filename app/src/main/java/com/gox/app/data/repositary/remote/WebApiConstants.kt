package com.gox.app.data.repositary.remote

object WebApiConstants {

    const val SALT_KEY = "salt_key"
    const val REQUESTED_WITH = "X-Requested-With"
    const val HTTP_REQUEST = "XMLHttpRequest"
    const val AUTHORIZATION = "Authorization"

    const val OLD_PASSWORD = "old_password"
    const val NEW_PASSWORD = "password"
    const val CONFIRM_PASSWORD = "password_confirmation"

    object SignIn {
        const val COUNTRY_CODE = "country_code"
        const val MOBILE = "mobile"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }

    object SocialLogin {
        const val DEVICE_TYPE = "device_type"
        const val DEVICE_TOKEN = "device_token"
        const val LOGIN_BY = "login_by"
        const val SOCIAL_UNIQUE_ID = "social_unique_id"
    }

    object ForgotPassword {
        const val ACCOUNT_TYPE = "account_type"
        const val COUNTRY_CODE = "country_code"
        const val MOBILE = "mobile"
        const val EMAIL = "email"
    }

    object ResetPassword {
        const val ACCOUNT_TYPE = "account_type"
        const val COUNTRY_CODE = "country_code"
        const val USERNAME = "username"
        const val OTP = "otp"
        const val PASSWORD = "password"
        const val PASSWORD_CONFIRMATION = "password_confirmation"
    }

    object SignUp {
        const val DEVICE_TYPE = "device_type"
        const val DEVICE_TOKEN = "device_token"
        const val LOGIN_BY = "login_by"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val GENDER = "gender"
        const val COUNTRY_CODE = "country_code"
        const val MOBILE = "mobile"
        const val CONFIRM_PASSWORD = "password_confirmation"
        const val COUNTRY_ID = "country_id"
        const val STATE_ID = "state_id"
        const val CITY_ID = "city_id"
    }

    object ChangePassword {
        const val OLD_PASSWORD = "old_password"
        const val PASSWORD = "password"
    }

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