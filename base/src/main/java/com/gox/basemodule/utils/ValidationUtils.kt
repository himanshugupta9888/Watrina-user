package com.gox.basemodule.utils

import android.util.Patterns

object ValidationUtils {


    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isMinLength(input: String, minLength: Int): Boolean {
        return minLength >= input.length
    }

}

