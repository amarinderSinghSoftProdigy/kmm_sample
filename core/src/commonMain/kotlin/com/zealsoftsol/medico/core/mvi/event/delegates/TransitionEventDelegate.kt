package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.OtpScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SettingsScope
import com.zealsoftsol.medico.core.mvi.scope.nested.SignUpScope
import com.zealsoftsol.medico.core.mvi.scope.regular.SearchScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.core.repository.requireUser
import com.zealsoftsol.medico.data.User
import com.zealsoftsol.medico.data.UserType

internal class TransitionEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Transition>(navigator) {

    override suspend fun handleEvent(event: Event.Transition) {
        navigator.apply {
            when (event) {
                is Event.Transition.Back -> dropScope()
                is Event.Transition.ForgetPassword -> setScope(
                    OtpScope.PhoneNumberInput.get(
                        phoneNumber = DataSource(""),
                        isForRegisteredUsersOnly = true,
                    )
                )
                is Event.Transition.SignUp -> setScope(SignUpScope.SelectUserType.get())
                is Event.Transition.Search -> setScope(SearchScope())
                is Event.Transition.Settings -> setScope(
                    SettingsScope.List(
                        if (userRepo.requireUser().type == UserType.SEASON_BOY)
                            SettingsScope.List.Section.simple()
                        else
                            SettingsScope.List.Section.all()
                    )
                )
                is Event.Transition.Profile -> setScope(
                    SettingsScope.Profile(userRepo.requireUser())
                )
                is Event.Transition.Address -> setScope(
                    SettingsScope.Address(userRepo.requireUser().addressData)
                )
                is Event.Transition.GstinDetails -> setScope(
                    SettingsScope.GstinDetails(userRepo.requireUser().details as User.Details.DrugLicense)
                )
            }
        }
    }
}