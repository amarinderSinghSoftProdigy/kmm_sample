package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.SearchScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope

internal class TransitionEventDelegate(
    navigator: Navigator,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        when (event) {
            is Event.Transition.Back -> navigator.dropCurrentScope()
            is Event.Transition.ForgetPassword -> navigator.setCurrentScope(
                OtpScope.PhoneNumberInput(
                    phoneNumber = DataSource(""),
                    isForRegisteredUsersOnly = true,
                )
            )
            is Event.Transition.SignUp -> navigator.setCurrentScope(
                SignUpScope.SelectUserType()
            )
            is Event.Transition.Search -> navigator.setCurrentScope(
                SearchScope.Query()
            )
        }
    }
}