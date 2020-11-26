package com.zealsoftsol.medico.utils

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

class PhoneNumberFormatter(private val countryCode: String) {
    private var phoneNumber: Phonenumber.PhoneNumber? = null
    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun verifyNumber(number: String): String? {
        return try {
            phoneNumber = util.parse(number, countryCode)
            if (phoneNumber.isValid(true)) {
                "${phoneNumber!!.countryCode}${phoneNumber!!.nationalNumber}"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

object PhoneParser {
    private val utils: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    private var locale = ""

//    fun parseInternational(number: String): Phonenumber.PhoneNumber? = safeCall {
//        if (number.startsWith("+"))
//            utils.parse(number, locale)
//        else try {
//            utils.parse("+$number", locale)
//        } catch (e: Exception) {
//            utils.parse(number, locale)
//        }
//    }
//
//    fun parse(number: String): Phonenumber.PhoneNumber? = safeCall { utils.parse(number, locale) }

    fun format(phoneNumber: Phonenumber.PhoneNumber, value: Boolean): String =
        if (!value)
            "${phoneNumber.countryCode}${phoneNumber.nationalNumber}"
        else
            utils.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

    fun isValid(phoneNumber: Phonenumber.PhoneNumber, acceptAnyType: Boolean) =
        utils.isValidNumber(phoneNumber) && (acceptAnyType || utils.getNumberType(phoneNumber).let {
            it == PhoneNumberUtil.PhoneNumberType.MOBILE || it == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE
        })

    fun updateLocale(value: String) {
        locale = value
    }

    fun getCountryCode(locale: String) = "+${utils.getCountryCodeForRegion(locale)}"
}

inline fun Phonenumber.PhoneNumber?.formatted() =
    if (this != null) PhoneParser.format(this, true) else ""

inline fun Phonenumber.PhoneNumber?.plain() =
    if (this != null) PhoneParser.format(this, false) else ""

inline fun Phonenumber.PhoneNumber?.isValid(acceptAnyType: Boolean = false) =
    if (this != null) PhoneParser.isValid(this, acceptAnyType) else false