package com.zealsoftsol.medico.core.mvi.scope

import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.event.EventCollector
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserType

sealed class SignUpScope : BaseScope(), CanGoBack {

    data class SelectUserType(
        val userType: UserType = UserType.STOCKIST,
    ) : SignUpScope() {
        override val isInProgress: Boolean
            get() = false

        /**
         * Transition to [PersonalData]
         */
        fun goToPersonalData(userType: UserType) =
            EventCollector.sendEvent(Event.Action.Registration.SelectUserType(userType))
    }

    data class PersonalData(
        val registration1: UserRegistration1,
        internal val userType: UserType,
        override val isInProgress: Boolean = false,
    ) : SignUpScope() {

        /**
         * Transition to [AddressData] if successful
         */
        fun tryToSignUp(userRegistration: UserRegistration1) =
            EventCollector.sendEvent(Event.Action.Registration.SignUp(registration1))
    }
}