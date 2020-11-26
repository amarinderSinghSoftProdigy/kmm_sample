package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.SignUpScope
import com.zealsoftsol.medico.core.mvi.viewmodel.AuthViewModel
import com.zealsoftsol.medico.core.mvi.withProgress
import com.zealsoftsol.medico.data.UserRegistration
import com.zealsoftsol.medico.data.UserRegistration1
import com.zealsoftsol.medico.data.UserType

internal class RegistrationEventDelegate(
    navigator: Navigator,
    private val authViewModel: AuthViewModel,
) : EventDelegate<Event.Action.Registration>(navigator) {

    override suspend fun handleEvent(event: Event.Action.Registration) = when (event) {
        is Event.Action.Registration.SelectUserType -> selectUserType(event.userType)
        is Event.Action.Registration.SignUp -> trySignUp(event.userRegistration)
    }

    private fun selectUserType(userType: UserType) {
        navigator.withScope<SignUpScope.SelectUserType> {
            setCurrentScope(it.copy(userType = userType), updateDataSource = false)
            transitionTo(
                SignUpScope.PersonalData(
                    userType = userType,
                    registration1 = UserRegistration1()
                )
            )
        }
    }

    private suspend fun trySignUp(userRegistration: UserRegistration) {
        navigator.withScope<SignUpScope.PersonalData> {
            val validation = withProgress {
                authViewModel.trySignUp(userRegistration)
            }
        }
    }
}