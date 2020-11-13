package com.zealsoftsol.medico.core.utils

import com.zealsoftsol.medico.data.AuthCredentials

sealed class TextFieldVerifier<T> {
    abstract fun verify(string: String): T?
}

class PhoneEmailVerifier : TextFieldVerifier<AuthCredentials.Type>() {

    private var currentType: AuthCredentials.Type? = null
    private val phoneNumberRegex = Regex("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*\$")
    private val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    override fun verify(string: String): AuthCredentials.Type? {
        checkType(string)
        return currentType
    }

    private fun checkType(string: String) {
        currentType = when {
            phoneNumberRegex.matches(string) -> AuthCredentials.Type.PHONE
            emailRegex.matches(string) -> AuthCredentials.Type.EMAIL
            string.isEmpty() -> null
            else -> currentType
        }
    }
}