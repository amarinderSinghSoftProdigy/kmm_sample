package com.zealsoftsol.medico.core.mvi.event.delegates

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.Navigator
import com.zealsoftsol.medico.core.mvi.event.Event
import com.zealsoftsol.medico.core.mvi.scope.nested.AddEmployeeScope
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.UserRegistration1

internal class AddEmployeeEventDelegate(
    navigator: Navigator,
    private val userRepo: UserRepo,
) : EventDelegate<Event.Action.AddEmployee>(navigator) {

    override suspend fun handleEvent(event: Event.Action.AddEmployee) =
        when (event) {
            is Event.Action.AddEmployee.SelectUserType -> moveToPersonalDetailsScreen()
        }

    private fun moveToPersonalDetailsScreen() {
        navigator.withScope<AddEmployeeScope> {
            setScope(
                AddEmployeeScope.PersonalData(
                    registration = DataSource(
                        UserRegistration1(
                            userType = it.selectedUserType.value!!.serverValue,
                        )
                    ),
                    validation = DataSource(null),
                )
            )
        }
    }

}