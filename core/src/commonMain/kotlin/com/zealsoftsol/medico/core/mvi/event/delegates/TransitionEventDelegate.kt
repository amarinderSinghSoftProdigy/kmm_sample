package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.ForgetPasswordScope
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope

internal class TransitionEventDelegate(
    navigator: Navigator,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        when (event) {
            is Event.Transition.Back -> navigator.dropCurrentScope()
            is Event.Transition.ForgetPassword -> navigator.setCurrentScope(
                ForgetPasswordScope.PhoneNumberInput(phoneNumber = "")
            )
            is Event.Transition.SignUp -> navigator.setCurrentScope(
                SignUpScope.SelectUserType()
            )
        }
    }
}