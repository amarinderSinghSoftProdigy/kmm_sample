package com.zealsoftsol.medico.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

class PhoneNumberFormatter(private val countryCode: String) {
    private var phoneNumber: Phonenumber.PhoneNumber? = null
    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun verifyNumber(number: String): String? {
        return runCatching {
            phoneNumber = util.parse(number, countryCode)
            if (util.isValidNumber(phoneNumber)) {
                util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            } else {
                null
            }
        }.getOrNull()
    }
}