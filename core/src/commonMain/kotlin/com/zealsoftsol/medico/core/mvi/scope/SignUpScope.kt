package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserRegistration2
import com.zealsoftsol.medico.data.UserRegistration3
import com.zealsoftsol.medico.data.UserType
import com.zealsoftsol.medico.data.UserValidation1
import com.zealsoftsol.medico.data.UserValidation2
import com.zealsoftsol.medico.data.UserValidation3

sealed class SignUpScope : BaseScope(), CanGoBack {

    data class SelectUserType(
        val userType: DataSource<UserType> = DataSource(UserType.STOCKIST),
    ) : SignUpScope() {
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
    }

    data class PersonalData(
        val registration: DataSource<UserRegistration1>,
        val validation: DataSource<UserValidation1?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        val isRegistrationValid: Boolean
            get() = registration.value.run {
                firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()
                        && phoneNumber.isNotEmpty() && password.isNotEmpty()
                        && verifyPassword.isNotEmpty() && verifyPassword == password
            }

        fun changeFirstName(firstName: String) {
            registration.value = registration.value.copy(firstName = firstName)
        }

        fun changeLastName(lastName: String) {
            registration.value = registration.value.copy(lastName = lastName)
        }

        fun changeEmail(email: String) {
            registration.value = registration.value.copy(email = email)
        }

        fun changePhoneNumber(phoneNumber: String) {
            registration.value = registration.value.copy(phoneNumber = phoneNumber)
        }

        fun changePassword(password: String) {
            registration.value = registration.value.copy(password = password)
        }

        fun changeRepeatPassword(repeatPassword: String) {
            registration.value = registration.value.copy(verifyPassword = repeatPassword)
        }

        /**
         * Transition to [AddressData] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(userRegistration))
    }

    data class AddressData(
        internal val registrationStep1: UserRegistration1,
        val registration: DataSource<UserRegistration2>,
        val validation: DataSource<UserValidation2?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        /**
         * Transition to [TraderData] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration2) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(userRegistration))
    }

    data class TraderData(
        internal val registrationStep1: UserRegistration1,
        internal val registrationStep2: UserRegistration2,
        val registration: DataSource<UserRegistration3>,
        val validation: DataSource<UserValidation3?>,
        override val isInProgress: Boolean = false,
    ) : SignUpScope()
}