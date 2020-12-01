package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.Location
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

sealed class SignUpScope : BaseScope(), CanGoBack {

    val canGoNext: DataSource<Boolean> = DataSource(false)

    protected abstract fun checkCanGoNext()

    data class SelectUserType(
        val userType: DataSource<UserType> = DataSource(UserType.STOCKIST),
    ) : SignUpScope() {

        init {
            canGoNext.value = true
        }

        override val isInProgress: Boolean
            get() = false

        fun chooseUserType(userType: UserType) {
            this.userType.value = userType
        }

        /**
         * Transition to [PersonalData]
         */
        fun goToPersonalData() =
            EventCollector.sendEvent(Event.Action.Registration.SelectUserType(userType.value))

        override fun checkCanGoNext() {

        }
    }

    data class PersonalData(
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        init {
            checkCanGoNext()
        }

        fun changeFirstName(firstName: String) {
            registration.value = registration.value.copy(firstName = firstName)
            checkCanGoNext()
        }

        fun changeLastName(lastName: String) {
            registration.value = registration.value.copy(lastName = lastName)
            checkCanGoNext()
        }

        fun changeEmail(email: String) {
            registration.value = registration.value.copy(email = email)
            checkCanGoNext()
        }

        fun changePhoneNumber(phoneNumber: String) {
            registration.value = registration.value.copy(phoneNumber = phoneNumber)
            checkCanGoNext()
        }

        fun changePassword(password: String) {
            registration.value = registration.value.copy(password = password)
            checkCanGoNext()
        }

        fun changeRepeatPassword(repeatPassword: String) {
            registration.value = registration.value.copy(verifyPassword = repeatPassword)
            checkCanGoNext()
        }

        /**
         * Transition to [AddressData] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                        && phoneNumber.isNotEmpty() && password.isNotEmpty()
                        && verifyPassword.isNotEmpty() && verifyPassword == password
            }
        }
    }

    data class AddressData(
        internal val registrationStep1: UserRegistration1,
        val locationData: DataSource<Location?>,
        val registration: DataSource<UserRegistration2>,
        val validation: DataSource<UserValidation2?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        init {
            checkCanGoNext()
        }

        /**
         * Updates [locationData] with 1s debounce
         */
        fun changePincode(pincode: String) {
            if (pincode.length <= 6) {
                EventCollector.sendEvent(Event.Action.Registration.UpdatePincode(pincode))
            }
        }

        fun changeAddressLine(address: String) {
            registration.value = registration.value.copy(addressLine1 = address)
            checkCanGoNext()
        }

        fun changeLocation(location: String) {
            registration.value = registration.value.copy(location = location)
            checkCanGoNext()
        }

        fun changeCity(city: String) {
            registration.value = registration.value.copy(city = city)
            checkCanGoNext()
        }

        /**
         * Transition to [TraderData] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration2) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                pincode.length == 6 && addressLine1.isNotEmpty() && location.isNotEmpty()
                        && city.isNotEmpty() && district.isNotEmpty() && state.isNotEmpty()
            }
        }
    }

    data class TraderData(
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        val registration: DataSource<UserRegistration3>,
        val validation: DataSource<UserValidation3?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        val isPanValid: Boolean
            get() = registration.value.panNumber.isEmpty() || PAN_REGEX.matches(registration.value.panNumber)

        val isGstinValid: Boolean
            get() = registration.value.gstin.isEmpty() || GSTIN_REGEX.matches(registration.value.gstin)

        init {
            checkCanGoNext()
        }

        fun changeTradeName(tradeName: String) {
            registration.value = registration.value.copy(tradeName = tradeName)
            checkCanGoNext()
        }

        fun changeGstin(gstin: String) {
            if (gstin.length <= 15) {
                registration.value = registration.value.copy(gstin = gstin)
                checkCanGoNext()
            }
        }

        fun changePan(panNumber: String) {
            if (panNumber.length <= 10) {
                registration.value = registration.value.copy(panNumber = panNumber)
                checkCanGoNext()
            }
        }

        fun changeDrugLicense1(drugLicenseNo: String) {
            if (drugLicenseNo.length <= 30) {
                registration.value = registration.value.copy(drugLicenseNo1 = drugLicenseNo)
                checkCanGoNext()
            }
        }

        fun changeDrugLicense2(drugLicenseNo: String) {
            if (drugLicenseNo.length <= 30) {
                registration.value = registration.value.copy(drugLicenseNo2 = drugLicenseNo)
                checkCanGoNext()
            }
        }

        /**
         * Transition to [] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration3) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(userRegistration))

        override fun checkCanGoNext() {
            canGoNext.value = registration.value.run {
                tradeName.isNotEmpty() && (gstin.isNotEmpty() || panNumber.isNotEmpty())
                        && drugLicenseNo1.isNotEmpty() && drugLicenseNo2.isNotEmpty()
            }
        }

        companion object {
            private val PAN_REGEX = Regex("^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?\$")
            private val GSTIN_REGEX =
                Regex("([0][1-9]|[1-2][0-9]|[3][0-7])([A-Z]{5})([0-9]{4})([A-Z]{1}[1-9A-Z]{1})([Z]{1})([0-9A-Z]{1})+")
        }
    }
}