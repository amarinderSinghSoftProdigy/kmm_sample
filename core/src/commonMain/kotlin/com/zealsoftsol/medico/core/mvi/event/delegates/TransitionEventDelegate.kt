package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope

internal class TransitionEventDelegate(
    navigator: Navigator,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        when (event) {
            is Event.Transition.Back -> navigator.dropScope()
            is Event.Transition.ForgetPassword -> navigator.setScope(
                OtpScope.PhoneNumberInput.get(
                    phoneNumber = DataSource(""),
                    isForRegisteredUsersOnly = true,
                )
            )
            is Event.Transition.SignUp -> navigator.setScope(
                SignUpScope.SelectUserType.get()
            )
            is Event.Transition.Search -> navigator.setScope(
                SearchScope()
            )
        }
    }
}